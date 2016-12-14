package com.worldwidewealth.wealthwallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.worldwidewealth.wealthwallet.MyApplication;
import com.worldwidewealth.wealthwallet.R;

/**
 * Created by user on 14-Dec-16.
 */

public class BottomSheetDialogChoicePhoto extends BottomSheetDialogFragment {
    private ViewHolder mHolder;
    private Fragment mFragment;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_CHOOSE = 2;

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
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    mFragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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

                mFragment.startActivityForResult(pickIntent, REQUEST_IMAGE_CHOOSE);
            }
        });

    }

    private class ViewHolder{
        private Button mButtonTakePhoto, mButtonChoosePic;
        public ViewHolder(View itemView) {
            mButtonChoosePic = (Button) itemView.findViewById(R.id.btn_choose_pic);
            mButtonTakePhoto = (Button) itemView.findViewById(R.id.btn_take_photo);
        }
    }
}
