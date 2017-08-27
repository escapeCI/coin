package com.example.android.architecture.blueprints.todoapp.currency;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Order;
import com.example.android.architecture.blueprints.todoapp.tasks.ScrollChildSwipeRefreshLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RunnableFuture;

/**
 * Created by tinyhhj on 2017-06-19.
 */

public class CurrencyFragment extends Fragment implements CurrencyContract.View{
    private CurrencyContract.Presenter mPresenter;
    private CoinsAdapter mListAdapter;
    private Thread refreshThread;
    private final int refresh_period = 5000;    // 현재가 갱신 반복주기 (millisecond)
    private LinearLayout coinLL;
    private LinearLayout noCoinLL;
    private Dialog mDialog;
    private Handler handler;
    private OrdersAdapter mOrderAdapter;

    public CurrencyFragment() {
    }

    public static CurrencyFragment newInstance() {
        return new CurrencyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mListAdapter = new CoinsAdapter(new ArrayList<Coin>(0) , itemClickListener);
        Toast.makeText(getActivity() , "CurrencyFragment onCreate()",Toast.LENGTH_SHORT).show();
        handler = new Handler();
        mOrderAdapter = new OrdersAdapter(new ArrayList<Order>(0));


//        /* 5초마다 현재가 갱신 쓰레드 */
//        refreshThread = new Thread(new Runnable(){
//            @Override
//            public void run(){
//                while(!refreshThread.isInterrupted()){
//                    mPresenter.start();
//                    try {
//                        Thread.sleep(refresh_period);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        /* 메뉴목록을 보이게설정 */
        setHasOptionsMenu(true);
    }
    /* 팝업메뉴목록 생성 및 버튼리스너 등록 */
    void showSortTypePopUp() {
        PopupMenu popup = new PopupMenu(getContext() , getActivity().findViewById(R.id.sort_type));
        popup.getMenuInflater().inflate(R.menu.sort_type , popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.sort_coin_name:
                        if(mPresenter.getSortType() == CurrencyPresenter.SORT_TYPE.CNAME_ASC)
                        {
                            mPresenter.setSortType(CurrencyPresenter.SORT_TYPE.CNAME_DESC);
                        }
                        else
                        {
                            mPresenter.setSortType(CurrencyPresenter.SORT_TYPE.CNAME_ASC);
                        }
                        break;
                    case R.id.sort_ex_name:
                        if( mPresenter.getSortType() == CurrencyPresenter.SORT_TYPE.EXNAME_ASC)
                        {
                            mPresenter.setSortType(CurrencyPresenter.SORT_TYPE.EXNAME_DESC);
                        }
                        else
                        {
                            mPresenter.setSortType(CurrencyPresenter.SORT_TYPE.EXNAME_ASC);
                        }
                        break;
                    case R.id.sort_price:
                        if(mPresenter.getSortType() == CurrencyPresenter.SORT_TYPE.PRICE_ASC)
                        {
                            mPresenter.setSortType(CurrencyPresenter.SORT_TYPE.PRICE_DESC);
                        }
                        else
                        {
                            mPresenter.setSortType(CurrencyPresenter.SORT_TYPE.PRICE_ASC);
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_type:
                Log.v("tinyhhj" , "sort_type btn clicked");
                showSortTypePopUp();
                break;
            default:
                mPresenter.setSortType(CurrencyPresenter.SORT_TYPE.NONE);
        }
        return true;
    }
    /* 메뉴 목록을 만든다.*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.currency_fragment_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("tinyhhj","### CurrencyFragment onResume() triggered! ###");
        mPresenter.start();

        if( refreshThread == null) {
            Log.v("tinyhhj" ,"refreshThread is null ");
            refreshThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v("tinyhhj" ,"refreshThread is start ");
                    while(true)
                    {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                mPresenter.start();
                            }
                        });

                        try {
                            Thread.sleep(refresh_period);
                        }
                        catch(InterruptedException e)
                        {
                            e.printStackTrace();
                            Log.v("tinyhhj" ,"refreshThread end ");
                            break;
                        }
                    }
                }
            });
        }
        refreshThread.start();

        Toast.makeText(getActivity() , "CurrencyFragment onResume()",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshThread.interrupt();

    }

    @Override
    public void onStop() {

        super.onStop();
        Log.v("tinyhhj","### CurrencyFragment onStop() triggered! ###");
        Toast.makeText(getActivity() , "CurrencyFragment onStop()",Toast.LENGTH_SHORT).show();
        /* 화면 stop상태에서 현재가 갱신 쓰레드 인터럽트 */

//       2017-07-12 : 현재가 스레드 임시 주석처리
//        Log.i("seongenie", "Thread Stop");
//        /* 화면 stop상태에서 현재가 갱신 쓰레드 인터럽트 */
//        if(!refreshThread.isInterrupted())refreshThread.interrupt();
//        Log.i("seongenie", "Thread State : "  + refreshThread.isInterrupted());

        super.onStop();
    }

