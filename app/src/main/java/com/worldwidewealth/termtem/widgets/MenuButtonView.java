package com.worldwidewealth.termtem.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.ActivityDashboard;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.ActivityAddCreditAgent;
import com.worldwidewealth.termtem.model.UserMenuModel;

/**
 * Created by user on 27-Feb-17.
 */

public class MenuButtonView extends FrameLayout implements View.OnClickListener{
    private CardView mCardButton;
    private ImageView mIconMenu;
    private TextView mTitleMenu;
    private MenuClickListener menuClickListener;

    public static final String SHOW = "SHOW";
    public static final String DISABLE = "DISABLE";
    public static final String HIDE = "HIDE";
    public static final String CASHIN_BUTTON = "CASHIN";
    public static final String CASHIN_AGENT = "AGENTCASHIN";
    public static final String SCAN_BUTTON = "SCAN";
    public static final String TOPUP_BUTTON = "TOPUP";
    public static final String SETUP_BUTTON = "SETUP";
    public static final String SUPPORT_BUTTON = "SUPPORT";
    public static final String NOTIPAY_BUTTON = "NOTIPAY";
    public static final String HISTORY_BUTTON = "HISTORY";
    public static final String QR_BUTTON = "QR";


    private String mTitle;
    private int mIcon;
    private String mType;
    private String mVisibility = HIDE;

    private static boolean sClickable = true;

    public MenuButtonView(Context context) {
        super(context);
        setup(null);
    }

    public MenuButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public MenuButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MenuButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(attrs);

    }

    private void setup(AttributeSet attrs){
        inflate(getContext(), R.layout.widget_menu_button, this);
        bindview();
        setupStyleable(attrs);
        setupView();
    }

    private void bindview(){
        mCardButton = (CardView) findViewById(R.id.menu_button);
        mIconMenu = (ImageView) findViewById(R.id.menu_icon);
        mTitleMenu = (TextView) findViewById(R.id.menuo_title);
    }

    private void setupStyleable(AttributeSet attrs){
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MenuButtonView);
            mTitle = typedArray.getString(R.styleable.MenuButtonView_mbv_title);
            mIcon = typedArray.getInt(R.styleable.MenuButtonView_mbv_icon, -1);
            mType = typedArray.getString(R.styleable.MenuButtonView_mbv_type);
            mVisibility = typedArray.getString(R.styleable.MenuButtonView_mbv_visibility);
            typedArray.recycle();
        }
    }

    private void setupView(){
        mCardButton.setOnClickListener(this);
        setTitle(mTitle);
        setIcon(mIcon);
        setType(mType);
        setMenuVisibility(mVisibility);
    }

    public void setTitle(String title){
        this.mTitle = title;
        mTitleMenu.setText(mTitle);
    }

    public String getTitle(){
        return this.mTitle;
    }

    public void setIcon(int resIcon){
        this.mIcon = resIcon;
        mIconMenu.setImageDrawable(getResources().getDrawable(mIcon));
    }

    public Drawable getIcon(){
        return mIconMenu.getDrawable();
    }

    public void setType(String type){
        this.mType = type;
    }

    public String getType(){
        return this.mType;
    }

    public void setMenuVisibility(String visibility){
        this.mVisibility = visibility;
        switch (this.mVisibility){
            case SHOW:
                setVisibility(View.VISIBLE);
                break;
            case HIDE:
                setVisibility(View.GONE);
                break;
            case DISABLE:
                mCardButton.setClickable(false);
                setVisibility(View.VISIBLE);
                mCardButton.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                mCardButton.setAlpha(0.2f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mCardButton.setElevation(0);
                }
                break;
        }
    }

    public String getMenuisibility(){
        return this.mVisibility;
    }

    public void setMenuClickListener(MenuClickListener listener){
        this.menuClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (sClickable) {
            sClickable = false;
            onMenuClick();
        }
    }

    private void onMenuClick(){
        switch (getType()){
            case CASHIN_AGENT:
                Intent intent = new Intent(getContext(), ActivityAddCreditAgent.class);
                intent.putExtra("type", ActivityAddCreditAgent.DEFAULT);
                ((Activity)getContext()).overridePendingTransition(R.anim.slide_in_right, 0);
                getContext().startActivity(intent);
                break;
            case CASHIN_BUTTON:
                break;
            case HISTORY_BUTTON:
                break;
            case NOTIPAY_BUTTON:
                break;
            case QR_BUTTON:
                break;
            case SCAN_BUTTON:
                break;
            case SETUP_BUTTON:
                break;
            case SUPPORT_BUTTON:
                break;
            case TOPUP_BUTTON:
                break;
            default:
                if (menuClickListener != null){
                    menuClickListener.onMenuClick();
                }


        }
        sClickable = true;
    }

    public interface MenuClickListener{
        void onMenuClick();
    }
}
