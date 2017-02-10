package com.worldwidewealth.termtem.dashboard.addCreditAgent;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;

public class ActivityAddCreditAgent extends MyAppcompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_to_agent);
        mHolder = new ViewHolder(this);
        initToolbar();
        intiRecyclerView();
    }

    @Override
    public void onBackPressed() {
        try {
            if (!getSupportFragmentManager()
                    .findFragmentById(R.id.container_add_credit)
                    .getChildFragmentManager()
                    .popBackStackImmediate()) {

                super.onBackPressed();
            }
        } catch (NullPointerException e){
            super.onBackPressed();
        } catch (IllegalStateException e){}
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void intiRecyclerView(){
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mHolder.mRecyclerAgent.setLayoutManager(manager);
        mHolder.mRecyclerAgent.setAdapter(new AgentAdapter());
    }


    private class ViewHolder{
        private Toolbar mToolbar;
        private RecyclerView mRecyclerAgent;
        public ViewHolder(Activity itemView) {
            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
            mRecyclerAgent = (RecyclerView) itemView.findViewById(R.id.recycler_agent);
        }
    }

    private class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(ActivityAddCreditAgent.this).inflate(R.layout.item_inbox, parent, false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if ((position%2) == 0){
                ((CardView)holder.itemView).setCardBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MyApplication.clickable) return;

                    MyApplication.clickable = false;

                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right)
                            .replace(R.id.container_add_credit, new FragmentAddCreditChoice())
                            .addToBackStack(null);
                    transaction.commit();

                    MyApplication.clickable = true;

                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView mIconNotify;
            private TextView mTitleNotify, mDesNotify;
            public ViewHolder(View itemView) {
                super(itemView);
                mIconNotify = (ImageView) itemView.findViewById(R.id.icon_notify);
                mTitleNotify = (TextView) itemView.findViewById(R.id.title_notify);
                mDesNotify = (TextView) itemView.findViewById(R.id.des_notify);
            }
        }
    }

}
