# Reactive Webflux

Reactive programming has been quite popular in recent times and for a good reason. With Webflux we can now have event loop kind of an execution in Springboot world.
Spring framework 5, introduced reactive web framework in the form of webflux. This can reside alongside spring web mvc framework.
Java developers have used thread and threadpools to achieve asynchronicity in the past. Although it worked to some extent, it does not solve the scalability needs of today. EventLoop implementation in NodeJS offered better approach for this problem. The way WebFlux approaches this problem is running few fixed number of threads togather with sockets to process data in parts, as opposed to multiple threads cosuming a gobs of data. This approach also puts functional programming in the front-seat.
In short the fundamental difference between spring-mvc and webflux is former is thread pool based and later is event-loop based. Although this capability existed in Spring framework even before NodeJS, it was dormant until NodeJS established as a pattern for microservices.
Benefit of such approch shows in the form of Webflux support

- Serverlet containers like Netty, Tomcat
- Non serverlet runtimes like Jetty and Undertow
  All it means is, in Webflux serverlet API enables support for serverlet based containers as well as non-serverlet based containers. In webflux both webfilter and handler are non-blocking

### What is Reactive stream?

It's an initiative to provide a standard for asynchronous stream processing with non-blocking back pressure.
There are 4 important concepts to program on Reactive streams

- Publisher
- Subscriber
- Subscription
- Processor

### What is Reactor?

Reactor is the default Reactive stream implmentation supported in Spring Webflux. There are couple of reactive types we should be familiar with

- Mono
- Flux

#### What Spring webflux brings to the table?

The Spring embraces Reactive Streams in the new 5.x era

For Spring developers, it brings a complete new programming model.

- Spring added a new spring-webflux module in it is core framework, and provided built-in reactive programming support via Reactor and RxJava 2/3(RxJava 1 support is removed in the latest Spring 5.3).
- Spring Security 5 also added reactive feature.
- Spring supports RSocket which a new bi-direction messaging protocol.
- In Spring Data umbrella projects, a new ReactiveSortingRepository interface is added in Spring Data Commons. Redis, Mongo, Cassandra subprojects firstly got reactive supports. For RDBMS, Spring created R2dbc sepc and R2dbc is part of Spring since 5.3.
- Spring Session also began to add reactive features, an reactive variant for its SessionRepository is included since 2.0.
- Spring Integration added flux message channel and reactive programming APIs.

What better way to experiece this than build a Springboot Webflux application that works. All code in this article can be found <<here>>

Start by choosing these libraries
<<Image>>

#### Model

Let's build a model class for us to operate on. For simplicity sake, we'll not have any DB

```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private long orderId;
    private String item;
    private int quantity;
    private float price;
}
```

As you can see, there's nothing fancy about the model. Let's move on to service and it's operations.

#### Service

Objective of this service is to persist order data in a local array and present it when asked for. Certianly not rocket science. What you'll see though is how different the approach and how asynchronicity is deeply embeded into the system and the responsibility you as a developer carry to achieve it.

```
@Service
public class OrderService {
    private Logger logger = LoggerFactory.getLogger(OrderService.class);
    private List<Order> orders;

    @PostConstruct
    public void init() {
        logger.info("Initialize orders");
        orders = new ArrayList<Order>();
        Order[] initialOrders = {
                new Order(1L, "Bat", 4, 400.0f),
                new Order(3L, "Ball", 10, 25F)
        };
        orders.addAll(Arrays.asList(initialOrders));
    }

    public Flux<Order> getAllOrders() {
        logger.info("getAllOrders");
        return Flux.fromIterable(orders);
    }

    public Mono<Order> getOrderById(long orderId) {
        logger.info("getOrderById - " + orderId);
        Flux<Order> fOrder = Flux.fromIterable(orders);
        return fOrder.filter(ord -> ord.getOrderId() == orderId)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(CreateException(InvalidOrderFetch, String.valueOf(orderId))));
    }

    public Mono<Order> addOrder(Order newOrder) {
        logger.info("addOrder - " + newOrder.getItem());
        orders.add(newOrder);
        return Mono.just(newOrder);
    }

}
```

OrderService as one would expect is annotated service class. We have a post construct to load some initial data into order. As you look further down, gteAllOrder has Flux return type. In simple terms, we use Flux for iterable data and Mono for single instance of data. You can read more about flux and mono here. Back to getAllOrders, I convert a simple list to an iterable Flux object and return. Next, to locate a single Order, we have getOrderById. In this case, as stated earlier, we'll return a single Order object, hence Mono. We have more than one case to comprehend. We may either find the order we are looking for or the order may not exist. In case we find the order, we reuturn it. Else, we raise an exception. Take a look at how we raise exception as a part of this function. You'll also notice, we don't have throws at the function level. Finally, addOrder, accepts order object and adds it to the list.
As we mature this code with datastore, we'll introduce DTO's and the conversion too. However, nothing will change from the current working model, though.

#### Handler

In the past a Controller will talk to service. We can do that, but a better approach with reactive style is to build a Handler

```
@Component
public class OrderHandler {
    private Logger logger = LoggerFactory.getLogger(OrderHandler.class);
    @Autowired
    private OrderService orderService;

//    @Autowired
    private OrderValidator validator = new OrderValidator();

    public Mono<ServerResponse> getAllOrders(ServerRequest serverRequest) {
        logger.info("getAllOrders");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderService.getAllOrders(), Order.class);
    }

    public Mono<ServerResponse> getOrderById(ServerRequest serverRequest) {
        var orderId = serverRequest.pathVariable("orderId");
        logger.info("getOrderById - " +  orderId);

        var order = orderService.getOrderById(Long.valueOf(serverRequest.pathVariable("orderId")));
        logger.info("Fetched order....");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(order, Order.class)
                .onErrorResume(e -> orderError(e.getMessage())
                    .flatMap(oe -> ServerResponse.status(HttpStatus.NOT_FOUND)
                                        .body(oe, OrderError.class)));
    }

    public Mono<ServerResponse> addOrder(ServerRequest request) {
        Mono<Order> order = request.bodyToMono(Order.class).doOnNext(this::validateAddOrder);
        return order.flatMap(ord -> orderService.addOrder(ord))
                .flatMap(ord -> ServerResponse.created(URI.create("/orders/" + ord.getOrderId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(ord)));
    }

    private Mono<OrderError> orderError(String message) {
        return Mono.just(new OrderError(message));
    }

    private void validateAddOrder(Order order) {
        Errors errors = new BeanPropertyBindingResult(order, "order");
        validator.validate(order, errors);
        if(errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

}
```

There's not much in the model zone for this

Here's a router

Wait how about errors?

And that's it. In my future articles, let's connect it to a datastore like Cassandra and see how it works?

As always thanks for your time. Stay well
