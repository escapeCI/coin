package com.example.android.architecture.blueprints.todoapp.data.source.local;



/**
 * Created by tinyhhj on 2017-07-02.
 */

public class CoinsPersistenceContract {

    private CoinsPersistenceContract(){}

    public enum Favor {
        NOT_FAVOR ,
        FAVOR
    }
    public static class CoinEntry  {
        public static final String TABLE_NAME = "coin";
        public static final String COIN_NAME = "coinName";
        public static final String COIN_EXCHANGE_NAME = "coinExchangeName";
        public static final String COIN_FAVORED = "coinFavored";
    }
}
