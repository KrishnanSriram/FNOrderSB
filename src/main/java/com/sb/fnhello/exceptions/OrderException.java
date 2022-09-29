package com.sb.fnhello.exceptions;

public class OrderException extends RuntimeException{
    public OrderException(Long id) {
        super("Order - " + id +", is not found.");
    }
}