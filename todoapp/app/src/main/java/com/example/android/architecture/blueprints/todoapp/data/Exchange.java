package com.example.android.architecture.blueprints.todoapp.data;

/**
 * Created by tinyhhj on 2017-06-25.
 */

public class Exchange {
    private String mName;         // 거래소 이름 : 빗썸 , okcoin
    private String mUrl;          //  서버 url
    private static final String serverIp = "abc";

    public Exchange(String ExName)
    {
        this(ExName , serverIp );
    }

    public Exchange(String ExName , String url)
    {
        mName = ExName;
        mUrl = url;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }


}
