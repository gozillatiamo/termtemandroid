package com.worldwidewealth.wealthwallet.dashboard.reportmoneytransfer.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.worldwidewealth.wealthwallet.APIServices;
import com.worldwidewealth.wealthwallet.MyApplication;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dialog.BottomSheetDialogChoicePhoto;
import com.worldwidewealth.wealthwallet.model.DataRequestModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.until.Until;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by MyNet on 14/10/2559.
 */

public class FragmentReportMT extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private String mStrAmount, mStrImgPath;
    private APIServices services;

    public static Fragment newInstance(){
        FragmentReportMT fragment = new FragmentReportMT();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        services = APIServices.retrofit.create(APIServices.class);
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
            switch (requestCode){
                case BottomSheetDialogChoicePhoto.REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();

                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mHolder.mImagePhoto.setImageBitmap(imageBitmap);
                    uri = Until.getImageUri(imageBitmap);
                    mStrImgPath = Until.getRealPathFromURI(uri);
                    Log.e("ImgPathFromCapture", mStrImgPath);

                    break;
                case BottomSheetDialogChoicePhoto.REQUEST_IMAGE_CHOOSE:

                        uri = data.getData();
                        mStrImgPath = Until.getRealPathFromURI(uri);
                        Log.e("ImgPathFromChoose", mStrImgPath);
                        Bitmap bm = Until.decodeSampledBitmapFromResource(mStrImgPath, 300, 300);
                        Bitmap bmFilp = Until.flip(bm, mStrImgPath);
                        Log.e("BitmapChooseImg", bm+"");
                        mHolder.mImagePhoto.setImageBitmap(bmFilp);
                    break;
            }

            if (uri != null){
                mStrImgPath = Until.getRealPathFromURI(uri);
                Log.e("ImgPath", mStrImgPath);
            }
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

                File file = new File(mStrImgPath);
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
//                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

                Call<okhttp3.ResponseBody> req = services.postImage(body, new RequestModel(APIServices.ACTIONCHANGEPASSWORD, new DataRequestModel()));
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // Do Something
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
