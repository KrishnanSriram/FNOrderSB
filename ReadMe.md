#Reactive Webflux
Reactive programming has been quite popular in recent times and for a good reason. With Webflux we can now have event loop kind of an execution in Springboot world
###What is Reactive stream?
It's an initiative to provide a standard for asynchronous stream processing with non-blocking back pressure.
There are 4 important concepts to program on Reactive streams
- Publisher
- Subscriber
- Subscription
- Processor

###What is Reactor?
Reactor is the default Reactive stream implmentation supported in Spring Webflux. There are couple of reactive types we should be familiar with
- Mono
- Flux

####What Spring webflux brings to the table?
The Spring embraces Reactive Streams in the new 5.x era

For Spring developers, it brings a complete new programming model.

- Spring added a new spring-webflux module in it is core framework, and provided built-in reactive programming support via Reactor and RxJava 2/3(RxJava 1 support is removed in the latest Spring 5.3).
- Spring Security 5 also added reactive feature.
- Spring supports RSocket which a new bi-direction messaging protocol.
- In Spring Data umbrella projects, a new ReactiveSortingRepository interface is added in Spring Data Commons. Redis, Mongo, Cassandra subprojects firstly got reactive supports. For RDBMS, Spring created R2dbc sepc and R2dbc is part of Spring since 5.3.
- Spring Session also began to add reactive features, an reactive variant for its SessionRepository is included since 2.0.
- Spring Integration added flux message channel and reactive programming APIs.
