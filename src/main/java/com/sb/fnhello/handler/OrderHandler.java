package com.sb.fnhello.handler;

import com.sb.fnhello.exceptions.OrderException;
import com.sb.fnhello.model.Order;
import com.sb.fnhello.model.OrderError;
import com.sb.fnhello.service.OrderService;
import com.sb.fnhello.validator.OrderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

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
//                .delayElement(Duration.ofSeconds(1))
//                .log();
    }

    @Bean
    public WebExceptionHandler exceptionHandler() {
        logger.info("Invoked exception...............");
        return (ServerWebExchange exchange, Throwable ex) -> {
            if (ex instanceof OrderException) {
                logger.info("OrderException fired and handled");
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().setComplete();
            } else if(ex instanceof NoSuchElementException) {
                logger.info("NoSuchElementException fired and handled");
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().setComplete();
            }
            return Mono.error(ex);
        };
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
