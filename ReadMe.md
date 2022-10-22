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

There's nothing fancy about the model

Let's start with building a simple Service

A simple handler code

There's not much in the model zone for this

Here's a router

Wait how about errors?

And that's it. In my future articles, let's connect it to a datastore like Cassandra and see how it works?

As always thanks for your time. Stay well
