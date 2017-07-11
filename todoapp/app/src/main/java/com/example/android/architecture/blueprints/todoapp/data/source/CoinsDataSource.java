package com.example.android.architecture.blueprints.todoapp.data.source;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Task;

import java.util.List;

/**
 * Created by tinyhhj on 2017-07-02.
 */

public interface CoinsDataSource {
    interface LoadCoinsCallback {

        void onCoinsLoaded(List<Coin> Coins);

        void onDataNotAvailable();
    }

    interface GetCoinCallback {

        void onCoinLoaded(Coin coin);

        void onDataNotAvailable();
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

    //For FavorCoinListPresenter
    void getAllFavorCoins(LoadCoinsCallback callback);
    void refreshAllFavorCoins();
    void addNewFavorCoin(String coinName , String exName);

}
