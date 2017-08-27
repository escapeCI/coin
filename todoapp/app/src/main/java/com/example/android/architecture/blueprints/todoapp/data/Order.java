package com.example.android.architecture.blueprints.todoapp.data;

/**
 * Created by tinyhhj on 2017. 8. 26..
 */

public class Order {
    private Order_type order_type;
    private double order_amount;
    private PriceInfo order_price;

    public enum Order_type {
        ASK ,
        BID
    }
    public Order(Order_type o , double amnt , PriceInfo p)
    {
        order_type = o;
        order_amount = amnt;
        order_price = p;
    }

    public Order(Order_type s )
    {
        this(s , 0.0 , new PriceInfo());
    }

    public Order_type getOrder_type() {
        return order_type;
    }

    public double getOrder_amount() {
        return order_amount;
    }

    public PriceInfo getOrder_price() {
        return order_price;
    }

    public void setOrder_type(Order_type order_type) {
        this.order_type = order_type;
    }

    public void setOrder_amount(double order_amount) {
        this.order_amount = order_amount;
    }

    public void setOrder_price(PriceInfo order_price) {
        this.order_price = order_price;
    }
}
