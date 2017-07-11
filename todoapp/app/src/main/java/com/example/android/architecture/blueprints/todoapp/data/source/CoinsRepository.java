package com.example.android.architecture.blueprints.todoapp.data.source;

import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Exchange;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsPersistenceContract;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tinyhhj on 2017-07-02.
 */

public class CoinsRepository implements CoinsDataSource {
    private static CoinsRepository INSTANCE = null;

    private final CoinsDataSource mCoinsRemoteDataSource;
    private final CoinsDataSource mCoinsLocalDataSource;

    Map<String, Coin >  mCachedAllFavorCoins = null;
    Map<String , Coin > mCachedCoins = null;

    boolean mCacheIsDirty = false;
    boolean mAllFavorCacheIsDirty = false;

    private CoinsRepository(CoinsDataSource remote, CoinsDataSource local) {
        mCoinsLocalDataSource = local;
        mCoinsRemoteDataSource = remote;
    }

    public static CoinsRepository getInstance(CoinsDataSource remote, CoinsDataSource local) {
        if (INSTANCE == null)
            INSTANCE = new CoinsRepository(remote, local);
        return INSTANCE;
    }


    void getFavorCoinsFromRemoteSource(final LoadCoinsCallback callback)
    {
        //관심코인 로드
        if( mCacheIsDirty || mCachedCoins == null) {
            mCoinsLocalDataSource.getFavorCoins(new LoadCoinsCallback() {
                @Override
                public void onCoinsLoaded(List<Coin> coins) {

                    refreshCache(coins);
                   // callback.onCoinsLoaded(coins);
                }

                @Override
                public void onDataNotAvailable()
                {
                    mCachedCoins = new LinkedHashMap<String , Coin>();
                    ////////////////////////////////////////////////////////////////////////////////////////////
                    //for test
                    ////////////////////////////////////////////////////////////////////////////////////////////
                    mCachedCoins.put("ETHbithumb" ,  new Coin("eth" ,new Exchange("bithumb")));
                    mCachedCoins.put("BTCbithumb" ,  new Coin("btc" ,new Exchange("bithumb")));
                    mCachedCoins.put("ETHcoinone" ,  new Coin("eth" ,new Exchange("coinone")));
                    mCachedCoins.put("BTCcoinone" ,  new Coin("btc" ,new Exchange("coinone")));
                    //callback.onDataNotAvailable();
                }
            });
        }
        // 가격정보 요청

        HttpConnection httpConnection = new HttpConnection( HttpConnection.url + "index.php" , "POST");
        httpConnection.requestPriceInfo(mCachedCoins , callback);
//        if(mCachedCoins.size() > 0 )
//            callback.onCoinsLoaded(new ArrayList<>(mCachedCoins.values()));
//        else
//            callback.onDataNotAvailable();
    }
    @Override
    public void getCoins(LoadCoinsCallback callback) {

    }

    @Override
    public void getCoin(String name, String exName, GetCoinCallback callback) {

    }

    @Override
    public void saveCoin(Coin c) {

    }

    @Override
    public void setPriceInfo(Coin c) {

    }

    @Override
    public void setPriceInfo(String name, String exName) {

    }

    @Override
    public void refreshCoins() {
        mCacheIsDirty = true;
    }

    @Override
    public void refreshAllFavorCoins() {
        mAllFavorCacheIsDirty = true;
    }

    @Override
    public void addNewFavorCoin(String coinName, String exName) {
        mCoinsLocalDataSource.addNewFavorCoin(coinName, exName);
        // 관심코인 추가한뒤에 리프레시
        refreshCoins();
    }

    @Override
    public void deleteAllCoins() {

    }

    @Override
    public void deleteCoin(String name, String exName) {

    }

    @Override
    public void getFavorCoins(LoadCoinsCallback callback) {
        getFavorCoinsFromRemoteSource(callback);
    }

    @Override
    public void getAllFavorCoins(final LoadCoinsCallback callback) {
        if( mAllFavorCacheIsDirty || mCachedAllFavorCoins == null)
        {
            Log.v("tinyhhj" , "http get method from server");
            HttpConnection httpConnection = new HttpConnection(HttpConnection.url + "possCoin.php","GET");
            final List<Coin> temp = new ArrayList<Coin>(0);
            httpConnection.requestAllFavorCoinList(temp, new LoadCoinsCallback() {
                @Override
                public void onCoinsLoaded(List<Coin> Coins) {
                    for(Coin c : Coins)
                        Log.v("tinyhhj", "keys " + c.getName()+c.getExchange().getName());
                    refreshAllFavorCache(Coins);
                    if (mCachedCoins == null || mCacheIsDirty == true )
                    {
                        mCoinsLocalDataSource.getFavorCoins(new LoadCoinsCallback() {
                            @Override
                            public void onCoinsLoaded(List<Coin> coins) {
                                for(Coin c : coins) {
                                    Coin favorCoin = mCachedAllFavorCoins.get(c.getName() + c.getExchange().getName());
                                    if(favorCoin != null)
                                        favorCoin.setFavor(CoinsPersistenceContract.Favor.FAVOR);
                                }
                            }

                            @Override
                            public void onDataNotAvailable()
                            {

                            }
                        });
                    }
                    //for test

                    //mCachedAllFavorCoins.get("BTCokCoin").setFavor(CoinsPersistenceContract.Favor.FAVOR);
                    //for test
                    Log.v("tinyhhj" , "callback triggered");
                    callback.onCoinsLoaded(new ArrayList<Coin>(mCachedAllFavorCoins.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    //목록조회 불가능할때 정의
                }
            });

        }


    }
    private void refreshAllFavorCache(List<Coin> coins)
    {
        if(mCachedAllFavorCoins == null)
            mCachedAllFavorCoins = new LinkedHashMap<>();
        mCachedAllFavorCoins.clear();
        for( Coin c : coins)
        {
            mCachedAllFavorCoins.put(c.getName()+c.getExchange().getName() , c);
        }
        mAllFavorCacheIsDirty = false;
    }
    private void refreshCache(List<Coin> coins)
    {
        if( mCachedCoins == null)
            mCachedCoins = new LinkedHashMap<>();
        mCachedCoins.clear();
        for (Coin c : coins) {
            mCachedCoins.put(c.getName() + c.getExchange().getName() , c);
        }
        mCacheIsDirty = false;
    }


}
