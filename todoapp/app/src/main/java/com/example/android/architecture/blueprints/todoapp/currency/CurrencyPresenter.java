package com.example.android.architecture.blueprints.todoapp.currency;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsDataSource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tinyhhj on 2017-06-20.
 */

public class CurrencyPresenter implements CurrencyContract.Presenter {
    private CurrencyContract.View mFragment;
    private CoinsDataSource mSource;
    private boolean mFirstLoad = true;
    private SORT_TYPE st =  SORT_TYPE.NONE;
    public enum SORT_TYPE
    {
            NONE
        ,   PRICE_ASC
        ,   PRICE_DESC
        ,   EXNAME_ASC
        ,   EXNAME_DESC
        ,   CNAME_ASC
        ,   CNAME_DESC
    }

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
                    mFragment.showNoFavorCoins();
                }
                else
                {
                    sortCoins(Coins);
                    mFragment.showFavorCoins(Coins);
                }

            }

            @Override
            public void onDataNotAvailable() {
                if(!mFragment.isActive())
                    return;
                if(showLoadingUI)
                    mFragment.setLoadingIndicator(false);
                Log.v("tinyhhj","CurrencyPresenter getFavorCoins onDataNotAvailable");
                mFragment.showNoFavorCoins();
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
    public void refreshFavorCoinList() {

    }

    @Override
    public void addNewFavorCoin(String coinName, String exName) {

    }

    public void setSortType(SORT_TYPE st) {
        this.st = st;
        start();
    }

    @Override
    public SORT_TYPE getSortType() {
        return st;
    }

    private void sortCoins(List<Coin> lists)
    {
        Collections.sort(lists , new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                switch(st)
                {
                    case PRICE_ASC:
                        return (int) (o1.getPriceInfo().getCurPrice() - o2.getPriceInfo().getCurPrice());
                    case PRICE_DESC:
                        return (int) -(o1.getPriceInfo().getCurPrice() - o2.getPriceInfo().getCurPrice());
                    case CNAME_ASC:
                        return o1.getName().compareTo(o2.getName());
                    case CNAME_DESC:
                        return o2.getName().compareTo(o1.getName());
                    case EXNAME_ASC:
                        return o1.getExchange().getName().compareTo(o2.getExchange().getName());
                    case EXNAME_DESC:
                        return o2.getExchange().getName().compareTo(o1.getExchange().getName());
                    default:
                        return 0;
                }

            }
        });
    }

}
