package com.worldwidewealth.termtem.widgets;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.UserMenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 15-Feb-17.
 */

public class BottomSheetTypeReport extends BottomSheetDialog {

    public static final int TOPUP_REPORT_MENU = 0;
    public static final int CASHIN_AGENT_REPORT_MENU = 1;

    public static final String TOPUP_REPORT = "TOPUP";
    public static final String CASHIN_REPORT = "CASHIN";
    public static final String EPIN_REPORT = "EPIN";
    public static final String TITLE = "title";
    public static final String ICON = "icon";
    public static final String TYPEREPORT = "typereport";

    public OnResultTypeListener onResultTypeListener;
    public String mTypeCurrent = TOPUP_REPORT;

//    private AppCompatButton mMenuTopup, mMenuCashInAgent;
    private RecyclerView mRecyclerSubMenu;
    private List<ContentValues> mListSubmenu;

    public interface OnResultTypeListener{
        void onResult(String typeReport);
    }

    public BottomSheetTypeReport(@NonNull Context context) {
        super(context);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.bottomsheet_type_report);
        mListSubmenu = new ArrayList<>();

        initWidgets();
        bindDataSubmenu();
        setupMenu();

    }

    @Override
    public void show() {

        if (mListSubmenu.size() <= 1){
            return;
        }

        super.show();
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (onResultTypeListener != null){
            onResultTypeListener.onResult(mTypeCurrent);
        }
    }

    public void setOnResultTypeListener(OnResultTypeListener listener){
        this.onResultTypeListener = listener;
    }

    public int getMenuSize(){
        return mListSubmenu.size();
    }

    private void initWidgets(){
        mRecyclerSubMenu = (RecyclerView) findViewById(R.id.list_submenu_history);
    }

    private void bindDataSubmenu(){
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

        for (UserMenuModel model : Global.getInstance().getUserMenuList()){

            MenuButtonView.VISIBILITY visibility = MenuButtonView.VISIBILITY.valueOf(model.getSTATUS());
            switch (visibility){
                case SHOW:

                    MenuButtonView.TYPE type = MenuButtonView.TYPE.asTYPE(model.getBUTTON());
                    if (type != null) {
                        switch (type) {
                            case TOPUP:
                            case EPIN:
                                sparseBooleanArray.put(MenuButtonView.TYPE.valueOf(model.getBUTTON()).getType(), true);
                                break;

                            case AGENTCASHIN:
                            case SCAN:
                                sparseBooleanArray.put(MenuButtonView.TYPE.AGENTCASHIN.getType(), true);
                                break;
                        }
                    }

                    break;

            }
        }

        createListForSubmenu(sparseBooleanArray);
    }

    private void createListForSubmenu(SparseBooleanArray sparseBooleanArray){

        String title = null, type = null;
        int icon = -1;

        for (int i = 0; i < sparseBooleanArray.size(); i++){
            if (sparseBooleanArray.valueAt(i)){
                MenuButtonView.TYPE menuType = MenuButtonView.TYPE.values()[sparseBooleanArray.keyAt(i)];
                switch (menuType){
                    case TOPUP:
                        title = getContext().getString(R.string.report_topup);
                        icon = R.drawable.ic_topup;
                        type = TOPUP_REPORT;
                        break;
                    case EPIN:
                        title = getContext().getString(R.string.report_epin);
                        icon = R.drawable.ic_pin_code;
                        type = EPIN_REPORT;
                        break;
                    case AGENTCASHIN:
                        title = getContext().getString(R.string.report_cashin_agent);
                        icon = R.drawable.ic_agent_cashin_primary;
                        type = CASHIN_REPORT;
                        break;
                }
                ContentValues values = new ContentValues();
                values.put(TITLE, title);
                values.put(ICON, icon);
                values.put(TYPEREPORT, type);
                mListSubmenu.add(values);
            }
        }
    }

    private void setupMenu(){
        mRecyclerSubMenu.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerSubMenu.setAdapter(new SubMenuHistoryAdapter());
    }

    public class SubMenuHistoryAdapter extends RecyclerView.Adapter<SubMenuHistoryAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_submenu, parent, false);

            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mIconTitle.setImageResource(getItem(position).getAsInteger(ICON));
            holder.mTextTitle.setText(getItem(position).getAsString(TITLE));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTypeCurrent = getItem(position).getAsString(TYPEREPORT);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mListSubmenu.size();
        }

        public ContentValues getItem(int postion){
            return mListSubmenu.get(postion);
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView mIconTitle;
            private TextView mTextTitle;
            public ViewHolder(View itemView) {
                super(itemView);
                mIconTitle = (ImageView) itemView.findViewById(R.id.icon_submenu);
                mTextTitle = (TextView) itemView.findViewById(R.id.title_submenu);
            }
        }
    }
}
