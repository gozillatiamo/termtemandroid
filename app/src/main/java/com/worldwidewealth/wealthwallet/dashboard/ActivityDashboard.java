package com.worldwidewealth.wealthwallet.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.Adapter.AdapterListDashboard;
import com.worldwidewealth.wealthwallet.dashboard.fragment.FragmentHome;
import com.worldwidewealth.wealthwallet.until.SimpleDividerItemDecoration;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityDashboard extends AppCompatActivity {

    private ViewHolder mHolder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mHolder = new ViewHolder(this);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboard_container, FragmentHome.newInstance())
                .addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (stackCount == 1) finish();
        else  super.onBackPressed();
    }

    public class ViewHolder{

        public ViewHolder(Activity view){
        }
    }
}
