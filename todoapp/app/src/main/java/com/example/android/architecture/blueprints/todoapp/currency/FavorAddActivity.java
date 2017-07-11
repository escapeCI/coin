package com.example.android.architecture.blueprints.todoapp.currency;

import android.app.Activity;
import android.os.Bundle;
import com.example.android.architecture.blueprints.todoapp.R;

/**
 * Created by seongjinlee on 2017. 7. 11..
 */

public class FavorAddActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.favor_add_activity);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.no_change, R.anim.sliding_off);

    }
}
