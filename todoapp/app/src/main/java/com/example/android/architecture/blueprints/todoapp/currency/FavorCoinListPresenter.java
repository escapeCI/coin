package com.example.android.architecture.blueprints.todoapp.currency;

import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsDataSource;

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
                if(!mFragment.isActive())
                    return;
                if(showLoadingUI)
                    mFragment.setLoadingIndicator(false);
                if( Coins.isEmpty()) {
                    onDataNotAvailable();
                }
                else {
                    mFragment.showAllFavorCoins(Coins);
                }

            }

            @Override
            public void onDataNotAvailable() {
                Log.v("tinyhhj", ""+"onDataNotAvailable");
            }
        });
    }

    @Override
    public void addNewFavorCoin(int from, int to) {

    }
}
