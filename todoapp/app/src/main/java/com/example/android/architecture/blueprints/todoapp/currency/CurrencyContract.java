package com.example.android.architecture.blueprints.todoapp.currency;

import com.example.android.architecture.blueprints.todoapp.BasePresenter;
import com.example.android.architecture.blueprints.todoapp.BaseView;
import com.example.android.architecture.blueprints.todoapp.data.Coin;

import java.util.List;

/**
 * Created by tinyhhj on 2017-06-20.
 */

public interface CurrencyContract {
    interface View extends BaseView<Presenter>
    {
        //For Both
        void setLoadingIndicator(boolean active);
        boolean isActive();
        //For CurrencyFragment
        void showFavorCoins(List<Coin> coins);
        void showAllFavorFragment(int from , int to) ;
        void showNoFavorCoins();
        //For FavorCoinListFragment
        void showAllFavorExchanges(List<Coin> coins);


    }

    interface Presenter extends BasePresenter
    {
        //For CurrencyPresenter
        void changeFragment(int from , int to);
        void setSortType(CurrencyPresenter.SORT_TYPE st);
        CurrencyPresenter.SORT_TYPE getSortType();


        //For FavorCoinsListPresenter
        void removeFavorCoin(Coin c);
        void removeFavorCoin(String coinName, String exName );
        void addNewFavorCoin(String coinName, String exName );
        void addNewFavorCoin(Coin c);
        void refreshFavorCoinList();

    }

    interface CoinItemListener {
        void onCoinClick(Coin c) ;
    }

}
