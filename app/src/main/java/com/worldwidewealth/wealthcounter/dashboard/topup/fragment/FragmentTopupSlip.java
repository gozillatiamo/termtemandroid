package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.app.ProgressDialog;
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
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldwidewealth.wealthcounter.APIServices;
import com.worldwidewealth.wealthcounter.EncryptionData;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.MyApplication;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillSlip;
import com.worldwidewealth.wealthcounter.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthcounter.game.ActivityGame;
import com.worldwidewealth.wealthcounter.model.DataRequestModel;
import com.worldwidewealth.wealthcounter.model.EslipRequestModel;
import com.worldwidewealth.wealthcounter.model.LoginResponseModel;
import com.worldwidewealth.wealthcounter.model.RequestModel;
import com.worldwidewealth.wealthcounter.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthcounter.until.Until;

import org.w3c.dom.Text;

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
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_slip, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

//        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_main);
//        tabLayout.setVisibility(View.GONE);
        initBtn();
        initEslip();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initHeader();
    }

    private void initHeader(){
        Call<ResponseBody> call = services.getbalance(new RequestModel(APIServices.ACTIONGETBALANCE, new DataRequestModel()));
        call.enqueue(new Callback<ResponseBody>() {
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
                new ErrorNetworkThrowable(t).networkError(FragmentTopupSlip.this.getContext());
            }
        });

    }
    private void initEslip(){
        mImageBitmap = BitmapFactory.decodeByteArray(mImageByte, 0, mImageByte.length);
        mHolder.mImageSlip.setImageBitmap(mImageBitmap);
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
                Call<ResponseBody> call = services.saveSlip(
                        new RequestModel(APIServices.ACTIONSAVESLIP,
                                new EslipRequestModel(mTransID)));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        boolean success;
                        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                        File myDir = new File(root + "/WealthCounterSlip");
                        myDir.mkdirs();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                        Date now = new Date();
                        String fname = formatter.format(now) + ".jpg";
                        File file = new File(myDir, fname);
                        Log.e("file", "" + file);
                        if (file.exists())
                            file.delete();
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            MediaScannerConnection.scanFile(FragmentTopupSlip.this.getContext(), new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);
                            success = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            success = false;
                        }

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FragmentTopupSlip.this.getContext());
                        alertDialog.setPositiveButton(R.string.done, null);
                        if (success){
                            alertDialog.setMessage(R.string.save_slip_success);
                        } else {
                            alertDialog.setMessage(R.string.save_slip_fail);
                        }

                        alertDialog.show();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(FragmentTopupSlip.this.getContext());
                    }
                });
            }
        });

    }

}
