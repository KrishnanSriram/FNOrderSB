package com.sb.fnhello.handler;

import com.sb.fnhello.model.Order;
import com.sb.fnhello.model.OrderError;
import com.sb.fnhello.service.OrderService;
import com.sb.fnhello.validator.OrderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import java.net.URI;

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
