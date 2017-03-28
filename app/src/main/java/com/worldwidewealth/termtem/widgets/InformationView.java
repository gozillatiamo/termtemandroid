package com.worldwidewealth.termtem.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.inbox.adapter.InboxAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 03-Mar-17.
 */

public class InformationView extends FrameLayout implements View.OnClickListener,
        View.OnLongClickListener, CompoundButton.OnCheckedChangeListener{

    private CardView mInformationView;
    private View mLayoutThumbnail;
    private ImageView mImageThumbnail;
    private TextView mTextLengthVideo, mTextTitle, mTextDes, mTextDate;
    private CheckBox mCheckDelete;

    private String mTitle, mDes, mThumbnailURL, mLengthVideo;
    private Date mDate;
    private boolean isRead;
    private boolean isCheck;

    private int mThumbnailResource;
    private InformationClickListener informationClickListener;
    private InformationLongClickListener informationLongClickListener;
    private OnInformationCheckedChangedListener informationCheckedChanged;
    private InboxAdapter.InboxViewHolder holder;
    private int mPosition = -1;

    public InformationView(Context context) {
        super(context);
        setup(null);
    }

    public InformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public InformationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public InformationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(attrs);

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mTitle = this.mTitle;
        ss.mDes = this.mDes;
        ss.mLengthVideo = this.mLengthVideo;
        ss.mThumbnailURL = this.mThumbnailURL;
        ss.mThumbnailResource = this.mThumbnailResource;
        ss.mDate = this.mDate;
        ss.isRead = this.isRead;
        ss.isCheck = this.isCheck;
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
        this.mTitle = ss.mTitle;
        this.mDes = ss.mDes;
        this.mLengthVideo = ss.mLengthVideo;
        this.mThumbnailURL = ss.mThumbnailURL;
        this.mThumbnailResource = ss.mThumbnailResource;
        this.mDate = ss.mDate;
        this.isRead = ss.isRead;
        this.isCheck = ss.isCheck;

        setTitle(this.mTitle);
        setDes(this.mDes);
        setLengthVideo(this.mLengthVideo);
        setDate(this.mDate);
        setRead(this.isRead);
        setCheckDelete(this.isCheck);

        if (this.mThumbnailURL != null){
            setThumbnail(this.mThumbnailURL);
        } else if (this.mThumbnailResource != -1) {
            setThumbnail(this.mThumbnailResource);
        }


    }

    private void setup(AttributeSet attrs){
        inflate(getContext(), R.layout.widget_information_view, this);
        bindview();
        setupStyleable(attrs);
        setupview();
    }

    private void bindview(){
        mImageThumbnail = (ImageView) findViewById(R.id.image_thumbnail);
        mInformationView = (CardView) findViewById(R.id.information_view);
        mLayoutThumbnail = findViewById(R.id.layout_thumbnail);
        mTextDes = (TextView) findViewById(R.id.txt_description);
        mTextLengthVideo = (TextView) findViewById(R.id.txt_length_video);
        mTextTitle = (TextView) findViewById(R.id.txt_title);
        mTextDate = (TextView) findViewById(R.id.txt_date);
        mCheckDelete = (CheckBox) findViewById(R.id.check_delete);
    }

    private void setupStyleable(AttributeSet attrs){
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.InformationView);
            mTitle = typedArray.getString(R.styleable.InformationView_iv_title);
            mDes = typedArray.getString(R.styleable.InformationView_iv_des);
            mLengthVideo = typedArray.getString(R.styleable.InformationView_iv_length_video);
            mThumbnailResource = typedArray.getResourceId(R.styleable.InformationView_iv_thumbnail, R.drawable.termtem_logo_small);
            typedArray.recycle();
        }
    }

    private void setupview(){
        mInformationView.setOnClickListener(this);
        mInformationView.setOnLongClickListener(this);
        mCheckDelete.setOnCheckedChangeListener(this);
        setTitle(this.mTitle);
        setDes(this.mDes);
        setLengthVideo(this.mLengthVideo);
        setThumbnail(this.mThumbnailResource);
        setRead(this.isRead);
    }

    public void checkToggle(){
        mCheckDelete.toggle();
        setCheckDelete(mCheckDelete.isChecked());
    }

    public void setEnableCheckDelete(boolean enable){
        if (enable){
            mCheckDelete.setVisibility(VISIBLE);
        } else
            mCheckDelete.setVisibility(GONE);
    }
    public void setCheckDelete(boolean isCheck){
        this.isCheck = isCheck;
        mCheckDelete.setChecked(this.isCheck);
    }

    public boolean isCheckDelete(){
        return isCheck;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;

        if (!this.isRead){
            mInformationView.setCardBackgroundColor(getContext().getResources().getColor(android.R.color.holo_orange_light));
        } else {
            mInformationView.setCardBackgroundColor(getContext().getResources().getColor(android.R.color.white));
        }

    }

    public void setDate(Date date){
        this.mDate = date;
        SimpleDateFormat dest = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String resultDate = dest.format(this.mDate);
        mTextDate.setText(resultDate);
    }

    public Date getDate(){
        return mDate;
    }

    public void setTitle(String title){
        this.mTitle = title;
        mTextTitle.setText(this.mTitle);
    }

    public String getTitle(){
        return this.mTitle;
    }

    public void setDes(String des){
        this.mDes = des;
        mTextDes.setText(this.mDes);
    }

    public String getDes(){
        return this.mDes;
    }

    public void setLengthVideo(String lengthVideo){
        this.mLengthVideo = lengthVideo;
        if (mTextLengthVideo!= null) {
            mTextLengthVideo.setText(this.mLengthVideo);
            mTextLengthVideo.setVisibility(VISIBLE);
        } else
            mTextLengthVideo.setVisibility(GONE);
    }

    public String getLengthVideo(){
        return this.mLengthVideo;
    }

    public void setThumbnail(String url){
        this.mThumbnailURL = url;
        this.mThumbnailResource = -1;
        setImage(this.mThumbnailURL);
    }

    public void setThumbnail(int res){
        this.mThumbnailResource = res;
        this.mThumbnailURL = null;

        if (this.mThumbnailResource != -1) {
            setImage(this.mThumbnailResource);
        }
    }

    private void setImage(Object image){
        if(!isInEditMode()) {
            Glide.with(getContext())
                    .load(image)

                    .placeholder(android.R.color.black)
                    .crossFade()
                    .into(mImageThumbnail);
        }
    }

    public void setInformationClickListener(InformationClickListener listener, InboxAdapter.InboxViewHolder holder, int position){
        this.informationClickListener = listener;
        this.mPosition = position;
        this.holder = holder;
    }

    public void setInformationLongClickListener(InformationLongClickListener listener, InboxAdapter.InboxViewHolder holder){
        this.informationLongClickListener = listener;
        this.holder = holder;
    }

    public void setInformationCheckedChanged(OnInformationCheckedChangedListener listener, int position){
        this.informationCheckedChanged = listener;
        this.mPosition = position;
    }

    @Override
    public void onClick(View v) {
        onInformationViewClick();
    }

    private void onInformationViewClick(){
        if (informationClickListener != null){
            informationClickListener.onInformationViewClick(holder, mPosition);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (informationLongClickListener != null){
            informationLongClickListener.onInformationLongViewClick(holder, mPosition);
            return true;
        }

        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isCheck = isChecked;
        if (informationCheckedChanged != null){
            informationCheckedChanged.onCheckedChanged(buttonView, isChecked, mPosition);
        }
    }

    public interface OnInformationCheckedChangedListener{
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int positon);
    }

    public interface InformationClickListener {
        void onInformationViewClick(InboxAdapter.InboxViewHolder holder, int position);
    }

    public interface InformationLongClickListener {
        void onInformationLongViewClick(InboxAdapter.InboxViewHolder holder, int postion);
    }


    private static class SavedState extends BaseSavedState{

        String mTitle, mDes, mLengthVideo, mThumbnailURL;
        int mThumbnailResource;
        Date mDate;
        boolean isRead;
        boolean isCheck;

        public SavedState(Parcel source) {
            super(source);
            this.mTitle = source.readString();
            this.mDes = source.readString();
            this.mLengthVideo = source.readString();
            this.mThumbnailURL = source.readString();
            this.mThumbnailResource = source.readInt();
            this.mDate = (Date) source.readValue(getClass().getClassLoader());
            this.isRead = source.readByte() != 0;
            this.isCheck = source.readByte() != 0;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.mTitle);
            out.writeString(this.mDes);
            out.writeString(this.mLengthVideo);
            out.writeString(this.mThumbnailURL);
            out.writeInt(this.mThumbnailResource);
            out.writeValue(this.mDate);
            out.writeByte((byte) (this.isRead ? 1:0));
            out.writeByte((byte) (this.isCheck ? 1:0));
        }

        public static final Creator<InformationView.SavedState> CREATOR = new Creator<InformationView.SavedState>() {
            public InformationView.SavedState createFromParcel(Parcel in) {
                return new InformationView.SavedState(in);
            }

            public InformationView.SavedState[] newArray(int size) {
                return new InformationView.SavedState[size];
            }
        };

    }
}
