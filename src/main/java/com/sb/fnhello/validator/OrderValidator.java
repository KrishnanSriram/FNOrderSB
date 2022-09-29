package com.sb.fnhello.validator;

import com.sb.fnhello.model.Order;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class OrderValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
       return Order.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "item", "item.empty");
        Order ord = (Order) target;
        if (ord.getQuantity() < 0) {
            errors.rejectValue("Quantity", "zero.quantity");
        }
    }
}
