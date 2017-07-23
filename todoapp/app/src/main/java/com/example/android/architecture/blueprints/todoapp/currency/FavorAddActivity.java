package com.example.android.architecture.blueprints.todoapp.currency;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsRepository;
import com.example.android.architecture.blueprints.todoapp.data.source.HttpConnection;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsLocalDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsPersistenceContract;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Created by seongjinlee on 2017. 7. 11..
 */

public class FavorAddActivity extends Activity implements CurrencyContract.View{

    CurrencyContract.Presenter mPresenter;
    Button favor_add_btn ;
    private static final int NUM_COL = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.favor_add_activity);
        super.onCreate(savedInstanceState);
        mPresenter = new FavorCoinListPresenter(this , CoinsRepository.getInstance(null , CoinsLocalDataSource.getInstance(getApplicationContext())));
        /* 확인버튼누를때 뒤로가기 */
        favor_add_btn = (Button) findViewById(R.id.favor_add);
        favor_add_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    protected void onResume() {
        super.onResume();
        mPresenter.start();

    }

    protected void onStop() {
        super.onStop();
        mPresenter.refreshFavorCoinList();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.no_change, R.anim.sliding_off);

    }

    @Override
    public void setLoadingIndicator(final boolean active) {

        final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        srl.post(new Runnable() {

            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showFavorCoins(List<Coin> coins) {

    }

    @Override
    public void showAllFavorFragment(int from, int to) {

    }

    /*
    *  현재 현재가 조회 가능한 모든 목록들을 보여준다.
    */
    @Override
    public void showAllFavorExchanges(final List<Coin> coins) {
        LinearLayout exchanges_ll = (LinearLayout) findViewById(R.id.exchanges_ll);
        final LinearLayout first_coins_ll = (LinearLayout) findViewById(R.id.first_coins_ll);
        final LinearLayout mid_coins_ll = (LinearLayout) findViewById(R.id.mid_coins_ll);
        final LinearLayout last_coins_ll = (LinearLayout) findViewById(R.id.last_coins_ll);
        /* 중복 방지위해 set 이용*/
        Set<String> set = new HashSet<String>();
        /* 코인 키값으로 정렬 */
        Collections.sort(coins);

        int to = 0;

        for ( int i = 0 ; i < coins.size() ; i++)
        {
            for (Coin c : coins)
                Log.v("tinyhhj" , "allfavorlist :" +  c.getName()+c.getExchange().getName()+ " " + c.getFavor());
            /* 새로운 거래소라면 거래소 버튼 생성*/
            if( set.add(coins.get(i).getExchange().getName()) )
            {
                Button btn = new Button(this);
                btn.setText(coins.get(i).getExchange().getName());
                while(to < coins.size() && coins.get(to).getExchange().getName().compareTo(btn.getText().toString()) == 0){to++;}
                final List<Coin> coin_lists = coins.subList(i,to);
                if(!coin_lists.isEmpty()) {
                    /* 거래소 클릭리스너 등록 */
                    btn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int idx = 0;
                            first_coins_ll.removeAllViews();
                            mid_coins_ll.removeAllViews();
                            last_coins_ll.removeAllViews();
                            for(final Coin c : coin_lists) {
                                idx++;
                                final Button btn = new Button(getBaseContext());
                                btn.setText(c.getName());
                                if( c.getFavor() == CoinsPersistenceContract.Favor.FAVOR)
                                    btn.setTextColor(Color.RED);
                                else
                                    btn.setTextColor(Color.BLACK);
                                /* 거래소별 가상화폐버튼 클릭리스너 등록 */
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(c.getFavor() == CoinsPersistenceContract.Favor.NOT_FAVOR)
                                        {
                                            c.setFavor(CoinsPersistenceContract.Favor.FAVOR);
                                            btn.setTextColor(Color.RED);
                                            mPresenter.addNewFavorCoin(c);
                                        }
                                        else
                                        {
                                            c.setFavor(CoinsPersistenceContract.Favor.NOT_FAVOR);
                                            btn.setTextColor(Color.BLACK);
                                            mPresenter.removeFavorCoin(c);
                                        }

                                    }
                                });
                                if(idx % NUM_COL == 1)
                                {
                                    first_coins_ll.addView(btn);
                                }
                                else if(idx % NUM_COL == 2)
                                {
                                    mid_coins_ll.addView(btn);
                                }
                                else {
                                    last_coins_ll.addView(btn);
                                }
                            }

                        }
                    });
                }
                exchanges_ll.addView(btn);
            }
        }

    }

    @Override
    public void setPresenter(CurrencyContract.Presenter presenter) {

    }
}
