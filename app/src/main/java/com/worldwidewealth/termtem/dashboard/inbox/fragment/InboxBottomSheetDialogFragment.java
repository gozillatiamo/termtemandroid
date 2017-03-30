package com.worldwidewealth.termtem.dashboard.inbox.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.InboxResponse;
import com.worldwidewealth.termtem.model.ReadMsgRequest;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIServices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 30-Mar-17.
 */

public class InboxBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final String KEY_INBOX_RESPONSE = "inboxresponse";

    private InboxResponse mDataInbox;
    private APIServices services = APIServices.retrofit.create(APIServices.class);

    private TextView mTitle, mDes;
    private ImageView mBtnDel;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallbak = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN){
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

     public static InboxBottomSheetDialogFragment newInstance(InboxResponse response, int position) {
         Bundle args = new Bundle();

         InboxBottomSheetDialogFragment fragment = new InboxBottomSheetDialogFragment();
         args.putParcelable(KEY_INBOX_RESPONSE, response);
         args.putInt(InboxFragment.POSITION, position);
         fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDataInbox = getArguments().getParcelable(KEY_INBOX_RESPONSE);
        getArguments().remove(KEY_INBOX_RESPONSE);

        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_inbox_bottom_sheet, null);
        bindView(contentView);
        dialog.setContentView(contentView);

        callRead();
        bindDialogBottomSheet(contentView);
        setupView();
    }

    private void bindView(View contentView){
        mTitle = (TextView) contentView.findViewById(R.id.inbox_title);
        mDes = (TextView) contentView.findViewById(R.id.inbox_des);
        mBtnDel = (ImageView) contentView.findViewById(R.id.btn_del);
    }

    private void bindDialogBottomSheet(View contentView){

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View)contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View)contentView.getParent()).setFitsSystemWindows(true);

        if (behavior != null && behavior instanceof BottomSheetBehavior){
            ((BottomSheetBehavior)behavior).setBottomSheetCallback(mBottomSheetBehaviorCallbak);
        }

        View parent = (View) contentView.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        contentView.measure(0, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(screenHeight);

        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior)params.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallbak);
        }

        params.height = screenHeight;
        parent.setLayoutParams(params);

    }

    private void setupView(){
        mTitle.setText(mDataInbox.getTitle());
        mDes.setText(mDataInbox.getMsg());

        mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog builderInner = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogError)
                        .setMessage(getContext().getString(R.string.confirm_delete_msg))
                        .setTitle(getContext().getString(R.string.confirm_delete_title))
                        .setCancelable(false)
                        .setPositiveButton(getContext().getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                InboxBottomSheetDialogFragment.this.dismiss();
                                Intent data = new Intent();
                                data.putExtras(InboxBottomSheetDialogFragment.this.getArguments());
                                getTargetFragment().onActivityResult(InboxFragment.DELETE_INBOX,
                                        Activity.RESULT_OK,
                                        data);
                            }
                        })
                        .setNegativeButton(getContext().getString(R.string.cancel),null)
                        .create();

                builderInner.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE)
                                .setTextColor(getContext().getResources()
                                        .getColor(android.R.color.holo_red_dark));
                    }
                });
                builderInner.show();

            }
        });
    }

    private void callRead(){
        if (!mDataInbox.isReaded()) {
            Call<ResponseBody> call = services.service(
                    new RequestModel(APIServices.ACTIONREADMSG,
                            new ReadMsgRequest(mDataInbox.getMsgid())));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Intent data = new Intent();
                    data.putExtras(InboxBottomSheetDialogFragment.this.getArguments());
                    getTargetFragment().onActivityResult(InboxFragment.READ_INBOX,
                            Activity.RESULT_OK,
                            data);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }

    }
}
