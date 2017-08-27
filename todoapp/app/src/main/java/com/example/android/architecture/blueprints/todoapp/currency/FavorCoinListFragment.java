package com.example.android.architecture.blueprints.todoapp.currency;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Order;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsPersistenceContract;
import com.example.android.architecture.blueprints.todoapp.tasks.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Created by tinyhhj on 2017-07-09.
 */

public class FavorCoinListFragment extends Fragment
                                    implements CurrencyContract.View {

    private CurrencyContract.Presenter mPresenter;
    private CoinsAdapter mListAdapter;


    public FavorCoinListFragment() {}
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mListAdapter = new CoinsAdapter(new ArrayList<Coin>(0) , (CurrencyContract.CoinItemListener) getActivity());
    }
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }



    public static FavorCoinListFragment getInstance() {
        return new FavorCoinListFragment();
    }
    @Override
    public void setLoadingIndicator(final boolean active) {
        if( getView() == null)
            return;
        final SwipeRefreshLayout srl = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
        srl.post(new Runnable() {

            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showFavorCoins(List<Coin> coins) {

    }

    @Override
    public void showAllFavorExchanges(List<Coin> coins) {
        mListAdapter.replaceData(coins);
    }

    @Override
    public void showAllFavorFragment(int from, int to) {

    }

    @Override
    public void showNoFavorCoins() {

    }

    @Override
    public void showOrderLists(List<Order> orders) {

    }

    @Override
    public void setPresenter(CurrencyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.coin1_fragment , container , false);
        //set up favorcoinview
        ListView listView = (ListView) root.findViewById(R.id.coin_list);
        listView.setAdapter(mListAdapter);

        ScrollChildSwipeRefreshLayout srl = (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        srl.setScrollUpChild(listView);
        srl.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPresenter.start();
            }
        });
        return root;
    }

    private static class CoinsAdapter extends BaseAdapter {
        private List<Coin> mCoins;
        private CurrencyContract.CoinItemListener mItemListener;

        public CoinsAdapter(List<Coin> coins , CurrencyContract.CoinItemListener listener )
        {
            mCoins = coins;
            mItemListener = listener;
        }
        public void replaceData(List<Coin> coins)
        {
            mCoins = coins;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return mCoins == null? 0 : mCoins.size();
        }

        @Override
        public Coin getItem(int position) {
            return mCoins.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if( rowView == null )
            {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.coin_item , parent , false);
            }

            final Coin coin = getItem(position);

            TextView coinName = (TextView) rowView.findViewById(R.id.coin_name);
            TextView exchangeName = (TextView) rowView.findViewById(R.id.exchange_name);
//            TextView curPrice = (TextView) rowView.findViewById(R.id.cur_price);
//            TextView avgPrice = (TextView) rowView.findViewById(R.id.avg_price);

            coinName.setText(coin.getName());
            coinName.setTextColor(coin.getFavor() == CoinsPersistenceContract.Favor.FAVOR ? Color.RED : Color.BLACK);
            exchangeName.setText(coin.getExchange().getName());

            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                        mItemListener.onCoinClick(coin);
                }
            });

//            curPrice.setText(String.valueOf(coin.getPriceInfo().getCurPrice()));
//            avgPrice.setText(String.valueOf(coin.getPriceInfo().getAvgPrice24h()));

            //click listener
            return rowView;
        }
    }


}
