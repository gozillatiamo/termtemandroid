package com.worldwidewealth.wealthwallet.dashboard.reportmoneytransfer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.worldwidewealth.wealthwallet.MyApplication;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.until.Until;

import static android.app.Activity.RESULT_OK;

/**
 * Created by MyNet on 14/10/2559.
 */

public class FragmentReportMT extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mStrAmount, mStrImgPath;

    public static Fragment newInstance(){
        FragmentReportMT fragment = new FragmentReportMT();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri uri = Until.getImageUri(imageBitmap);
            mStrImgPath = Until.getRealPathFromURI(uri);
            Log.e("ImgPath", mStrImgPath);

            mHolder.mImagePhoto.setImageBitmap(imageBitmap);
        }
    }

    private void initBtn(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrAmount = mHolder.mEditAmount.getText().toString();

                if (mStrAmount.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_enter_amount), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mStrImgPath == null || mStrImgPath.equals("")){
                    Toast.makeText(FragmentReportMT.this.getContext(), getString(R.string.please_add_image), Toast.LENGTH_LONG).show();
                    return;
                }

                FragmentTransaction fragmentTransaction = FragmentReportMT.this.getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.container_report_mt, FragmentReportMtPreview.newInstance())
                        .addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mHolder.mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.LeavingOrEntering.currentActivity = null;
                dispatchTakePictureIntent();
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public class ViewHolder{
        private Button mBtnNext;
        private ImageButton mBtnTakePhoto;
        private ImageView mImagePhoto;
        private EditText mEditAmount;

        public ViewHolder(View itemview){
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTakePhoto = (ImageButton) itemview.findViewById(R.id.btn_take_photo);
            mImagePhoto = (ImageView) itemview.findViewById(R.id.image_photo);
            mEditAmount = (EditText) itemview.findViewById(R.id.edit_amount);
        }
    }

}
