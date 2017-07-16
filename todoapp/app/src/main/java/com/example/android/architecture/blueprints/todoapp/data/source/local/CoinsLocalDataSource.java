package com.example.android.architecture.blueprints.todoapp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Exchange;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinyhhj on 2017-07-02.
 */

public class CoinsLocalDataSource implements CoinsDataSource {

    private static CoinsLocalDataSource INSTANCE;

    private CoinsDbHelper mDbHelper;

    private CoinsLocalDataSource ( Context context )
    {
        mDbHelper = new CoinsDbHelper(context);
    }

    public static CoinsLocalDataSource getInstance(Context context) {
        if ( INSTANCE == null )
            INSTANCE = new CoinsLocalDataSource(context);
        return INSTANCE;
    }

    @Override
    public void getCoins(LoadCoinsCallback callback) {


    }



    @Override
    public void getCoin(String name, String exName, GetCoinCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        String[] whereArgs = { name , exName};
        String query = "SELECT " + CoinsPersistenceContract.CoinEntry.COIN_NAME
                        +" , "    + CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME
                        +" , "    + CoinsPersistenceContract.CoinEntry.COIN_FAVORED
                        +" FROM " + CoinsPersistenceContract.CoinEntry.TABLE_NAME
                        +" WHERE " + CoinsPersistenceContract.CoinEntry.COIN_NAME + " = ? "
                        +" AND " +CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME + " = ? ";
        Cursor cur = db.rawQuery(query,whereArgs);

        Coin c = null;
        if( cur.getCount() > 0 && cur != null )
        {
            cur.moveToNext();
            String coinName = cur.getString(cur.getColumnIndex(CoinsPersistenceContract.CoinEntry.COIN_NAME));
            String exchangeName = cur.getString(cur.getColumnIndex(CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME));
            String favored = cur.getString(cur.getColumnIndex(CoinsPersistenceContract.CoinEntry.COIN_FAVORED));
            c = new Coin(coinName, new Exchange(exchangeName));
            c.setFavor(String.valueOf(CoinsPersistenceContract.Favor.FAVOR).compareTo(favored) == 0 ? CoinsPersistenceContract.Favor.FAVOR : CoinsPersistenceContract.Favor.NOT_FAVOR);
            callback.onCoinLoaded(c);
        }
        else
        {
            callback.onDataNotAvailable();
        }

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

    }

    @Override
    public void deleteAllCoins() {

    }

    @Override
    public void deleteCoin(String name, String exName) {

    }

    @Override
    public void getFavorCoins(LoadCoinsCallback callback) {
        List<Coin> coins = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] whereArgs = new String[] {
                String.valueOf(CoinsPersistenceContract.Favor.FAVOR)
        };
        String queryString =
                "SELECT " + CoinsPersistenceContract.CoinEntry.COIN_NAME
                        + ", "+ CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME
                        + " FROM " +CoinsPersistenceContract.CoinEntry.TABLE_NAME
                        + " WHERE " + CoinsPersistenceContract.CoinEntry.COIN_FAVORED + " = ? ";
        Cursor cur = db.rawQuery(queryString,whereArgs);

        Coin c = null;
        if( cur != null && cur.getCount() > 0)
        {
            while(cur.moveToNext()) {
                String coinName = cur.getString(cur.getColumnIndexOrThrow(CoinsPersistenceContract.CoinEntry.COIN_NAME));
                String coinExchangeName = cur.getString(cur.getColumnIndex(CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME));
                Coin coin =  new Coin(coinName, new Exchange(coinExchangeName));
                coins.add(coin);
            }
        }
        if( cur != null ) {
            cur.close();
        }

        db.close();

        if( coins.isEmpty())
        {
            callback.onDataNotAvailable();
        }
        else
        {
            callback.onCoinsLoaded(coins);
        }
    }

    @Override
    public void getAllFavorCoins(LoadCoinsCallback callback) {

    }

    @Override
    public void refreshAllFavorCoins() {

    }




    @Override
    public void saveNewFavorCoin(String coinName, String exName) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CoinsPersistenceContract.CoinEntry.COIN_NAME , coinName);
        contentValues.put(CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME , exName);
        contentValues.put(CoinsPersistenceContract.CoinEntry.COIN_FAVORED , String.valueOf(CoinsPersistenceContract.Favor.FAVOR));

        db.insert(CoinsPersistenceContract.CoinEntry.TABLE_NAME , null, contentValues);

        db.close();

    }

    @Override
    public void removeFavorCoin(String coinName, String exName) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] whereArgs = { coinName, exName};
        String sql = "DELETE FROM "+ CoinsPersistenceContract.CoinEntry.TABLE_NAME + " WHERE "
                        + CoinsPersistenceContract.CoinEntry.COIN_NAME + " = ? AND "
                        + CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME + " = ? ";
        db.rawQuery(sql,whereArgs);

    }

    @Override
    public void setCoinStatus(String coinName, String exName , CoinsPersistenceContract.Favor favor) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CoinsPersistenceContract.CoinEntry.COIN_FAVORED  , String.valueOf(favor));


        String whereClauses = CoinsPersistenceContract.CoinEntry.COIN_NAME + " = '"+coinName+"' AND "+CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME + " = '"
                                +exName +"'";


        db.update(CoinsPersistenceContract.CoinEntry.TABLE_NAME , contentValues , whereClauses , null );
        db.close();
    }
}
