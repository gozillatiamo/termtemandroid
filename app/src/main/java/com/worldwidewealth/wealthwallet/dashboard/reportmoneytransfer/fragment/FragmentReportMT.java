package com.worldwidewealth.wealthwallet.dashboard.reportmoneytransfer.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.worldwidewealth.wealthwallet.APIServices;
import com.worldwidewealth.wealthwallet.MyApplication;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dialog.BottomSheetDialogChoicePhoto;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.model.DataRequestModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.model.RequestUploadImage;
import com.worldwidewealth.wealthwallet.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthwallet.until.Until;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

import static android.app.Activity.RESULT_OK;

/**
 * Created by MyNet on 14/10/2559.
 */

public class FragmentReportMT extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private String mStrReference, mStrAmount;
    private APIServices services;
    private String mBitmapEncode;
    private DatePickerDialog mDateDialog;
    private TimePickerDialog mTimeDialog;
    private long mDateTime = 0;
    private Calendar mCalender = Calendar.getInstance();


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

        initBtn();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = null;
            String imgPath = null;
            switch (requestCode){
                case BottomSheetDialogChoicePhoto.REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    uri = Until.getImageUri((Bitmap) extras.get("data"));
                    imgPath =  Until.getRealPathFromURI(uri);

                    Log.e("ImgPathFromCapture", imgPath);

                    break;
                case BottomSheetDialogChoicePhoto.REQUEST_IMAGE_CHOOSE:

                        uri = data.getData();
                        imgPath = Until.getRealPathFromURI(uri);
                        Log.e("ImgPathFromChoose", imgPath);

                    break;
            }

            if (imgPath != null) {
                Bitmap bitmapDecode = Until.decodeSampledBitmapFromResource(imgPath, 200, 200);
                Bitmap bitmapFilp = Until.flip(bitmapDecode, imgPath);
                mBitmapEncode = Until.encodeBitmapToUpload(bitmapFilp);
                byte[] imagebyte = Base64.decode(mBitmapEncode, Base64.DEFAULT);

                mHolder.mImagePhoto.setImageBitmap(BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length));
            }
        }
    }

    private void initBtn(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrReference = mHolder.mEditReference.getText().toString();
                mStrAmount = mHolder.mEditAmount.getText().toString();

                if (mStrReference.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_enter_reference), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mStrAmount.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_enter_amount), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mDateTime == 0){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_select_date_time), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mBitmapEncode == null || mBitmapEncode.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_add_image), Toast.LENGTH_LONG).show();
                    return;
                }

                new DialogCounterAlert.DialogProgress(getContext());
                Call<okhttp3.ResponseBody> req = services.postImage(new RequestModel(
                        APIServices.ACTIONUPLOADIMAGE,
                        new RequestUploadImage(mStrReference,
                                mStrAmount,
                                mDateTime,
                                mBitmapEncode)));

                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // Do Something
                        DialogCounterAlert.DialogProgress.dismiss();
                        FragmentTransaction fragmentTransaction = FragmentReportMT.this.getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.container_report_mt, FragmentReportMtPreview.newInstance())
                                .addToBackStack(null);
                        fragmentTransaction.commit();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(getContext());
                        t.printStackTrace();
                    }
                });
            }
        });

        mHolder.mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment sheetDialogFragment = new BottomSheetDialogChoicePhoto(FragmentReportMT.this);
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
    }

    private void showDateDialog(){
        if (mDateDialog == null){

            mDateDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mCalender.set(Calendar.YEAR, year);
                    mCalender.set(Calendar.MONTH, month);
                    mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mHolder.mBtnDateTransfer.setText(dayOfMonth+"/"+month+"/"+year);
                }
            },  mCalender.get(Calendar.YEAR), mCalender.get(Calendar.MONTH), mCalender.get(Calendar.DAY_OF_MONTH));
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
                    mHolder.mBtnTimeTransfer.setText(hourOfDay+":"+minute);
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
        private EditText mEditAmount, mEditReference;

        public ViewHolder(View itemview){
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnDateTransfer = (Button) itemview.findViewById(R.id.btn_date_transfer);
            mBtnTimeTransfer = (Button) itemview.findViewById(R.id.btn_time_transfer);
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTakePhoto = (ImageButton) itemview.findViewById(R.id.btn_take_photo);
            mImagePhoto = (ImageView) itemview.findViewById(R.id.image_photo);
            mEditAmount = (EditText) itemview.findViewById(R.id.edit_amount);
            mEditReference = (EditText) itemview.findViewById(R.id.edit_reference);

        }
    }

}
