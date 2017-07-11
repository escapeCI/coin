package com.example.android.architecture.blueprints.todoapp.data;

import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsPersistenceContract;

/**
 * Created by tinyhhj on 2017-06-25.
 */

public class Coin {
    private String mName;
    private Exchange mExchange;
    private PriceInfo mPriceInfo;
    private CoinsPersistenceContract.Favor mFavor;


    public Coin(String name , Exchange exchange)
    {
        this(name,exchange,new PriceInfo() );
    }

    public Coin (String name , Exchange exchange , PriceInfo priceInfo )
    {
        mName = name;
        mExchange = exchange;
        mPriceInfo = priceInfo;
        mFavor = CoinsPersistenceContract.Favor.NOT_FAVOR;
    }

    public String getName() { return mName; }
    public Exchange getExchange() { return mExchange; }
    public PriceInfo getPriceInfo() { return mPriceInfo; }
    public void setPriceInfo(PriceInfo p) { mPriceInfo = p ;}
    public void setFavor(CoinsPersistenceContract.Favor f) {mFavor = f;}
    public CoinsPersistenceContract.Favor getFavor() {return mFavor;}


}
