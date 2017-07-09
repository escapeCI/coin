package com.example.android.architecture.blueprints.todoapp.data;

/**
 * Created by tinyhhj on 2017-06-25.
 */

public class Coin {
    private String mName;
    private Exchange mExchange;
    private PriceInfo mPriceInfo;


    public Coin(String name , Exchange exchange)
    {
        this(name,exchange,new PriceInfo() );
    }

    public Coin (String name , Exchange exchange , PriceInfo priceInfo )
    {
        mName = name;
        mExchange = exchange;
        mPriceInfo = priceInfo;
    }

    public String getName() { return mName; }
    public Exchange getExchange() { return mExchange; }
    public PriceInfo getPriceInfo() { return mPriceInfo; }
    public void setPriceInfo(PriceInfo p) { mPriceInfo = p ;}


}