    @Override
    public void onDestroy() {
        //       2017-07-12 : 현재가 스레드 임시 주석처리
        //if(!refreshThread.isInterrupted())refreshThread.interrupt();
        super.onDestroy();
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

        /* 관심코인 있을경우와 없을경우 리니어레이아웃 */
        coinLL = (LinearLayout) root.findViewById(R.id.coinLL);
        noCoinLL = (LinearLayout) root.findViewById(R.id.noCoinLL);

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

        //Dialog 생성
        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.dialog_popup);
        ListView dlv = (ListView) mDialog.findViewById(R.id.popup_listview);
        dlv.setAdapter(mOrderAdapter);

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
        coinLL.setVisibility(View.VISIBLE);
        noCoinLL.setVisibility(View.GONE);
    }

    @Override
    public void showAllFavorExchanges(List<Coin> coins) {

    }

    @Override
    public void showAllFavorFragment(int from , int to ) {
        ((CurrencyActivity)getActivity()).changeFragment(from ,to );
    }

    @Override
    public void showNoFavorCoins() {
        Log.v("tinyhhj" , "CurrencyFragment shownoFavorCoins");
        coinLL.setVisibility(View.GONE);
        noCoinLL.setVisibility(View.VISIBLE);

    }
    //order list adapter
    private static class OrdersAdapter extends BaseAdapter {
        private List<Order> orders;

        public OrdersAdapter( List<Order> orders)
        {
            this.orders = orders;
        }

        @Override
        public int getCount() {
            return orders == null? 0 : orders.size();
        }

        @Override
        public Object getItem(int i) {
            return orders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if(view == null)
            {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.order_item , viewGroup , false);
            }

            Order o = (Order) getItem(i);

            TextView order_type = (TextView) rowView.findViewById(R.id.order_item_name);
            TextView order_price = (TextView) rowView.findViewById(R.id.order_item_price);
            TextView order_amount = (TextView) rowView.findViewById(R.id.order_item_amount);

            order_type.setText(o.getOrder_type() == Order.Order_type.BID ? "구매" : "판매");
            order_price.setText(new DecimalFormat("#,###").format((int)o.getOrder_price().getCurPrice()));
            order_amount.setText(Double.toString(o.getOrder_amount()));
            int color;
            if(o.getOrder_type() == Order.Order_type.BID)
            {
                color = Color.RED;
            }
            else
            {
                color = Color.BLUE;
            }
            order_type.setTextColor(color);
            order_price.setTextColor(color);
            order_amount.setTextColor(color);

            return rowView;
        }
    }
    //FavorCoin List adapter
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



            /* 숫자 1,000 형태의 String으로 변환 */
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String numberAsString = decimalFormat.format((int)coin.getPriceInfo().getCurPrice());
            curPrice.setText(numberAsString);

            double diff = coin.getPriceInfo().getCurPrice() - coin.getPriceInfo().getPrevPrice();
            numberAsString = decimalFormat.format((int)diff);
            diffPrice.setText(numberAsString);

            if(diff != 0){
                updown.setImageResource( (diff < 0) ? R.drawable.blue_triangle  : R.drawable.red_triangle );
                curPrice.setTextColor( (diff < 0) ? Color.BLUE : Color.RED );
                diffRate.setTextColor( (diff < 0) ? Color.BLUE : Color.RED );
                diffPrice.setTextColor((diff < 0) ? Color.BLUE : Color.RED );
            }

            double rate = (diff / coin.getPriceInfo().getPrevPrice()) * 100;
            rate = Double.parseDouble(String.format("%.2f", rate));
            diffRate.setText(String.valueOf(rate) + "%");

            //click listener
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onCoinClick(coin);
                }
            });
            return rowView;
        }
    }

    CurrencyContract.CoinItemListener itemClickListener = new CurrencyContract.CoinItemListener() {
        @Override
        public void onCoinClick(Coin c) {
            // get Data
            mPresenter.getOrderInfo(c);
            // inflate data on listView
        }
    };


}
