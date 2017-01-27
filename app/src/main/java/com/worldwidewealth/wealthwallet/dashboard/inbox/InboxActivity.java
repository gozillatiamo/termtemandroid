package com.worldwidewealth.wealthwallet.dashboard.inbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthwallet.ActivityShowNotify;
import com.worldwidewealth.wealthwallet.MyFirebaseMessagingService;
import com.worldwidewealth.wealthwallet.R;

public class InboxActivity extends AppCompatActivity {

    private RecyclerView mInboxRecycler;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        initWidgets();
        initToolbar();
        initListInbox();
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


    private void initWidgets(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mInboxRecycler = (RecyclerView) findViewById(R.id.inbox_recyclear);
    }

    private void initToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListInbox(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mInboxRecycler.setLayoutManager(layoutManager);
        mInboxRecycler.setAdapter(new InboxAdapter());
    }

    private class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(InboxActivity.this).inflate(R.layout.item_inbox, parent, false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if ((position%2) == 0){
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorSelector));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InboxActivity.this, ActivityShowNotify.class);
                    intent.putExtra(MyFirebaseMessagingService.TEXT, "Itme at position");
                    intent.putExtra(MyFirebaseMessagingService.BOX, position+"");
                    overridePendingTransition(R.anim.slide_in_up, 0);
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
