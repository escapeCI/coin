package com.example.android.architecture.blueprints.todoapp.data.source;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsPersistenceContract;

import java.util.List;

/**
 * Created by tinyhhj on 2017-07-02.
 */

public interface CoinsDataSource {

    interface LoadDataCallback<T> {
        void onDataLoaded (List<T> list);
        void noDataAvailable();
        void onDataError();
    }


    interface LoadCoinsCallback {

        void onCoinsLoaded(List<Coin> Coins);

        void onDataNotAvailable();

        void onError();
    }

    interface GetCoinCallback {

        void onCoinLoaded(Coin coin);

        void onDataNotAvailable();

        void onError();
    }

    void getCoins(LoadCoinsCallback callback );
    void getCoin(String name , String exName , GetCoinCallback callback );
    void saveCoin(Coin c);
    void setPriceInfo(Coin c);
    void setPriceInfo(String name , String exName );


    void deleteAllCoins();
    void deleteCoin(String name , String exName);

    //For CurrencyPresenter
    void refreshCoins();
    void getFavorCoins(LoadCoinsCallback callback);
    void getOrderInfos(Coin c , LoadDataCallback callback);

    //For FavorCoinListPresenter
    void getAllFavorCoins(LoadCoinsCallback callback);
    void refreshAllFavorCoins();
    void setCoinStatus(String coinName , String exName , CoinsPersistenceContract.Favor favor);
    void saveNewFavorCoin(String coinName, String exName);
    void removeFavorCoin(String coinName , String exName);

}
