package com.example.android.architecture.blueprints.todoapp.data;

/**
 * Created by tinyhhj on 2017-06-25.
 */

public class Exchange {
    private String mName;         // 거래소 이름 : 빗썸 , okcoin
    private static String mUrl;          //  서버 url

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }


}
