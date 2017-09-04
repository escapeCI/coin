package com.example.android.architecture.blueprints.todoapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Exchange;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsRepository;
import com.example.android.architecture.blueprints.todoapp.data.source.HttpConnection;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsLocalDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by seongjinlee on 2017. 9. 3..
 */

public class CoinWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new CoinPriceGet(context, appWidgetManager), 5000, 5000);
    }
    class CoinPriceGet extends TimerTask{
        RemoteViews remoteViews;
        AppWidgetManager appWidgetmanager;
        ComponentName componentName;
        private Context context;

        public CoinPriceGet(Context context, AppWidgetManager appWidgetManager){
            this.appWidgetmanager = appWidgetManager;
            this.remoteViews = new RemoteViews(context.getPackageName(), R.layout.coin_widget);
            this.componentName = new ComponentName(context, CoinWidgetProvider.class);
            this.context = context;
        }

        @Override
        public void run() {
            Log.i("seongenie", "executed!!!");

            Map<String, Coin> map = new HashMap<String, Coin>();

            map.put("XRPbithumb", new Coin("XRP", new Exchange("bithumb"), null));

            HttpConnection httpConnection = new HttpConnection( HttpConnection.url + "index.php" , "POST");
            httpConnection.requestPriceInfo(map, new CoinsDataSource.LoadCoinsCallback() {
                @Override
                public void onCoinsLoaded(List<Coin> coins) {
                    remoteViews.setTextViewText(R.id.coinTextView, (int)coins.get(0).getPriceInfo().getCurPrice()+"");
                    appWidgetmanager.updateAppWidget(componentName, remoteViews);
                }

                @Override
                public void onDataNotAvailable() {
                    Log.i("seongenie", "onDataNotAvailable!!!");
                }

                @Override
                public void onError() {
                    Log.i("seongenie","ERROR!!!");
                }
            });

        }
    }
}

