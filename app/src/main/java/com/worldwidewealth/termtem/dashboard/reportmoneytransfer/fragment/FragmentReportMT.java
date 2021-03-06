package com.worldwidewealth.termtem.dashboard.reportmoneytransfer.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.worldwidewealth.termtem.ActivityRegister;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.BottomSheetDialogChoicePhoto;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.PopupChoiceBank;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.NotiPayRequestModel;
import com.worldwidewealth.termtem.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.app.Activity.RESULT_OK;

/**
 * Created by MyNet on 14/10/2559.
 */

public class FragmentReportMT extends Fragment {
    private static final String TAG = FragmentReportMT.class.getSimpleName();
    private View rootView;
    private ViewHolder mHolder;
    private String mStrAmount, mStrBankStart = "", mStrBankEnd = "";
    private List<ContentValues> mListBankStart, mListBankEnd;
    private APIServices services;
    private Bitmap mBitmapImage;
    private DatePickerDialog mDateDialog;
    private TimePickerDialog mTimeDialog;
    private static long mDateTime = 0;
    private Calendar mCalender = Calendar.getInstance();
    private PopupChoiceBank mPopupBankStart, mPopupBankEnd;
    private BottomSheetDialogChoicePhoto sheetDialogFragment;
    private String imgPath = null;
    private int id = 1;
    private DialogCounterAlert mAlertConfirmUpload = null;
    private String mAttachEncode;


    private String[] mListNameBank;
    private String[] mListCodeBank;
    private TypedArray mLisIconBank;


    public static Fragment newInstance(){
        FragmentReportMT fragment = new FragmentReportMT();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        services = APIServices.retrofit.create(APIServices.class);
        services = APIServices.retrofit.newBuilder()
                .client(APIServices.client.newBuilder()
                        .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES).build()).build().create(APIServices.class);
        mCalender.setTimeInMillis(System.currentTimeMillis());
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_report_mt, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();
        Util.setupUI(rootView);
        bindListBank();
        initBtn();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = null;
            mHolder.mLayoutBtnAddImage.setVisibility(View.GONE);
            mHolder.mLoadingImage.setVisibility(View.VISIBLE);

            switch (requestCode){
                case MyApplication.REQUEST_IMAGE_CAPTURE:
//                    Bundle extras = data.getExtras();
//                    uri = Util.getImageUri((Bitmap) extras.get("data"));
                    try {
                        uri = sheetDialogFragment.getImageUri();

                        imgPath =  Util.getRealPathFromURI(uri);

                    } catch (NullPointerException e){
                        Util.backToSignIn(getActivity());
                    }


                    break;
                case MyApplication.REQUEST_IMAGE_CHOOSE:
                    uri = data.getData();
                    imgPath = Util.getRealPathFromURI(uri);

                    uri.toString().replace("com.android.gallery3d","com.google.android.gallery3d");

                    if (uri.toString().startsWith("content://com.google.android.gallery3d")
                            || uri.toString().startsWith("content://com.sec.android.gallery3d.provider") ) {

                        imgPath = Util.getPicasaImage(uri);
                    }
                    else
                        imgPath = Util.getRealPathFromURI(uri);

//                    imgPath = Util.getRealPathFromURI(uri);

                    break;
            }

            if (imgPath != null) {
                getView().findViewById(R.id.text_des_pic).setVisibility(View.GONE);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        System.gc();
                        Glide.clear(mHolder.mImagePhoto);

                        try {
                            if (getActivity() != null && isAdded())
                                Glide.with(FragmentReportMT.this).load(imgPath)
                                        .override(300, 300)
                                        .crossFade()
                                        .placeholder(R.drawable.ic_picture)
                                        .into(mHolder.mImagePhoto);
                        } catch (IllegalArgumentException e){}
                    }
                });
            }


