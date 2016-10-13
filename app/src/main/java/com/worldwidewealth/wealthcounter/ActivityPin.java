package com.worldwidewealth.wealthcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by MyNet on 11/10/2559.
 */

public class ActivityPin extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pin);
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);

    }
}
