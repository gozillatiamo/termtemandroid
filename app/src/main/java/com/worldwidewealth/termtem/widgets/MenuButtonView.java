package com.worldwidewealth.termtem.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.OtherMenuFragment;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.ActivityAddCreditAgent;
import com.worldwidewealth.termtem.dashboard.billpayment.BillPaymentActivity;
import com.worldwidewealth.termtem.dashboard.mPayStation.SelectChoiceMpayActivity;
import com.worldwidewealth.termtem.dashboard.myqrcode.ActivityMyQrCode;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.dashboard.reportmoneytransfer.ActivityReportMT;
import com.worldwidewealth.termtem.dashboard.scan.ActivityScan;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.DialogHelp;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.ChangePasswordRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 27-Feb-17.
 */

public class MenuButtonView extends FrameLayout implements View.OnClickListener{
    private CardView mCardButton;
    private AppCompatImageView mIconMenu;
    private TextView mTitleMenu;
    private MenuClickListener menuClickListener;

    private DialogHelp mDialogHelp;
    private Dialog mDialogSetting;
    private AlertDialog mAlertChangePass;

    private APIServices services;

    public enum TYPE {
        CASHIN(0),
        AGENTCASHIN(1),
        SCAN(2),
        TOPUP(3),
        SETUP(4),
        SUPPORT(5),
        NOTIPAY(6),
        HISTORY(7),
        EPIN(8),
        OTHER(9),
        AR(10),
        VAS(11),
        BILLPAY(12);

        private int type;
        TYPE(int i) {
            this.type = i;
        }

        public int getType() {
            return type;
        }

        public static TYPE asTYPE(String str) {
            for (TYPE type : TYPE.values()) {
                if (type.name().equalsIgnoreCase(str))
                    return type;
            }
            return null;
        }
    }

    public enum VISIBILITY{
        SHOW(0),
        DISABLE(1),
        HIDE(2);

        private int visibility;
        VISIBILITY(int i) {
            this.visibility = i;
        }

        public int getVisibility() {
            return visibility;
        }
    }

    private String mTitle;
    private int mIcon;
    private int mType;
    private int mVisibility;
    private static boolean canCashIn = false;
    private static int mAgentCashInVisible = 2;
    private static int mScanVisible = 2;


    public static final String TAG = MenuButtonView.class.getSimpleName();

    private static boolean sClickable = true;

    public static boolean issClickable() {
        return sClickable;
    }

