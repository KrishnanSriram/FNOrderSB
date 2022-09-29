package com.sb.fnhello.service;

import com.sb.fnhello.exceptions.OrderException;
import com.sb.fnhello.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
                new Order(2L, "Ball", 10, 25F)
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
                .switchIfEmpty(Mono.error(new OrderException(orderId)));
    }

    public Mono<Order> addOrder(Order newOrder) {
        logger.info("addOrder - " + newOrder.getItem());
        orders.add(newOrder);
        return Mono.just(newOrder);
    }

}
