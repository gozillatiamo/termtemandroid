package com.worldwidewealth.termtem.dashboard.topup.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.AddFavRequestModel;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.LoginResponseModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.EslipRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.util.BadgeDrawable;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;

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
    private String mPhoneNo;
    private String mCarrier;
    private String mTypeToup;
    private boolean mIsFav;
    public static final String TAG = FragmentTopupSlip.class.getSimpleName();

    private static final String IMAGE = "image";
    private static final String TRANSID = "transid";
    private static final String FAVORITE = "favorite";

    public static Fragment newInstance(byte[] imagebyte, String transid, boolean isFav){
        Bundle bundle = new Bundle();
        bundle.putByteArray(IMAGE, imagebyte);
        bundle.putString(TRANSID, transid);
        bundle.putBoolean(FAVORITE, isFav);
        FragmentTopupSlip fragment = new FragmentTopupSlip();
        fragment.setArguments(bundle);
        return fragment;
    }


    public class ViewHolder{
        private Button mBtnBack, mBtnGame, mBtnSavePic;
        private ImageView mImageSlip;
        private View mIncludeMyWallet;
        private ShineButton mBtnAddFavorite;
        public ViewHolder(View itemview){
            mBtnBack = (Button) itemview.findViewById(R.id.btn_back_to_dashboard);
            mBtnGame = (Button) itemview.findViewById(R.id.btn_play_game);
            mImageSlip = (ImageView) itemview.findViewById(R.id.image_slip);
            mBtnSavePic = (Button) itemview.findViewById(R.id.btn_save_pic);
            mIncludeMyWallet = (View) itemview.findViewById(R.id.include_my_wallet);
            mBtnAddFavorite = (ShineButton) itemview.findViewById(R.id.btn_add_favorite);

            mBtnAddFavorite.setChecked(mIsFav);
            mBtnAddFavorite.setClickable(!mIsFav);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DialogCounterAlert.DialogProgress(getContext()).show();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mPage = getArguments().getString("page");
        mImageByte = getArguments().getByteArray(IMAGE);
        mTransID = getArguments().getString(TRANSID);
        mIsFav = getArguments().getBoolean(FAVORITE);

        mPhoneNo = Global.getInstance().getLastSubmitPhoneNo();
        mCarrier = Global.getInstance().getLastSubmitCarrier();
        mTypeToup = MyApplication.getTypeToup(Global.getInstance().getLastSubmitAction());

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
//        Util.updateMyBalanceWallet(getContext(), mHolder.mIncludeMyWallet);


        return rootView;
    }

    @Override
    public void onStart() {
        onBackPress();
        setupBtnFavorite();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        initBtn();
        if (Global.getInstance().getLastTranId() == null){
            getActivity().finish();
            return;
        }
        Call<ResponseBody> call = services.getbalance(new RequestModel(APIServices.ACTIONGETBALANCE, Global.getInstance().getLastSubmit().getData()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(getContext(), call, response.body(), this);

                if (values instanceof String) {
                    LoginResponseModel loginResponseModel = new Gson().fromJson((String) values, LoginResponseModel.class);
                    Global.getInstance().setBALANCE(loginResponseModel.getBALANCE());
                    Global.getInstance().setMSGREAD(loginResponseModel.getMSGREAD());
                    Util.setBalanceWallet(mHolder.mIncludeMyWallet);

                }

                if (mImageBitmap != null || mTransID != null) {
                    initEslip();
                }


//                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

        NotificationManager mNM = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.cancel(MyApplication.NOTITOPUP);

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
                    Util.setBalanceWallet(mHolder.mIncludeMyWallet);

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

    private String getService(){
        switch (mTypeToup){
            case FragmentTopup.MOBILE:
                return ActivityReport.TOPUP_REPORT;
            case FragmentTopup.PIN:
                return ActivityReport.EPIN_REPORT;
            case FragmentTopup.VAS:
                return ActivityReport.VAS_REPORT;
            case FragmentAddCreditChoice.AGENT_CASHIN:
                return ActivityReport.CASHIN_REPORT;
        }

        return null;
    }

    private void setupBtnFavorite(){
        mHolder.mBtnAddFavorite.init(getActivity());
        mHolder.mBtnAddFavorite.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                if (checked){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogWarning)
                            .setTitle(R.string.set_title_favorite)
                            .setView(R.layout.dialog_edit_text)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHolder.mBtnAddFavorite.setChecked(false);
                                }
                            })
                            .setPositiveButton(R.string.confirm, null);
                    final AlertDialog alertDialog = builder.create();


                    alertDialog.setOnShowListener(new MyShowListener(){
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            super.onShow(dialogInterface);
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText editFavoriteName = (EditText) alertDialog.findViewById(R.id.edit_text);

                                    String favName = editFavoriteName.getText().toString();
                                    if (favName == null || favName.isEmpty()){
                                        Toast.makeText(getContext(), R.string.please_set_name, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    mHolder.mBtnAddFavorite.setClickable(false);

                                    Call<ResponseBody> call = services.service(new RequestModel(
                                            APIServices.ACTION_ADD_FAV,
                                            new AddFavRequestModel(mTransID, favName, getService())
                                    ));

                                    APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                        }
                                    });

                                    alertDialog.dismiss();
                                }
                            });
                        }
                    });

                    alertDialog.show();

                }
            }
        });
    }

    private void initBtn(){
        mHolder.mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTypeToup.equals(FragmentTopup.MOBILE) && mCarrier.equals(APIServices.AIS)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.interesting_vas))
                            .setPositiveButton(getString(R.string.interested), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), ActivityTopup.class);
                                    intent.putExtra(FragmentTopup.keyTopup, FragmentTopup.VAS);
                                    intent.putExtra(ActivityTopup.KEY_CARRIER, mCarrier);
                                    intent.putExtra(ActivityTopup.KEY_PHONENO, mPhoneNo);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            })
                            .setNegativeButton(getString(R.string.ignore), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new MyShowListener());
                    alertDialog.show();
                } else {
                    getActivity().finish();
                }