/*
            if (imgPath != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mBitmapImage = Util.flip(Util.decodeSampledBitmapFromResource(imgPath, 300, 300), imgPath);
                        mHolder.mImagePhoto.setImageBitmap(mBitmapImage);
                        System.gc();
                    }
                }, 300);
            }
*/
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDateDialog != null && mDateDialog.isShowing()) mDateDialog.cancel();
        if (mTimeDialog != null && mTimeDialog.isShowing()) mTimeDialog.cancel();
    }

    private void bindListBank(){
        mListNameBank = getContext().getResources().getStringArray(R.array.list_name_bank_start);
        mListCodeBank = getContext().getResources().getStringArray(R.array.list_code_bank_start);
        mLisIconBank = getContext().getResources().obtainTypedArray(R.array.ic_list_bank_start);

        mListBankStart = new ArrayList<>();
        mListBankEnd = new ArrayList<>();

        for (int i = 0; i < mListNameBank.length; i++){
            ContentValues values = new ContentValues();
            values.put(PopupChoiceBank.KEY_NAME, mListNameBank[i]);
            values.put(PopupChoiceBank.KEY_ICON, mLisIconBank.getResourceId(i, -1));
            values.put(PopupChoiceBank.KEY_CODE, mListCodeBank[i]);
            mListBankStart.add(values);
        }

        mListNameBank = getContext().getResources().getStringArray(R.array.list_name_bank_end);
        mListCodeBank = getContext().getResources().getStringArray(R.array.list_code_bank_end);
        mLisIconBank = getContext().getResources().obtainTypedArray(R.array.ic_list_bank_end);

        for (int i = 0; i < mListNameBank.length; i++){
            ContentValues values = new ContentValues();
            values.put(PopupChoiceBank.KEY_NAME, mListNameBank[i]);
            values.put(PopupChoiceBank.KEY_ICON, mLisIconBank.getResourceId(i, -1));
            values.put(PopupChoiceBank.KEY_CODE, mListCodeBank[i]);
            mListBankEnd.add(values);
        }



    }

    private void initBtn(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mStrAmount = mHolder.mEditAmount.getText().toString();
                mStrBankStart = mPopupBankStart.getBank();
                mStrBankEnd = mPopupBankEnd.getBank();
//                mStrBankEnd = "KBANK";
                if (mStrAmount.equals("") || Double.parseDouble(mStrAmount) < 0.25){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_enter_amount), Toast.LENGTH_LONG).show();
                    mHolder.mScrollView.smoothScrollTo(0, 0);
                    return;
                }

                if (mDateTime == 0){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_select_date_time), Toast.LENGTH_LONG).show();
                    mHolder.mScrollView.smoothScrollTo(0, 0);
                    return;
                }

                if (mStrBankStart.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_select_bank_start), Toast.LENGTH_LONG).show();
                    mHolder.mScrollView.smoothScrollTo(0, 0);

                    return;
                }

                if (mStrBankEnd.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_select_bank_end), Toast.LENGTH_LONG).show();
                    mHolder.mScrollView.smoothScrollTo(0, 0);

                    return;
                }


                if (imgPath == null){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_add_image), Toast.LENGTH_LONG).show();
                    mHolder.mScrollView.smoothScrollTo(0, 0);

                    return;
                }
                if (mAlertConfirmUpload != null) {

                    if (!mAlertConfirmUpload.isShow()) mAlertConfirmUpload.show();

                }else if (mAlertConfirmUpload == null) {
                    mAlertConfirmUpload = new DialogCounterAlert(getContext(), getString(R.string.warning), getString(R.string.msg_waiting_upload),
                            getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (!MyApplication.isUpload(getContext(), R.string.has_upload)) {

                                mBitmapImage = Util.flip(Util.decodeSampledBitmapFromResource(imgPath, 300, 300), imgPath);

                                mAttachEncode = Util.encodeBitmapToUpload(mBitmapImage);

                                if (mBitmapImage != null && !mBitmapImage.isRecycled()) {
                                    System.gc();
                                    mBitmapImage.recycle();
                                    mBitmapImage = null;
                                }

                                final RequestModel requestModel = new RequestModel(
                                        APIServices.ACTIONNOTIPAY,
                                        new NotiPayRequestModel(mStrAmount,
                                                mDateTime,
                                                mAttachEncode,
                                                mStrBankStart,
                                                mStrBankEnd));

                                final Call<okhttp3.ResponseBody> req = services.notipay(requestModel);
                                MyApplication.showNotifyUpload(MyApplication.NOTIUPLOAD);
                                APIHelper.enqueueWithRetry(req, new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        Object responseValues = EncryptionData.getModel(null, call, response.body(), this);

                                        if (responseValues instanceof ResponseModel) {
                                            ResponseModel responseModel = (ResponseModel) responseValues;
                                            if (responseModel.getStatus() == APIServices.SUCCESS)
                                                MyApplication.uploadSuccess(MyApplication.NOTIUPLOAD, null);
                                            else
                                                setUploadFail(responseModel.getMsg());
                                        } else {
                                            setUploadFail(null);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        t.printStackTrace();
                                        //new ErrorNetworkThrowable(t).networkError(FragmentReportMT.this.getContext(), call, this);
                                        setUploadFail(null);
                                    }
                                });

                                FragmentTransaction fragmentTransaction = FragmentReportMT.this.getActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                        .replace(R.id.container_report_mt, FragmentReportMtPreview.newInstance(
                                                Double.parseDouble(mStrAmount),
                                                mAttachEncode,
                                                mHolder.mBtnDateTransfer.getText().toString(),
                                                mHolder.mBtnTimeTransfer.getText().toString(),
                                                mPopupBankStart.getValuesBankSelected(),
                                                mPopupBankEnd.getValuesBankSelected()))
                                        .addToBackStack(null);
                                fragmentTransaction.commit();

                            }
                        }
                    });
                }
                //new DialogCounterAlert.DialogProgress(getContext());
                //MyApplication.LeavingOrEntering.currentActivity = null;

            }
        });

        mHolder.mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (sheetDialogFragment == null) {
                sheetDialogFragment = new BottomSheetDialogChoicePhoto(FragmentReportMT.this);
            }

            sheetDialogFragment.show(getActivity().getSupportFragmentManager(), sheetDialogFragment.getTag());
            }
        });

        mHolder.mBtnDateTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        mHolder.mBtnTimeTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });

        mPopupBankStart = new PopupChoiceBank(getContext(), mHolder.mIncludeBankStart, mListBankStart);
        mPopupBankEnd = new PopupChoiceBank(getContext(), mHolder.mIncludeBankEnd, mListBankEnd);
    }


    private void showDateDialog(){
        if (mDateDialog == null){

            mDateDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mCalender.set(Calendar.YEAR, year);
                    mCalender.set(Calendar.MONTH, month);
                    mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    if (mCalender.getTimeInMillis() > System.currentTimeMillis()){
                        new AlertDialog.Builder(getContext())
                                .setCancelable(false)
                                .setMessage(R.string.error_date_limit)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDateDialog.show();
                                        return;
                                    }
                                }).show();
                    }

                    mHolder.mBtnDateTransfer.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                }
            },  mCalender.get(Calendar.YEAR), mCalender.get(Calendar.MONTH), mCalender.get(Calendar.DAY_OF_MONTH));
            mDateDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDateDialog.setCancelable(false);
        }

        mDateDialog.show();
    }

    private void setUploadFail(String msg){
        MyApplication.uploadFail(MyApplication.NOTIUPLOAD, msg);
    }

    private void showTimeDialog(){
        if (mTimeDialog == null){
            mTimeDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    mCalender.set(Calendar.MINUTE, minute);
                    String strHour = String.valueOf(hourOfDay);
                    String strMinute = String.valueOf(minute);

                    if (strHour.length() == 1){
                        strHour = "0"+strHour;
                    }

                    if (strMinute.length() == 1){
                        strMinute = "0"+strMinute;
                    }

                    mHolder.mBtnTimeTransfer.setText(strHour + ":" + strMinute);

                    mDateTime = mCalender.getTimeInMillis();
                }
            }, mCalender.get(Calendar.HOUR_OF_DAY), mCalender.get(Calendar.MINUTE), true);
            mTimeDialog.setTitle("");
            mTimeDialog.setCancelable(false);
        }

            mTimeDialog.show();

    }


    public class ViewHolder{
        private Button mBtnNext, mBtnDateTransfer, mBtnTimeTransfer;
        private ImageButton mBtnTakePhoto;
        private ImageView mImagePhoto;
        private EditText mEditAmount;
        private View mIncludeBankStart, mIncludeBankEnd;
        private NestedScrollView mScrollView;
        private ProgressBar mLoadingImage;
        private View mLayoutBtnAddImage;

        public ViewHolder(View itemview){
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnDateTransfer = (Button) itemview.findViewById(R.id.btn_date_transfer);
            mBtnTimeTransfer = (Button) itemview.findViewById(R.id.btn_time_transfer);
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTakePhoto = (ImageButton) itemview.findViewById(R.id.btn_take_photo);
            mImagePhoto = (ImageView) itemview.findViewById(R.id.image_photo);
            mImagePhoto.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                }
            });
            mEditAmount = (EditText) itemview.findViewById(R.id.edit_amount);
            mIncludeBankStart = (View) itemview.findViewById(R.id.include_bank_start);
            mIncludeBankEnd = (View) itemview.findViewById(R.id.include_bank_end);
            mScrollView = (NestedScrollView) itemview.findViewById(R.id.nested_scrollview);
            mLoadingImage = (ProgressBar) itemview.findViewById(R.id.progress_loading_image);
            mLayoutBtnAddImage = (View) itemview.findViewById(R.id.layout_btn_add_image);
            mImagePhoto.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (mImagePhoto.getDrawable() != null){
                        mBtnNext.setEnabled(true);
                        mLoadingImage.setVisibility(View.GONE);
                        mLayoutBtnAddImage.setVisibility(View.VISIBLE);
                        mScrollView.smoothScrollTo(0, bottom);
                    }
                }
            });
/*
            mImagePhoto.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (mImagePhoto.getDrawable() != null) {

                    }
                }
            });
*/
//

        }
    }

}
