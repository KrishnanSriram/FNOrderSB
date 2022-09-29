package com.sb.fnhello.router;

import com.sb.fnhello.handler.OrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class OrderRouter {
    @Autowired
    private OrderHandler orderHandler;

    @Bean
    public RouterFunction<ServerResponse> orderRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/api/orders"), orderHandler::getAllOrders)
                .andRoute(RequestPredicates.GET("/api/orders/{orderId}"), orderHandler::getOrderById)
                .andRoute(RequestPredicates.POST("/api/orders"), orderHandler::addOrder);
    }
}
