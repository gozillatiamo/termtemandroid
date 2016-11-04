package com.worldwidewealth.wealthcounter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mHolder;
    private String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHolder = new ViewHolder(this);
        FragmentTransaction transaction = this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, FragmentLogin.newInstance())
                .addToBackStack(null);

        transaction.commit();
    }



    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (fragment instanceof FragmentLogin) finish();
        else super.onBackPressed();
    }

    public class ViewHolder{

        private View mainContainer;

        public ViewHolder(AppCompatActivity view){
            mainContainer = (View) view.findViewById(R.id.main_container);
        }
    }

}
