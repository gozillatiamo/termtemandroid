package com.worldwidewealth.termtem.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.util.Util;

import java.io.File;
import java.io.IOException;

/**
 * Created by user on 14-Dec-16.
 */

public class BottomSheetDialogChoicePhoto extends BottomSheetDialogFragment {
    private ViewHolder mHolder;
    private Fragment mFragment;
    private Uri photoURI;
    public static final String TAG = BottomSheetDialogChoicePhoto.class.getSimpleName();

    public BottomSheetDialogChoicePhoto() {
    }

    @SuppressLint("ValidFragment")
    public BottomSheetDialogChoicePhoto(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottomsheet_dialog_choice_photo, null);
        dialog.setContentView(contentView);
        mHolder = new ViewHolder(contentView);

        mHolder.mButtonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.LeavingOrEntering.currentActivity = null;
                BottomSheetDialogChoicePhoto.this.dismiss();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(mFragment.getContext().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = Util.createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
//                        photoURI = FileProvider.getUriForFile(mFragment.getContext(),
//                                MyApplication.getContext().getPackageName(),
//                                photoFile);
                        photoURI = Uri.fromFile(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        mFragment.startActivityForResult(takePictureIntent, MyApplication.REQUEST_IMAGE_CAPTURE);
                    }
/*
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    mFragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
*/
                }
            }
        });

        mHolder.mButtonChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.LeavingOrEntering.currentActivity = null;
                BottomSheetDialogChoicePhoto.this.dismiss();
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                mFragment.startActivityForResult(pickIntent, MyApplication.REQUEST_IMAGE_CHOOSE);
            }
        });

    }

    public Uri getImageUri(){
        return  photoURI;
    }

    private class ViewHolder{
        private Button mButtonTakePhoto, mButtonChoosePic;
        public ViewHolder(View itemView) {
            mButtonChoosePic = (Button) itemView.findViewById(R.id.btn_choose_pic);
            mButtonTakePhoto = (Button) itemView.findViewById(R.id.btn_take_photo);
        }
    }
}
