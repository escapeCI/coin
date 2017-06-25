package com.example.android.architecture.blueprints.todoapp.data;

/**
 * Created by tinyhhj on 2017-06-25.
 */

public class PriceInfo {
    private double mCurPrice;               // 현재가
    private double mAvgPrice24h;           // 전일 24시간 평균가격
    private String  mCurrency;              // KRW : 원 , USD : 달러

    public PriceInfo()
    {
        this(0.0 , 0.0 , "KRW");
    }
    public PriceInfo(double curPrice , double avgPrice24h)
    {
        this(curPrice, avgPrice24h , "KRW");
    }

    public PriceInfo(double curPrice , double avgPrice24h , String currency)
    {
        mCurPrice = curPrice;
        mAvgPrice24h = avgPrice24h;
        mCurrency = currency;
    }

    public double getCurPrice() {
        return mCurPrice;
    }

    public double getAvgPrice24h() {
        return mAvgPrice24h;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurPrice(double mCurPrice) {
        this.mCurPrice = mCurPrice;
    }

    public void setAvgPrice24h(double mAvgPrice24h) {
        this.mAvgPrice24h = mAvgPrice24h;
    }

    public void setCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }
}
