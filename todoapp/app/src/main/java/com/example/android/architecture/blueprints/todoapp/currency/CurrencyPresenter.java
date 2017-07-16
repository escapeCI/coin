package com.example.android.architecture.blueprints.todoapp.currency;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsDataSource;

import java.util.List;

/**
 * Created by tinyhhj on 2017-06-20.
 */

public class CurrencyPresenter implements CurrencyContract.Presenter {
    private CurrencyContract.View mFragment;
    private CoinsDataSource mSource;
    private boolean mFirstLoad = true;

    public CurrencyPresenter(CurrencyFragment fragment , CoinsDataSource repo)
    {
        mFragment = fragment;
        mFragment.setPresenter(this);
        mSource = repo;
    }
    @Override
    public void start() {

        loadFavorCoins(false || mFirstLoad , true);
        mFirstLoad = false;
    }

    private void loadFavorCoins(boolean forceUpdate  , final boolean showLoadingUI)
    {
        if(showLoadingUI)
        {
            mFragment.setLoadingIndicator(true);
        }
        if(forceUpdate)
        {
            mSource.refreshCoins();
        }

        mSource.getFavorCoins(new CoinsDataSource.LoadCoinsCallback() {

            @Override
            public void onCoinsLoaded(List<Coin> Coins) {
                if(!mFragment.isActive())
                    return;
                if(showLoadingUI)
                    mFragment.setLoadingIndicator(false);
                if( Coins.isEmpty())
                {
                    //empty view
                }
                else
                {
                    //show list
                    for (Coin c : Coins)
                        Log.v("tinyhhj" , "priceinfo : " +c.getPriceInfo().getCurPrice() + " " + c.getPriceInfo().getPrevPrice());
                    mFragment.showFavorCoins(Coins);
                }

            }

            @Override
            public void onDataNotAvailable() {

            }
        });

    }

    public void changeFragment(int from , int to) {
        //create new fragment for all favorcoin list
        mFragment.showAllFavorFragment(from , to);
    }

    @Override
    public void removeFavorCoin(Coin c) {

    }

    @Override
    public void removeFavorCoin(String coinName, String exName) {

    }

    @Override
    public void addNewFavorCoin(Coin c) {

    }

    @Override
    public void addNewFavorCoin(String coinName, String exName) {

    }

}
