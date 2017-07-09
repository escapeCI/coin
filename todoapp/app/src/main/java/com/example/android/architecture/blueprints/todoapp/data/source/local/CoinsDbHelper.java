package com.example.android.architecture.blueprints.todoapp.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tinyhhj on 2017-07-02.
 */

public class CoinsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Coins.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = " , ";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CoinsPersistenceContract.CoinEntry.TABLE_NAME + " (" +
                    CoinsPersistenceContract.CoinEntry.COIN_NAME + TEXT_TYPE + COMMA_SEP +
                    CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME + TEXT_TYPE + COMMA_SEP +
                    CoinsPersistenceContract.CoinEntry.COIN_FAVORED + INTEGER_TYPE  + COMMA_SEP +
                    "PRIMARY KEY ( "+ CoinsPersistenceContract.CoinEntry.COIN_NAME
                                       + COMMA_SEP
                                        + CoinsPersistenceContract.CoinEntry.COIN_EXCHANGE_NAME + " ) "
                    + " ) ";

    public CoinsDbHelper(Context context){
        super(context , DATABASE_NAME , null , DATABASE_VERSION );
    }

    public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE_ENTRIES);}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
