package com.worldwidewealth.termtem.dashboard.inbox;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.ActivityShowNotify;
import com.worldwidewealth.termtem.MainActivity;
import com.worldwidewealth.termtem.MyFirebaseMessagingService;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.TermTemDialog;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InboxActivity extends AppCompatActivity {

    private RecyclerView mInboxRecycler;
    private Toolbar mToolbar;
    private TermTemDialog.SearchDateRangeDialog mSearchDateRange;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        initWidgets();
        initToolbar();
        initListInbox();

        mSearchDateRange = new TermTemDialog.SearchDateRangeDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mSearchDateRange.canSearch()) return;


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.inbox_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) InboxActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(InboxActivity.this.getComponentName()));
            View searchEdit = searchView.findViewById(android.support.v7.appcompat.R.id.search_edit_frame);
            searchEdit.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.editbox_background_normal));

            EditText txt = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            txt.setHintTextColor(getResources().getColor(android.R.color.darker_gray));

            ImageView close = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            close.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));


        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_date_range:
                mSearchDateRange.show();
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
        getSupportActionBar().setTitle(getString(R.string.in_box));
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
