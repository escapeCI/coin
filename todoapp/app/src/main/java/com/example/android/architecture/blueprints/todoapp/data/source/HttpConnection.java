package com.example.android.architecture.blueprints.todoapp.data.source;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.architecture.blueprints.todoapp.data.Coin;
import com.example.android.architecture.blueprints.todoapp.data.Exchange;
import com.example.android.architecture.blueprints.todoapp.data.Order;
import com.example.android.architecture.blueprints.todoapp.data.PriceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tinyhhj on 2017-07-02.
 */

public class HttpConnection {
    public static final String url = "http://13.124.162.39/";
    private static final int CONN_TIMEOUT = 3;
    private static final int READ_TIMEOUT = 3;
    private CoinsDataSource.LoadCoinsCallback mCallback;
    private static final int ORDER_INFO_COUNT = 5;

    //For priceInfo JSONObject Keys
    public static final String CUR_PRICE = "last_price";
    public static final String PREV_PRICE = "first_price";
    private HttpURLConnection conn ;
    OutputStream os ;
    InputStream is;
    ByteArrayOutputStream baos ;

    public HttpConnection(String method) {
        this(url, method);
    }
    public HttpConnection(String baseAddr , String method) {
        try {
            URL opener = new URL(baseAddr);
            Log.v("tinyhhj" , ""+ baseAddr + " " + method);
            conn = (HttpURLConnection)opener.openConnection();
            conn.setConnectTimeout(CONN_TIMEOUT * 1000);
            conn.setReadTimeout(READ_TIMEOUT * 1000);
            conn.setRequestProperty("Accept", "application/json,text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
            if(method.equals("POST")) {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
            } else if (method.equals("GET"))
            {
                conn.setRequestMethod("GET");
            }
            conn.setDoInput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject changeListIntoJson(Map<String , Coin> coins) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray ;

        for (Map.Entry<String, Coin> entry  : coins.entrySet())
        {
            String exName = entry.getValue().getExchange().getName();
            String coinName = entry.getValue().getName();
            if(jsonObj.has(exName)){
                jsonArray = (JSONArray)jsonObj.get(exName);
                jsonArray.put(coinName);
            }
            else{
                jsonArray = new JSONArray();
                jsonArray.put(coinName);
                jsonObj.put(exName , jsonArray);
            }
        }
        Log.v("tinyhhj" ,"request form : " + jsonObj.toString());

        return jsonObj;
    }
    private JSONObject sendJsonRequest(JSONObject request) throws IOException, JSONException {
        os = conn.getOutputStream();
        os.write(request.toString().getBytes());
        os.flush();
        os.close();
        Log.v("tinyhhj" ,"http result : " + conn.getResponseCode());
        if( conn.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData;
            int nLength;
            while( (nLength = is.read(byteBuffer , 0 , byteBuffer.length)) != -1)
                baos.write(byteBuffer, 0 ,nLength);
            byteData = baos.toByteArray();
            String resp = new String(byteData);
            Log.v("tinyhhj" ,"result form" + resp);
            return new JSONObject(resp);
        }
        return null;

    }

    private void updateResponse(Map<String , Coin> coins ,JSONObject response ) throws JSONException {


//        jObj = response.getJSONObject("data");
//        Iterator<String>
//        for(int i = 0 ; i < jArray.length() ; i++) {
            //Log.v("tinyhhj" , ""+response.toString());
            JSONObject exchangeInfo = response.getJSONObject("data");
            double exchangeRate = response.getDouble("USDKRW");
            //Log.v("tinyhhj" , ""+exchangeInfo.toString());
            Iterator<String> exchangeKeys = exchangeInfo.keys();

            while(exchangeKeys.hasNext()) {
                String exchangeName = exchangeKeys.next();
                JSONObject coinInfo = (JSONObject) exchangeInfo.get(exchangeName);
                //Log.v("tinyhhj" , ""+coinInfo.toString());
                Iterator<String> coinKeys = coinInfo.keys();
                while(coinKeys.hasNext()) {
                    String coinName = coinKeys.next();
                    JSONObject priceInfo = (JSONObject) coinInfo.get(coinName);
                    //Log.v("tinyhhj" , ""+priceInfo.toString());
                    double curPrice = (double) Double.parseDouble((String)priceInfo.get(CUR_PRICE));
                    double prevPrice = (double) Double.parseDouble((String)priceInfo.get(PREV_PRICE));
                    /* 현재 poloniex의 경우에 달러로 가격이 오므로 환율을 곱해줘서 원화로 표현 */
                    if(exchangeName.compareTo("poloniex") == 0) {
                        curPrice *= exchangeRate;
                        prevPrice *= exchangeRate;
                    }

                    Log.v("tinyhhj" , "" + coinName+exchangeName + " " + curPrice + " " + prevPrice);

                    Coin c = coins.get(coinName + exchangeName);
                    c.setPriceInfo(new PriceInfo(curPrice, prevPrice));
                }
            }
//        }
    }

    public void requestPriceInfo(final Map<String , Coin> coins , CoinsDataSource.LoadCoinsCallback callback)  {

            mCallback = callback;

            //json 형태로 파싱
            new AsyncTask<Void, Void,Void>() {
                JSONObject obj;
                int status = 0;
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        if( isCancelled())
                            return null;
                        obj = changeListIntoJson(coins);
                        //서버에 요청
                        JSONObject responseObj = sendJsonRequest(obj);
                        // 응답 update
                        // 각 코인의 priceinfo를 찾아서 update해주기 힘들어서 그냥 clear 후 새로운 코인 list 생성
                        if( responseObj != null ) {
                            updateResponse( coins , responseObj);
                        }
                    } catch (Exception e) {
                        Log.v("tinyhhj" , "exception occur!");
                        e.printStackTrace();
                        status = -1;
                    }


                    return null;
                }

                protected void onPostExecute(Void aVoid)
                {
                    super.onPostExecute(aVoid);
                    if(coins.size() > 0 )
                        mCallback.onCoinsLoaded(new ArrayList<>(coins.values()));
                    else
                        mCallback.onDataNotAvailable();

                    Log.v("tinyhhj" , "end");
                }


            }.execute();
        }
        private void getAllFavorCoinList(JSONObject allFavorCoins , List<Coin> coins) throws JSONException {
            Iterator<String> exchangeInfos = allFavorCoins.keys();
            while(exchangeInfos.hasNext())
            {
                String exName = exchangeInfos.next();
                JSONArray coinInfos = allFavorCoins.getJSONArray(exName);
                for(int i = 0 ; i < coinInfos.length() ; i++){
                    String coinName = (String)coinInfos.get(i);
                    coins.add(new Coin(coinName, new Exchange(exName)));
                }
            }
        }
        public void requestAllFavorCoinList(final List<Coin> coins , CoinsDataSource.LoadCoinsCallback callback)
        {
            mCallback = callback;
            new AsyncTask<Void , Void , Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        if( isCancelled()) {
                            Log.v("tinyhhj","isCancelled triggered");
                            return null;
                        }
                        //os = conn.getOutputStream();
                        is = conn.getInputStream();
                        Log.v("tinyhhj" ,"http result : " + conn.getResponseCode());
                        if( conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                        {

                            baos = new ByteArrayOutputStream();
                            byte[] byteBuffer = new byte[1024];
                            byte[] byteData;
                            int nLength;
                            while( (nLength = is.read(byteBuffer , 0 , byteBuffer.length)) != -1)
                                baos.write(byteBuffer, 0 ,nLength);
                            byteData = baos.toByteArray();
                            String resp = new String(byteData);
                            Log.v("tinyhhj" ,"result form" + resp);
                            getAllFavorCoinList(new JSONObject(resp) , coins);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(Void aVoid)
                {
                    super.onPostExecute(aVoid);
                    if(coins.size() > 0 ) {
                        mCallback.onCoinsLoaded(coins);
                    }
                    else {
                        mCallback.onDataNotAvailable();
                    }
                    Log.v("tinyhhj" , "end " + coins.size());
                }
            }.execute();

        }

        //get order infos
        private void getOrderInfos(JSONObject order_info, List<Order> list ) throws JSONException {
            Iterator<String> keys = order_info.keys();
            while(keys.hasNext())
            {
                String key = keys.next();
                if( key.compareTo("data") == 0)
                {
                    JSONObject infos = order_info.getJSONObject(key);
                    Iterator<String> order_type = infos.keys();
                    while(order_type.hasNext())
                    {
                        String order_type_key = order_type.next();
                        Order.Order_type ot ;
                        if( order_type_key.compareTo("ASK") == 0)
                        {
                            ot = Order.Order_type.ASK;

                        }
                        else if(order_type_key.compareTo("BID") == 0)
                        {
                            ot = Order.Order_type.BID;
                        }
                        else
                        {
                            Log.v("tinyhhj", "### error with orde_type_key :" + order_type_key + " ###" );
                            return;
                        }
                        JSONObject ask_order_info = infos.getJSONObject(order_type_key);
                        Iterator<String> ask_order_price = ask_order_info.keys();
                        while(ask_order_price.hasNext())
                        {
                            String ask_order_price_key = ask_order_price.next();
                            double ask_order_amount = Double.parseDouble((String)ask_order_info.get(ask_order_price_key));
                            double ask_price = Double.parseDouble(ask_order_price_key);
                            list.add(new Order(ot , ask_order_amount , new PriceInfo(ask_price, ask_price) ));
                        }
                    }
                }
                else if(key.compareTo("USDKRW") == 0)
                {

                }
                else
                {

                }

            }
        }
        public void requsetGetMethod(final Object o , final int ver, final CoinsDataSource.LoadCoinsCallback callback)
        {
            new AsyncTask<Void , Void , Void> () {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        is = conn.getInputStream();
                        if( conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                        {
                            baos = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            byte[] data;
                            int nLength;
                            while ((nLength = is.read(buf , 0 , buf.length)) != -1)
                            {
                                baos.write(buf, 0 , nLength);
                            }
                            data = baos.toByteArray();
                            String resp = new String(data);
                            Log.v("tinyhhj" , "Response"+resp);

                            switch(ver)
                            {
                                case 0 :
                                    List<Order> orders = (List<Order>)o;
                                    getOrderInfos( new JSONObject(resp) , orders );


                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }

}








