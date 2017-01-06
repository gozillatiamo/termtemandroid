package com.worldwidewealth.wealthwallet.dashboard.topup.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.worldwidewealth.wealthwallet.services.APIHelper;
import com.worldwidewealth.wealthwallet.services.APIServices;
import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.Global;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.game.ActivityGame;
import com.worldwidewealth.wealthwallet.model.DataRequestModel;
import com.worldwidewealth.wealthwallet.model.EslipRequestModel;
import com.worldwidewealth.wealthwallet.model.LoginResponseModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthwallet.until.Until;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupSlip extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private TabLayout tabLayout;
    private String mPage;
    private byte[] mImageByte;
    private Bitmap mImageBitmap;
    private APIServices services;
    private String mTransID;
    private String mFileName;

    private static final String IMAGE = "image";
    private static final String TRANSID = "transid";

    public static Fragment newInstance(byte[] imagebyte, String transid){
        Bundle bundle = new Bundle();
        bundle.putByteArray(IMAGE, imagebyte);
        bundle.putString(TRANSID, transid);
        FragmentTopupSlip fragment = new FragmentTopupSlip();
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnBack, mBtnGame, mBtnSavePic;
        private ImageView mImageSlip;
        private View mIncludeMyWallet;
        public ViewHolder(View itemview){
            mBtnBack = (Button) itemview.findViewById(R.id.btn_back_to_dashboard);
            mBtnGame = (Button) itemview.findViewById(R.id.btn_play_game);
            mImageSlip = (ImageView) itemview.findViewById(R.id.image_slip);
            mBtnSavePic = (Button) itemview.findViewById(R.id.btn_save_pic);
            mIncludeMyWallet = (View) itemview.findViewById(R.id.include_my_wallet);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mPage = getArguments().getString("page");
        mImageByte = getArguments().getByteArray(IMAGE);
        mTransID = getArguments().getString(TRANSID);
        services = APIServices.retrofit.create(APIServices.class);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        mFileName = formatter.format(now) + ".jpg";
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_slip, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

//        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_main);
//        tabLayout.setVisibility(View.GONE);
        Until.updateMyBalanceWallet(getContext(), mHolder.mIncludeMyWallet);
        initBtn();
        if (mImageBitmap != null || mTransID != null) {
            initEslip();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        onBackPress();
    }

/*
    private void initHeader(){
        Call<ResponseBody> call = services.getbalance(new RequestModel(APIServices.ACTIONGETBALANCE, new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ContentValues values = EncryptionData.getModel(response.body());
                if (values.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                    new DialogCounterAlert.DialogFromResponse(FragmentTopupSlip.this.getContext(), values.getAsString(EncryptionData.STRMODEL));
                } else {
                    LoginResponseModel loginResponseModel = new Gson().fromJson(values.getAsString(EncryptionData.STRMODEL), LoginResponseModel.class);
                    Global.setBALANCE(loginResponseModel.getBALANCE());
                    Until.setBalanceWallet(mHolder.mIncludeMyWallet);

                }

                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(FragmentTopupSlip.this.getContext(), call, this);
            }
        });

    }
*/

    private void onBackPress(){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) return true;

                return false;
            }
        });

    }

    private void initEslip(){
        mImageBitmap = BitmapFactory.decodeByteArray(mImageByte, 0, mImageByte.length);
        mHolder.mImageSlip.setImageBitmap(mImageBitmap);
        saveImage();
    }

    private void initBtn(){
        mHolder.mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
//                FragmentManager fragmentManager = FragmentTopupSlip.this.getActivity().getSupportFragmentManager();
//                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                ((AppCompatActivity)FragmentTopupSlip.this.getActivity()).getSupportActionBar().show();
//                FragmentTopupSlip.this.getFragmentManager().popBackStack();
            }
        });

        mHolder.mBtnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentTopupSlip.this.getContext(), ActivityGame.class);
                startActivity(intent);
                getActivity().finish();
//                FragmentManager fragmentManager = FragmentTopupSlip.this.getActivity().getSupportFragmentManager();
//                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                ((AppCompatActivity)FragmentTopupSlip.this.getActivity()).getSupportActionBar().show();
            }
        });

        mHolder.mBtnSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private boolean saveImage(){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File myDir = new File(root + "/WealthCounterSlip");
        myDir.mkdirs();

        File file = new File(myDir, mFileName);
        Log.e("file", "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            MediaScannerConnection.scanFile(FragmentTopupSlip.this.getContext(), new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);
            Toast.makeText(getContext(), getString(R.string.save_eslip_success), Toast.LENGTH_LONG).show();
            Call<ResponseBody> call = services.saveSlip(
                    new RequestModel(APIServices.ACTIONSAVESLIP,
                            new EslipRequestModel(mTransID)));

            APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(FragmentTopupSlip.this.getContext(), call, this);
                }
            });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            new DialogCounterAlert(getContext(), getString(R.string.error), getString(R.string.save_eslip_fail), null);
            return false;
        }

    }
}