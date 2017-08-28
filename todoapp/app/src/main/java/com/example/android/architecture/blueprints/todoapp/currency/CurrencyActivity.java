package com.example.android.architecture.blueprints.todoapp.currency;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.CoinsRepository;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsLocalDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.local.CoinsPersistenceContract;
import com.example.android.architecture.blueprints.todoapp.util.ActivityUtils;
import javax.sql.DataSource;


public class CurrencyActivity extends AppCompatActivity  implements CurrencyContract.CoinItemListener{
    //Fragment id
    public static final int MY_FAVOR_COIN_FRAGMENT =  0;
    public static final int ALL_FAVOR_COIN_FRAGMENT = 1;

    private static final String CURRENT_TAB_NO = "CURRENT_TAB_NO";
    private static final int MAX_NUM_FRAGMENTS = 3;
    private CurrencyContract.Presenter[] mPresenterArr = new CurrencyContract.Presenter [MAX_NUM_FRAGMENTS];
    private CurrencyContract.View[] mFragmentArr = new CurrencyContract.View[MAX_NUM_FRAGMENTS];
    private int currentFragmentId  = MY_FAVOR_COIN_FRAGMENT ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favorAddActivity = new Intent(CurrencyActivity.this, FavorAddActivity.class);
                startActivity(favorAddActivity);
                overridePendingTransition(R.anim.sliding_on, R.anim.no_change);

                // 새 관심코인 추가 (임시 블럭처리)
//                if( currentFragmentId == CurrencyActivity.MY_FAVOR_COIN_FRAGMENT ) {
//                    mPresenterArr[currentFragmentId].changeFragment(MY_FAVOR_COIN_FRAGMENT  , ALL_FAVOR_COIN_FRAGMENT);
//                }
//                else {
//
//                }



            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        CurrencyFragment currency_fragment =
                (CurrencyFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if( currency_fragment == null )
        {
            currency_fragment = CurrencyFragment.newInstance();
            mFragmentArr[MY_FAVOR_COIN_FRAGMENT] = currency_fragment;
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager() , (Fragment) mFragmentArr[MY_FAVOR_COIN_FRAGMENT] , R.id.contentFrame);
        }
        CoinsDataSource repo = CoinsRepository.getInstance(null , CoinsLocalDataSource.getInstance(getApplicationContext()));
        mPresenterArr[MY_FAVOR_COIN_FRAGMENT] = new CurrencyPresenter(currency_fragment , repo);

        if(savedInstanceState != null) {

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //뒤로가기버튼눌렀을 때, 전체조회화면이면 메인화면으로 복귀한다.
//        if (currentFragmentId == ALL_FAVOR_COIN_FRAGMENT) {
//            changeFragment(ALL_FAVOR_COIN_FRAGMENT , MY_FAVOR_COIN_FRAGMENT);
//        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
    public void changeFragment( int from , int to ) {
        if( mFragmentArr[to] == null ) {
            mFragmentArr[to] = FavorCoinListFragment.getInstance();
        }
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager() , (Fragment) mFragmentArr[to] , R.id.contentFrame);
        CoinsDataSource repo = CoinsRepository.getInstance(null , CoinsLocalDataSource.getInstance(getApplicationContext()));
        mPresenterArr[to] = new FavorCoinListPresenter(mFragmentArr[to] , repo);
        currentFragmentId = to;

    }
    public int getCurrentFragmentId() {
        return currentFragmentId;
    }

    @Override
    public void onCoinClick(Coin c) {
        if(getCurrentFragmentId() == ALL_FAVOR_COIN_FRAGMENT)
        {
            if (c.getFavor()  == CoinsPersistenceContract.Favor.FAVOR)
                mPresenterArr[ALL_FAVOR_COIN_FRAGMENT].removeFavorCoin(c);
            else
                mPresenterArr[ALL_FAVOR_COIN_FRAGMENT].addNewFavorCoin(c.getName() , c.getExchange().getName());
        }
    }
}
