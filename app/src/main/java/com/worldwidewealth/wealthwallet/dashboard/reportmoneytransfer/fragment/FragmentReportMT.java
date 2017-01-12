package com.worldwidewealth.wealthwallet.dashboard.reportmoneytransfer.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.worldwidewealth.wealthwallet.services.APIHelper;
import com.worldwidewealth.wealthwallet.services.APIServices;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dialog.BottomSheetDialogChoicePhoto;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.dialog.PopupChoiceBank;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.model.NotiPayRequestModel;
import com.worldwidewealth.wealthwallet.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthwallet.until.Until;

import java.util.Calendar;

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
    private APIServices services;
    private String mBitmapEncode;
    private DatePickerDialog mDateDialog;
    private TimePickerDialog mTimeDialog;
    private static long mDateTime = 0;
    private Calendar mCalender = Calendar.getInstance();
    private byte[] mImageByte;
    private PopupChoiceBank mPopupBankStart, mPopupBankEnd;
    private BottomSheetDialogChoicePhoto sheetDialogFragment;
    private String imgPath = null;


    public static Fragment newInstance(){
        FragmentReportMT fragment = new FragmentReportMT();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        services = APIServices.retrofit.create(APIServices.class);
        mCalender.setTimeInMillis(System.currentTimeMillis());
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_report_mt, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();
        Until.setupUI(rootView);
        initBtn();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = null;

            switch (requestCode){
                case BottomSheetDialogChoicePhoto.REQUEST_IMAGE_CAPTURE:
//                    Bundle extras = data.getExtras();
//                    uri = Until.getImageUri((Bitmap) extras.get("data"));
                    uri = sheetDialogFragment.getImageUri();

                    imgPath =  Until.getRealPathFromURI(uri);

                    Log.e("ImgPathFromCapture", imgPath);

                    break;
                case BottomSheetDialogChoicePhoto.REQUEST_IMAGE_CHOOSE:
                    uri = data.getData();
                    imgPath = Until.getRealPathFromURI(uri);

                    uri.toString().replace("com.android.gallery3d","com.google.android.gallery3d");

                    if (uri.toString().startsWith("content://com.google.android.gallery3d")
                            || uri.toString().startsWith("content://com.sec.android.gallery3d.provider") ) {

                        imgPath = Until.getPicasaImage(uri);
                    }
                    else
                        imgPath = Until.getRealPathFromURI(uri);

//                    imgPath = Until.getRealPathFromURI(uri);
                    Log.e("ImgPathFromChoose", imgPath);

                    break;
            }

         if (imgPath != null) {
                Log.e(TAG, imgPath+"");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmapDecode = Until.decodeSampledBitmapFromResource(imgPath, 130, 130);
                        Bitmap bitmapFilp = Until.flip(bitmapDecode, imgPath);
                        Log.e(TAG, bitmapDecode.toString()+"");
                        mBitmapEncode = Until.encodeBitmapToUpload(bitmapFilp);
                        mImageByte = Base64.decode(mBitmapEncode, Base64.DEFAULT);

                        mHolder.mImagePhoto.setImageBitmap(bitmapFilp);

                    }
                }, 2000);
            }
     }
    }

    private void initBtn(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrAmount = mHolder.mEditAmount.getText().toString();
                mStrBankStart = mPopupBankStart.getBank();
//                mStrBankEnd = mPopupBankEnd.getBank();
                mStrBankEnd = "KBANK";
                if (mStrAmount.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_enter_amount), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mDateTime == 0){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_select_date_time), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mStrBankStart.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_select_bank_start), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mStrBankEnd.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_select_bank_end), Toast.LENGTH_LONG).show();
                    return;
                }


                if (mBitmapEncode == null || mBitmapEncode.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_add_image), Toast.LENGTH_LONG).show();
                    return;
                }
                Log.e("BitmapEncode", mBitmapEncode);
                new DialogCounterAlert.DialogProgress(getContext());
                Call<okhttp3.ResponseBody> req = services.notipay(new RequestModel(
                        APIServices.ACTIONNOTIPAY,
                        new NotiPayRequestModel(mStrAmount,
                                mDateTime,
                                mBitmapEncode,
                                mStrBankStart,
                                mStrBankEnd)));

                APIHelper.enqueueWithRetry(req, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // Do Something
                        DialogCounterAlert.DialogProgress.dismiss();
                        FragmentTransaction fragmentTransaction = FragmentReportMT.this.getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.container_report_mt, FragmentReportMtPreview.newInstance(
                                        Double.parseDouble(mStrAmount),
                                        mImageByte,
                                        mHolder.mBtnDateTransfer.getText().toString(),
                                        mHolder.mBtnTimeTransfer.getText().toString(),
                                        mPopupBankStart.getPositionSelect(),
                                        3))
                                .addToBackStack(null);
                        fragmentTransaction.commit();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(FragmentReportMT.this.getContext(), call, this);
                        t.printStackTrace();
                    }
                });
            }
        });

        mHolder.mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialogFragment = new BottomSheetDialogChoicePhoto(FragmentReportMT.this);
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

        mPopupBankStart = new PopupChoiceBank(getContext(), mHolder.mIncludeBankStart);
//        mPopupBankEnd = new PopupChoiceBank(getContext(), mHolder.mIncludeBankEnd);
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

        public ViewHolder(View itemview){
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnDateTransfer = (Button) itemview.findViewById(R.id.btn_date_transfer);
            mBtnTimeTransfer = (Button) itemview.findViewById(R.id.btn_time_transfer);
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTakePhoto = (ImageButton) itemview.findViewById(R.id.btn_take_photo);
            mImagePhoto = (ImageView) itemview.findViewById(R.id.image_photo);
            mEditAmount = (EditText) itemview.findViewById(R.id.edit_amount);
            mIncludeBankStart = (View) itemview.findViewById(R.id.include_bank_start);
//            mIncludeBankEnd = (View) itemview.findViewById(R.id.include_bank_end);

        }
    }

}