//                FragmentManager fragmentManager = FragmentTopupSlip.this.getActivity().getSupportFragmentManager();
//                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                ((AppCompatActivity)FragmentTopupSlip.this.getActivity()).getSupportActionBar().show();
//                FragmentTopupSlip.this.getFragmentManager().popBackStack();
            }
        });


        mHolder.mBtnSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void saveImage(){
/*
        NotificationManager nMgr = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
*/
        if (mFileName == null) return;


        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File myDirOld = new File(root + "/WealthCounterSlip");
        File myDir = new File(root + "/TermTemSlip");
        if(myDirOld.exists()){
            Log.e(TAG, "isHasOldDir");
            myDirOld.renameTo(myDir);
        }
        myDir.mkdirs();

        File file = new File(myDir, mFileName);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            MediaScannerConnection.scanFile(FragmentTopupSlip.this.getContext(), new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);

            Toast.makeText(getContext(), getString(R.string.save_eslip_success), Toast.LENGTH_LONG).show();

            DataRequestModel dataRequestModel = Global.getInstance().getLastSubmit().getData();
            SubmitTopupRequestModel submitTopupRequestModel = (SubmitTopupRequestModel) dataRequestModel;
            EslipRequestModel eslipRequestModel = new EslipRequestModel(Global.getInstance().getLastTranId(), submitTopupRequestModel.getPHONENO());
            eslipRequestModel.setUSERID(dataRequestModel.getUSERID());
            eslipRequestModel.setTXID(dataRequestModel.getTXID());
            eslipRequestModel.setDEVICEID(dataRequestModel.getDEVICEID());
            eslipRequestModel.setAGENTID(dataRequestModel.getAGENTID());

            Call<ResponseBody> call = services.saveSlip(
                    new RequestModel(APIServices.ACTIONSAVESLIP,
                            eslipRequestModel));

            APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    DialogCounterAlert.DialogProgress.dismiss();
                    mFileName = null;
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(FragmentTopupSlip.this.getContext(), call, this);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            new DialogCounterAlert(getContext(), getString(R.string.error), getString(R.string.save_eslip_fail), null);
        }


        Global.getInstance().setLastSubmit(null, false);

    }
}
