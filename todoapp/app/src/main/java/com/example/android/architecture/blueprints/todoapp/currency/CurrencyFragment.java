package com.example.android.architecture.blueprints.todoapp.currency;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.tasks.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tinyhhj on 2017-06-19.
 */

public class CurrencyFragment extends Fragment implements CurrencyContract.View{
    private CurrencyContract.Presenter mPresenter;
    private CoinsAdapter mListAdapter;
    private Thread refreshThread;
    private final int refresh_period = 5000;    // 현재가 갱신 반복주기 (millisecond)

    public CurrencyFragment() {


    }

    public static CurrencyFragment newInstance() {
        return new CurrencyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mListAdapter = new CoinsAdapter(new ArrayList<Coin>(0) , null);
        Toast.makeText(getActivity() , "CurrencyFragment onCreate()",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("tinyhhj","### CurrencyFragment onResume() triggered! ###");
        mPresenter.start();

//        /* 5초마다 현재가 갱신 */
//        refreshThread = new Thread(new Runnable(){
//           @Override
//            public void run(){
//               while(true){
//                   mPresenter.start();
//                   try {
//                       Thread.sleep(refresh_period);
//                   } catch (InterruptedException e) {
//                       e.printStackTrace();
//                       break;
//                   }
//
//               }
//
//           }
//        });
//        refreshThread.start();
        Toast.makeText(getActivity() , "CurrencyFragment onResume()",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("tinyhhj","### CurrencyFragment onStop() triggered! ###");
        Toast.makeText(getActivity() , "CurrencyFragment onStop()",Toast.LENGTH_SHORT).show();
        /* 화면 stop상태에서 현재가 갱신 쓰레드 인터럽트 */
//        refreshThread.interrupt();

    }

    @Override
    public void setPresenter(CurrencyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState) {
        Toast.makeText(getActivity() , "CurrencyFragment onCreateView()",Toast.LENGTH_SHORT).show();
        View root = inflater.inflate(R.layout.coin1_fragment , container, false);
        ListView listView = (ListView) root.findViewById(R.id.coin_list);
        listView.setAdapter(mListAdapter);

        //set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout
                = (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        //set the scrolling view in the custom SwipeRefreshLayout
        swipeRefreshLayout.setScrollUpChild(listView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPresenter.start();
            }
        });

        //FlaotingAction button setup
//        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//        if (fab == null)
//            Toast.makeText(getActivity() , "CurrencyFragment onCreateView()",Toast.LENGTH_SHORT).show();
//        Log.v("tinyhhj" , ""+fab.toString());
//        fab.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if( ((CurrencyActivity)getActivity()).getCurrentFragmentId() == CurrencyActivity.MY_FAVOR_COIN_FRAGMENT ) {
//                    Toast.makeText(getActivity(), "CurrecnyFragment ChangeEvent()", Toast.LENGTH_SHORT).show();
//                    mPresenter.addNewFavorCoin(CurrencyActivity.ALL_FAVOR_COIN_FRAGMENT);
//                }
//                else {
//
//                }
//            }
//        }) ;

        return root;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if(getView() == null)
            return;
        final SwipeRefreshLayout srl = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        srl.post(new Runnable() {

            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    public boolean isActive() { return isAdded(); }

    @Override
    public void showFavorCoins(List<Coin> coins) {
        mListAdapter.replaceData(coins);
    }

    @Override
    public void showAllFavorCoins(List<Coin> coins) {

    }

    @Override
    public void showAllFavorFragment(int from , int to ) {
        ((CurrencyActivity)getActivity()).changeFragment(from ,to );
    }


    private static class CoinsAdapter extends BaseAdapter{
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
            TextView curPrice = (TextView) rowView.findViewById(R.id.cur_price);
//            TextView avgPrice = (TextView) rowView.findViewById(R.id.avg_price);
            TextView diffPrice = (TextView) rowView.findViewById(R.id.diff_price);
            TextView diffRate = (TextView) rowView.findViewById(R.id.diff_rate);
            ImageView updown = (ImageView) rowView.findViewById(R.id.updown_triangle);


            coinName.setText(coin.getName().toUpperCase());
            exchangeName.setText(coin.getExchange().getName());
            curPrice.setText(String.valueOf((int)coin.getPriceInfo().getCurPrice()));

            double diff = coin.getPriceInfo().getCurPrice() - coin.getPriceInfo().getPrevPrice();
            updown.setImageResource( (diff < 0) ? R.drawable.blue_triangle  : R.drawable.red_triangle );
            curPrice.setTextColor((diff < 0) ? Color.BLUE : Color.RED );
            diffPrice.setTextColor((diff < 0) ? Color.BLUE : Color.RED );
            diffRate.setTextColor((diff < 0) ? Color.BLUE : Color.RED );

            double rate = (diff / coin.getPriceInfo().getPrevPrice()) * 100;
            rate = Double.parseDouble(String.format("%.2f", rate));

            diffPrice.setText(String.valueOf((int)diff));
            diffRate.setText(String.valueOf(rate) + "%");

            //click listener
            return rowView;
        }
    }


}
