package com.sb.fnhello.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private long orderId;
    private String item;
    private int quantity;
    private float price;
}
