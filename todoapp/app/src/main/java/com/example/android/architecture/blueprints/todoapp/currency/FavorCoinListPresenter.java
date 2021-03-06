package com.example.android.architecture.blueprints.todoapp.currency;

import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsPersistenceContract;

import java.util.List;

/**
 * Created by tinyhhj on 2017-07-09.
 */

public class FavorCoinListPresenter implements CurrencyContract.Presenter{
    private CurrencyContract.View mFragment;
    private CoinsDataSource mSource;
    private boolean mFirstLoad = true;

    public FavorCoinListPresenter(CurrencyContract.View fragment , CoinsDataSource repo) {
        mFragment = fragment;
        mFragment.setPresenter(this);
        mSource = repo;
    }
    @Override
    public void start() {
        Log.v("tinyhhj" , "FavorCoinListPresenter start() mFirstLoad : " + mFirstLoad );
        loadAllFavorCoins(false||mFirstLoad , true);
    }
    private void loadAllFavorCoins(boolean forceUpdate , final boolean showLoadingUI)
    {
        if(showLoadingUI)
            mFragment.setLoadingIndicator(showLoadingUI);
        if(forceUpdate)
            mSource.refreshAllFavorCoins();

        mSource.getAllFavorCoins(new CoinsDataSource.LoadCoinsCallback() {
            @Override
            public void onCoinsLoaded(List<Coin> Coins) {
//                if(!mFragment.isActive())
//                    return;
                if(showLoadingUI)
                    mFragment.setLoadingIndicator(false);
                if( Coins.isEmpty()) {
                    onDataNotAvailable();
                }
                else {
                    mFragment.showAllFavorExchanges(Coins);
                }

            }

            @Override
            public void onDataNotAvailable() {
                Log.v("tinyhhj", ""+"onDataNotAvailable");
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void changeFragment(int from, int to) {

    }

    @Override
    public void removeFavorCoin(Coin c) {
        removeFavorCoin(c.getName() , c.getExchange().getName());
    }

    @Override
    public void removeFavorCoin(String coinName, String exName) {
        mSource.removeFavorCoin(coinName, exName);
    }

    @Override
    public void addNewFavorCoin(Coin c) {
        addNewFavorCoin(c.getName() , c.getExchange().getName());
    }

    @Override
    public void refreshFavorCoinList() {
        mSource.refreshCoins();
    }

    @Override
    public void setSortType(CurrencyPresenter.SORT_TYPE st) {

    }

    @Override
    public CurrencyPresenter.SORT_TYPE getSortType() {
        return null;
    }

    @Override
    public void getOrderInfo(Coin c) {

    }

    @Override
    public void addNewFavorCoin(String coinName, String exName) {
        mSource.saveNewFavorCoin(coinName, exName );

    }
}
