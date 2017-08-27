package com.example.android.architecture.blueprints.todoapp.data.source;

import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Exchange;
import com.example.android.architecture.blueprints.todoapp.data.PriceInfo;
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
                    Log.v("tinyhhj" , "CoinsRepository getFavorCoins onCoinsLoaded");
                    refreshCache(coins);
                    // 가격정보 요청
                    HttpConnection httpConnection = new HttpConnection( HttpConnection.url + "index.php" , "POST");
                    httpConnection.requestPriceInfo(mCachedCoins , callback);
                   // callback.onCoinsLoaded(coins);
                }

                @Override
                public void onDataNotAvailable()
                {
                    Log.v("tinyhhj" , "CoinsRepository getFavorCoins onDataNotAvailable");
                    callback.onDataNotAvailable();
                }

                @Override
                public void onError() {

                }
            });
        }
        // cache coin return
        else
        {
            HttpConnection httpConnection = new HttpConnection( HttpConnection.url + "index.php" , "POST");
            httpConnection.requestPriceInfo(mCachedCoins , callback);
        }

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
        Log.v("tinyhhj" , "refreshAllfavorCoins() start");
        mAllFavorCacheIsDirty = true;
    }

    @Override
    public void setCoinStatus(String coinName, String exName , CoinsPersistenceContract.Favor favor) {

    }

    @Override
    public void saveNewFavorCoin(final String coinName, final String exName) {

         mCoinsLocalDataSource.getCoin(coinName, exName, new GetCoinCallback() {
            @Override
            public void onCoinLoaded(Coin coin) {
                if(coin.getFavor() == CoinsPersistenceContract.Favor.NOT_FAVOR) {
                    mCoinsLocalDataSource.setCoinStatus(coinName, exName, CoinsPersistenceContract.Favor.FAVOR);
                }
            }

            @Override
            public void onDataNotAvailable() {
                mCoinsLocalDataSource.saveNewFavorCoin(coinName, exName);

            }

             @Override
             public void onError() {

             }
         });
        refreshCoins();
    }

    @Override
    public void removeFavorCoin(final String coinName, final String exName) {
        mCoinsLocalDataSource.getCoin(coinName, exName, new GetCoinCallback() {
            @Override
            public void onCoinLoaded(Coin coin) {
                mCoinsLocalDataSource.setCoinStatus(coinName , exName , CoinsPersistenceContract.Favor.NOT_FAVOR);
            }

            @Override
            public void onDataNotAvailable() {

            }

            @Override
            public void onError() {

            }
        });
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
    public void getOrderInfos(Coin c) {
        //string format get method
        String httpAddr = String.format("orderbook.php?xchg=%s&coin=%s",c.getExchange().getName() , c.getName());
        Log.v("tinyhhj" , "request order addr : " + httpAddr);
        HttpConnection httpConnection = new HttpConnection(HttpConnection.url + httpAddr,"GET");


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

                    Log.v("tinyhhj" , "mCacheIsDirty : " +mCacheIsDirty);
                    // 관심코인이 없을경우 로드
                    if (mCachedCoins == null || mCacheIsDirty == true )
                    {
                        mCoinsLocalDataSource.getFavorCoins(new LoadCoinsCallback() {
                            @Override
                            public void onCoinsLoaded(List<Coin> coins) {
                                refreshCache(coins);

                            }

                            @Override
                            public void onDataNotAvailable()
                            {

                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }

                    //관심코인이 로드 되었다면 Favor로 세팅
                    if( !mCacheIsDirty)
                    {
                        //전체 리스트중 관심코인을 체크하여 favor 값을 세팅해준다.
                        for (Coin c : mCachedCoins.values())
                        {
                            for ( Coin cc : Coins ) {
                                if (c.compareTo(cc) == 0 && c.getName().compareTo(cc.getName()) == 0)
                                {
                                    cc.setFavor(CoinsPersistenceContract.Favor.FAVOR);
                                }
                            }
                        }
                    }

                    refreshAllFavorCache(Coins);
                    callback.onCoinsLoaded(new ArrayList<Coin>(mCachedAllFavorCoins.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    //목록조회 불가능할때 정의
                }

                @Override
                public void onError() {

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
