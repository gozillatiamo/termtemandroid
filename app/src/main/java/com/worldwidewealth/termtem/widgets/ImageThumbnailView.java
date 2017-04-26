package com.worldwidewealth.termtem.widgets;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.worldwidewealth.termtem.R;

import java.util.List;

/**
 * Created by user on 26-Apr-17.
 */

public class ImageThumbnailView extends FrameLayout{

    private ImageView mThumb1, mThumb2, mThumb3, mThumb4;
    private View mLastThmb;
    private TextView mTextMore;

    private List<String> mListImage;

    public ImageThumbnailView(@NonNull Context context) {
        super(context);
        setup();
    }

    public ImageThumbnailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();

    }

    public ImageThumbnailView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageThumbnailView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superstate = super.onSaveInstanceState();
        SavedState ss = new SavedState(superstate);
        ss.mListImage = this.mListImage;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)){
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mListImage = ss.mListImage;


    }

    private void setup(){
        inflate(getContext(), R.layout.widget_thumbnail_image, this);
        bindView();
    }

    private void bindView(){
        mThumb1 = (ImageView) findViewById(R.id.image_thumbnail_1);
        mThumb2 = (ImageView) findViewById(R.id.image_thumbnail_2);
        mThumb3 = (ImageView) findViewById(R.id.image_thumbnail_3);
        mThumb4 = (ImageView) findViewById(R.id.image_thumbnail_4);
        mLastThmb = findViewById(R.id.layout_last_image);
        mTextMore = (TextView) findViewById(R.id.text_more);
    }

    public void setImageThumbnail(List<String> listImage){
        mThumb2.setVisibility(GONE);
        mThumb3.setVisibility(GONE);
        mLastThmb.setVisibility(GONE);
        mTextMore.setVisibility(GONE);

        setImage(mThumb1, listImage.get(0));

        if (listImage.size() > 1){
            mThumb2.setVisibility(VISIBLE);
            setImage(mThumb2, listImage.get(1));

        }

        if (listImage.size() > 2){
            mThumb3.setVisibility(VISIBLE);
            setImage(mThumb3, listImage.get(2));

        }

        if (listImage.size() > 3){
            mLastThmb.setVisibility(VISIBLE);
            setImage(mThumb4, listImage.get(3));
        }

        if (listImage.size() > 4){
            mTextMore.setText("+"+(listImage.size()-4));
            mTextMore.setVisibility(VISIBLE);
        }

    }

    private void setImage(ImageView imageView, String urlImage){
        Glide.with(getContext()).load(urlImage)
                .thumbnail(0.3f)
                .override(100, 100)
                .crossFade()
                .placeholder(R.drawable.ic_picture)
                .into(imageView);
    }

    private static class SavedState extends BaseSavedState{

        List<String> mListImage;

        public SavedState(Parcel source) {
            super(source);
            source.readStringList(this.mListImage);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeStringList(this.mListImage);
        }

        private static final Creator<ImageThumbnailView.SavedState> CREATOR = new Creator<ImageThumbnailView.SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
    }
}
