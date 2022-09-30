package com.sb.fnhello.exceptions;

public class OrderException extends RuntimeException{
    public enum OrderExceptionType {
        InvalidOrderFetch,
        NewOrderFail,
        UpdateOrderFail,
        Others
    }
    public OrderExceptionType exceptionType;
    public OrderException(String message) {
        super(message);
    }

    public static OrderException CreateException(OrderExceptionType exceptionType, String message) {
        String errorMessage = "";
        switch (exceptionType) {
            case NewOrderFail:
                errorMessage = "Order may be missing some required field. Please check and try again!!";
                break;
            case UpdateOrderFail:
                errorMessage = "Order may be missing some required field. Please check and try again!!";
                break;
            case InvalidOrderFetch:
                errorMessage = "Order ID you are looking for is not found. Please check the ID or try again later - ";
                break;
            default:
                errorMessage = "Random error!!";
                break;
        }
        errorMessage += message;
        return new OrderException(errorMessage);
    }
}