    public static void setsClickable(boolean sClickable) {
        MenuButtonView.sClickable = sClickable;
    }

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

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mTitle = this.mTitle;
        ss.mIcon = this.mIcon;
        ss.mType = this.mType;
        ss.mVisibility = this.mVisibility;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)){
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mTitle = ss.mTitle;
        this.mIcon = ss.mIcon;
        this.mType = ss.mType;
        this.mVisibility = ss.mVisibility;
        setTitle(mTitle);
        setIcon(mIcon);
        setType(mType);
        setMenuVisibility(mVisibility);
    }


    private void setup(AttributeSet attrs){
        if(!isInEditMode()) {
            services = APIServices.retrofit.create(APIServices.class);
        }
        inflate(getContext(), R.layout.widget_menu_button, this);
        bindview();
        setupStyleable(attrs);
        setupView();
        if (!isInEditMode()) {
            binddialog();
        }

    }

    private void binddialog(){
        if (mType == TYPE.SUPPORT.getType()) {
            mDialogHelp = new DialogHelp(getContext());
            mDialogHelp.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    setsClickable(true);
                }
            });
        } else if(mType == TYPE.SETUP.getType()){
            mDialogSetting = new Dialog(getContext());
            mDialogSetting.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialogSetting.setContentView(R.layout.dialog_setting);
            mDialogSetting.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    setsClickable(true);
                }
            });
            Button btnChangePassword = (Button) mDialogSetting.findViewById(R.id.btn_forgot_password);
            Button btnMyQR = (Button) mDialogSetting.findViewById(R.id.btn_my_qr);
            btnMyQR.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ActivityMyQrCode.class);
                    ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_right, 0);
                    getContext().startActivity(intent);

                }
            });

            btnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDialogChangePassword();
                }
            });
        }

    }

    public void dismiss(){
        if(mType == TYPE.SUPPORT.getType()){
            if (mDialogHelp != null) {
                mDialogHelp.dismiss();
            }
        } else if(mType == TYPE.SETUP.getType()){
            if (mDialogSetting != null) {
                mDialogSetting.dismiss();
                if (mAlertChangePass != null)
                    mAlertChangePass.dismiss();
            }
        }
    }

    private void initDialogChangePassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View dialogView  = inflater.inflate(R.layout.dialog_change_password, null);

        final EditText editNewPass = (EditText) dialogView.findViewById(R.id.edit_new_password);
        final EditText editNewPassAgain = (EditText) dialogView.findViewById(R.id.edit_new_password_again);

        builder.setView(dialogView);
        builder.setTitle(R.string.change_password);
        builder.setPositiveButton(R.string.confirm, null);
        builder.setNegativeButton(R.string.cancel, null);

        mAlertChangePass = builder.create();

        mAlertChangePass.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button confirm = mAlertChangePass.getButton(AlertDialog.BUTTON_POSITIVE);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (editNewPass.getText().length() < 8 || editNewPassAgain.length() < 8 ){
                            Toast.makeText(getContext(), R.string.please_enter_data, Toast.LENGTH_LONG).show();
                        } else if (!editNewPass.getText().toString()
                                .equals(editNewPassAgain.getText().toString())){
                            Toast.makeText(getContext(), R.string.password_not_same, Toast.LENGTH_LONG).show();
                        } else {
                            Call<ResponseModel> call = services.CHANGEPASSWORD(new RequestModel(APIServices.ACTIONCHANGEPASSWORD,
                                    new ChangePasswordRequestModel(
                                            EncryptionData.EncryptData(editNewPass.getText().toString(),
                                                    Global.getInstance().getDEVICEID()+Global.getInstance().getTXID()))
                            ));

                            APIHelper.enqueueWithRetry(call, new Callback<ResponseModel>() {
                                @Override
                                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                    if (response.body().getStatus() == APIServices.SUCCESS){
                                        mAlertChangePass.dismiss();
                                        new DialogCounterAlert(getContext(),
                                                response.body().getMsg(),
                                                getContext().getString(R.string.change_password_success),
                                                null);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseModel> call, Throwable t) {
                                    new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
                                }
                            });
                        }

                    }
                });
            }
        });

        mAlertChangePass.show();

    }


    private void bindview(){
        mCardButton = (CardView) findViewById(R.id.menu_button);
        mIconMenu = (AppCompatImageView) findViewById(R.id.menu_icon);
        mTitleMenu = (TextView) findViewById(R.id.menuo_title);
    }

    private void setupStyleable(AttributeSet attrs){
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MenuButtonView);
            mTitle = typedArray.getString(R.styleable.MenuButtonView_mbv_title);

            mIcon = typedArray.getResourceId(R.styleable.MenuButtonView_mbv_icon, R.drawable.ic_people);
            mType = typedArray.getInt(R.styleable.MenuButtonView_mbv_type, -1);
            mVisibility = typedArray.getInt(R.styleable.MenuButtonView_mbv_visibility, VISIBILITY.HIDE.getVisibility());

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
        mIconMenu.setImageResource(mIcon);

    }

    public ImageView getImageView(){
        return mIconMenu;
    }

    public void setType(int type){
        this.mType = type;
    }

    public String getType(){
        return TYPE.values()[this.mType].name();
    }

    public void setMenuVisibility(int visibility){
        this.mVisibility = visibility;

        if (mType == TYPE.AGENTCASHIN.getType()) mAgentCashInVisible = mVisibility;
        if (mType == TYPE.SCAN.getType()) mScanVisible = mVisibility;

        if (mAgentCashInVisible != VISIBILITY.SHOW.getVisibility() &&
                mScanVisible != VISIBILITY.SHOW.getVisibility()) {
            canCashIn = false;
        } else
            canCashIn = true;



        if (this.mVisibility == -1){
            this.mVisibility = VISIBILITY.HIDE.getVisibility();
        }
        switch (VISIBILITY.values()[this.mVisibility]){
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
        return VISIBILITY.values()[this.mVisibility].name();
    }

    public void setMenuClickListener(MenuClickListener listener){
        this.menuClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (issClickable()) {
            setsClickable(false);
            onMenuClick();
        }
    }

    private void onMenuClick(){
        if (mType == -1){
            if (menuClickListener != null){
                menuClickListener.onMenuClick();
            }
        } else {
            Intent intent = null;
            switch (TYPE.values()[this.mType]) {
                case AGENTCASHIN:
                    intent = new Intent(getContext(), ActivityAddCreditAgent.class);
                    intent.putExtra("type", ActivityAddCreditAgent.DEFAULT);
                    break;
                case CASHIN:
                    intent = new Intent(getContext(), SelectChoiceMpayActivity.class);
                    break;
                case HISTORY:
                    intent = new Intent(getContext(), ActivityReport.class);
                    intent.putExtra(ActivityReport.CASHIN_REPORT, canCashIn);
                    break;
                case NOTIPAY:
                    intent = new Intent(getContext(), ActivityReportMT.class);
                    break;
                case SCAN:
                    intent = new Intent(getContext(), ActivityScan.class);
                    break;
                case SETUP:
                    mDialogSetting.show();
                    break;
                case SUPPORT:
                    mDialogHelp.show();
                    break;
                case TOPUP:
                    intent = new Intent(getContext(), ActivityTopup.class);
                    intent.putExtra(FragmentTopup.keyTopup, FragmentTopup.MOBILE);
                    break;
                case EPIN:
                    intent = new Intent(getContext(), ActivityTopup.class);
                    intent.putExtra(FragmentTopup.keyTopup, FragmentTopup.PIN);
                    break;
                case OTHER:
                    ((AppCompatActivity)getContext()).findViewById(R.id.logo_menu).setVisibility(VISIBLE);
                    ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                    android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.layout_main_content, OtherMenuFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    setsClickable(true);
                    break;
                case AR:
//                    intent = new Intent(getContext(), UnityPlayerNativeActivity.class);
                    break;
                case VAS:
                    intent = new Intent(getContext(), ActivityTopup.class);
                    intent.putExtra(FragmentTopup.keyTopup, FragmentTopup.VAS);
                    break;
                case BILLPAY:
                    intent = new Intent(getContext(), BillPaymentActivity.class);
                    break;

            }

            switch (TYPE.values()[this.mType]){
                case TOPUP:
                case EPIN:
                case AGENTCASHIN:
                case SCAN:
                case VAS:
                case BILLPAY:
                    if (Global.getInstance().hasSubmit()) {
                        showDialogHasProcess(getContext());
                        return;
                    }

                    break;
            }

            if (intent != null) {
                ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_right, 0);
                getContext().startActivity(intent);
            }
        }
    }

    public static  void showDialogHasProcess(Context context){
         AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogWarning)
                .setTitle(R.string.warning)
                .setMessage(R.string.msg_has_process_service)
                .setPositiveButton(R.string.confirm, null)
                .setCancelable(false);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new MyShowListener(){
            @Override
            public void onShow(DialogInterface dialogInterface) {
                super.onShow(dialogInterface);
                ((TextView)alertDialog.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setsClickable(true);
            }
        });

        alertDialog.show();

        MyApplication.showCurrentStatusbar(MyApplication.NOTITOPUP);

    }

    public interface MenuClickListener{
        void onMenuClick();
    }

    private static class SavedState extends BaseSavedState{

        String mTitle;
        int mIcon;
        int mType;
        int mVisibility;

        public SavedState(Parcel source) {
            super(source);
            this.mTitle = source.readString();
            this.mIcon = source.readInt();
            this.mType = source.readInt();
            this.mVisibility = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.mTitle);
            out.writeInt(this.mIcon);
            out.writeInt(this.mType);
            out.writeInt(this.mVisibility);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
