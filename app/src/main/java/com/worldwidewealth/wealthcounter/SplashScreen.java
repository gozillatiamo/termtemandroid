package com.worldwidewealth.wealthcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class SplashScreen extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String box = null;
        if (bundle != null) {
            box = bundle.getString("box");
        }

        if (box != null){
                Intent intent;

                if (box.contains("Get")){
                    intent = new Intent(this, ActivityLuck.class);
                } else  intent = new Intent(this, ActivityPin.class);
                startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        finish();
    }
}
