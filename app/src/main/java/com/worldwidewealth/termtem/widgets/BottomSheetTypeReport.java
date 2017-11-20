package com.worldwidewealth.termtem.widgets;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.model.UserMenuModel;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 15-Feb-17.
 */

public class BottomSheetTypeReport extends BottomSheetDialog {

/*
    public static final int TOPUP_REPORT_MENU = 0;
    public static final int CASHIN_AGENT_REPORT_MENU = 1;

    public static final String TITLE = "title";
    public static final String ICON = "icon";
    public static final String TYPEREPORT = "typereport";
*/

    public OnResultTypeListener onResultTypeListener;
    public String mTypeCurrent = null;

//    private AppCompatButton mMenuTopup, mMenuCashInAgent;
    private RecyclerView mRecyclerSubMenu;
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private SubMenuHistoryAdapter mAdapter;
/*
    private BottomSheetBehavior mBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                dismiss();
                bottomSheetBehavior.setState(CustomBottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };
*/

    public interface OnResultTypeListener{
        void onResult(String typeReport);
    }

    public BottomSheetTypeReport(@NonNull Context context) {
        super(context);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.bottomsheet_type_report);
//        mBottomSheetBehavior = BottomSheetBehavior.from(getwi)

        initWidgets();
        bindDataSubmenu();
        setupMenu();
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog
                        .findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);

                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

                bottomSheetBehavior.setPeekHeight(bottomSheet.getMeasuredHeight());
            }
        });
    }

    @Override
    public void show() {

        if (sparseBooleanArray.size() <= 1 || mAdapter.getItemCount() == 0){
            return;
        }

        super.show();
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
        return sparseBooleanArray.size();
    }

    public String getCurrentType(){
        return mTypeCurrent;
    }

    private void initWidgets(){
        mRecyclerSubMenu = (RecyclerView) findViewById(R.id.list_submenu_history);
    }


    private void bindDataSubmenu(){

        if (Global.getInstance().getUserMenuList() == null){

            new ErrorNetworkThrowable(null).networkError(getContext(), MyApplication.getContext().getString(R.string.error_msg_data)
                    , null, null, true, new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Util.backToSignIn((AppCompatActivity)getContext());
                        }
                    });

            return;
        }
        for (UserMenuModel model : Global.getInstance().getUserMenuList()){

            if (model.getSTATUS() == null || model.getSTATUS().equals("")) return;

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
                            case VAS:
                                sparseBooleanArray.put(MenuButtonView.TYPE.valueOf(model.getBUTTON()).getType(), true);
                                break;
                            case BILL:
                                sparseBooleanArray.put(MenuButtonView.TYPE.valueOf(model.getBUTTON()).getType(), true);
                                break;


                        }
                    }

                    break;

            }

            sparseBooleanArray.put(MenuButtonView.TYPE.NOTIPAY.getType(), true);

//            sparseBooleanArray.put(MenuButtonView.TYPE.BILLPAY.getType(), true);

        }

//        createListForSubmenu(sparseBooleanArray);
    }

    private void setupMenu(){
        mRecyclerSubMenu.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new SubMenuHistoryAdapter(getContext());
        mRecyclerSubMenu.setAdapter(mAdapter);
        if (mAdapter.getItemCount() > 0)
            mTypeCurrent = mAdapter.getItem(0).getAsString(SubMenuHistoryAdapter.TYPE);
    }

    public class SubMenuHistoryAdapter extends RecyclerView.Adapter<SubMenuHistoryAdapter.ViewHolder>{

        private Context mContext;
        private List<ContentValues> mListData;

        private static final int TOPUP = 0;
        private static final int EPIN = 1;
        private static final int CASHIN = 2;
        private static final String TITLE = "title";
        private static final String TYPE = "type";
        private static final String ICON = "icon";

        public SubMenuHistoryAdapter(Context context) {
            this.mContext = context;

            mListData = new ArrayList<>();
            ContentValues values;
            values = new ContentValues();
            values.put(TITLE, getContext().getString(R.string.report_cashin));
            values.put(TYPE, ActivityReport.FUNDIN_REPORT);
            values.put(ICON, R.drawable.ic_my_cashin_report);
            mListData.add(values);

            if (sparseBooleanArray.get(MenuButtonView.TYPE.TOPUP.getType())){
                values = new ContentValues();
                values.put(TITLE, getContext().getString(R.string.report_topup));
                values.put(TYPE, ActivityReport.TOPUP_REPORT);
                values.put(ICON, R.drawable.ic_report_topup);
                mListData.add(values);
            }
            if (sparseBooleanArray.get(MenuButtonView.TYPE.EPIN.getType())){
                values = new ContentValues();
                values.put(TITLE, getContext().getString(R.string.report_epin));
                values.put(TYPE, ActivityReport.EPIN_REPORT);
                values.put(ICON, R.drawable.ic_report_epin);
                mListData.add(values);
            }

            if (sparseBooleanArray.get(MenuButtonView.TYPE.VAS.getType())){
                values = new ContentValues();
                values.put(TITLE, getContext().getString(R.string.report_vas));
                values.put(TYPE, ActivityReport.VAS_REPORT);
                values.put(ICON, R.drawable.ic_vas_report);
                mListData.add(values);
            }

            if (sparseBooleanArray.get(MenuButtonView.TYPE.BILL.getType())){
                values = new ContentValues();
                values.put(TITLE, getContext().getString(R.string.report_bill));
                values.put(TYPE, ActivityReport.BILL_REPORT);
                values.put(ICON, R.drawable.ic_report_bill);
                mListData.add(values);
            }


            if (sparseBooleanArray.get(MenuButtonView.TYPE.AGENTCASHIN.getType())){
                values = new ContentValues();
                values.put(TITLE, getContext().getString(R.string.report_cashin_agent));
                values.put(TYPE, ActivityReport.CASHIN_REPORT);
                values.put(ICON, R.drawable.ic_report_cashin);
                mListData.add(values);
            }


        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_submenu, parent, false);

            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
//            boolean isShow = false;

            holder.mIconTitle.setImageResource(mListData.get(position).getAsInteger(ICON));
            holder.mTextTitle.setText(mListData.get(position).getAsString(TITLE));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTypeCurrent = mListData.get(position).getAsString(TYPE);
                    dismiss();
                }
            });

        }

        public ContentValues getItem(int position){
            return mListData.get(position);
        }

        @Override
        public int getItemCount() {
            return mListData.size();
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
