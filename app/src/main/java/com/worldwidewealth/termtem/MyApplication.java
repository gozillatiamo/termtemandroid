package com.worldwidewealth.termtem;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.FirebasePerformance;
import com.squareup.otto.Bus;
import com.worldwidewealth.termtem.broadcast.LocalService;
import com.worldwidewealth.termtem.broadcast.NetworkStateMonitor;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.ActivityAddCreditAgent;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;
import com.worldwidewealth.termtem.dashboard.billpayment.BillPaymentActivity;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.chat.ChatBotActivity;
import com.worldwidewealth.termtem.chat.PhotoViewActivity;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.TermTemSignIn;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.ControllerPinCode;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.content.Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT;
import static com.worldwidewealth.termtem.MyApplication.LeavingOrEntering.currentActivity;

/**
 * Created by MyNet on 17/11/2559.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Context mContext;
    private static Bus mBus;
    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;
    public static boolean clickable = true;
    public static final int NOTIUPLOAD = 1;
    public static final int NOTITOPUP = 2;
    public static boolean isUpload = false;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_CHOOSE = 2;
    private static Intent intentLocalService = null;
    public static FirebasePerformance mPerformance;


    // ฺฺBroadcastReceiver สำหรับ ดักจับ notification
    private static BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SubmitTopupRequestModel submitModel = null;
            if (Global.getInstance().hasSubmit()){
                submitModel = (SubmitTopupRequestModel)Global.getInstance().getLastSubmit().getData();
            } else return;


            try {
                if (!(intent.getExtras().containsKey("topup"))) return;

                if (intent.getExtras().getBoolean("topup")) {
                    MyApplication.uploadSuccess(MyApplication.NOTITOPUP, null);

                } else {

                    Global.getInstance().setLastSubmit(null, false);
                    MyApplication.uploadFail(MyApplication.NOTITOPUP, null);

                }
            } catch (NullPointerException e){
                NotificationManager mNM = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNM.cancel(MyApplication.NOTITOPUP);

                Global.getInstance().setLastSubmit(null, false);
            }
        }
    };

    public static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        registerActivityLifecycleCallbacks(this);
        mContext = getApplicationContext();
        ControllerPinCode.enable(false);

        mBus = new Bus();
        mPerformance = FirebasePerformance.getInstance();
        mPerformance.setPerformanceCollectionEnabled(true);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Mitr-Regular.ttf")
                .addCustomStyle(AppCompatTextView.class, android.R.attr.textViewStyle)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        try {
            PackageInfo packageInfo = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            if (Global.getInstance().getVERSIONCODE() != versionCode){
                if (Global.getInstance().getAGENTID() != null){
                    Util.logoutAPI(mContext, true);
                }

                Global.getInstance().setVERSIONCODE(versionCode);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getTypeScreenLayout(){
        int screenLayout = getContext().getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenLayout;
    }

    public static Context getContext(){
        return mContext;
    }

    public static Bus getBus() {
        return mBus;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!(activity instanceof ActivityShowNotify))
            stopService(activity);


        LeavingOrEntering.activityResumed(activity);

        startSlip(activity, null);



    }

    //เรียก เปิดหน้าสรุป

    public static boolean startSlip(Activity activity, String msg){

        if (!canUseLeaving(activity) || activity instanceof ActivityShowNotify) return false;

        if (Global.getInstance().hasSubmit() &&
                !(activity instanceof ActivityTopup)
                && !(activity instanceof ActivityAddCreditAgent)){

            if (Global.getInstance().getSubmitStatus()) {
                NotificationManager mNM = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNM.cancel(MyApplication.NOTITOPUP);

                Intent intent = new Intent(activity, ActivityTopup.class);
                intent.putExtra(FragmentTopup.keyTopup, getTypeToup(Global.getInstance().getLastSubmit().getAction()));
                intent.putExtra(FragmentTopupSlip.ERRORMSG, msg);
                intent.addFlags(FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                activity.startActivity(intent);
                return true;
            }

        }

        return false;
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {

            LeavingOrEntering.activityStopped(activity);

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        String strCurrentAtivity = (currentActivity == null) ? null:currentActivity.getLocalClassName();

        if (currentActivity == null) return;

        if ( strCurrentAtivity.equals(activity.getLocalClassName()) ) {
            DialogCounterAlert.DialogProgress.dismiss();

        }

    }


    //หยุด service สำหรับ นับถอยหลัง logout
    public static void stopService(Activity activity){
        if (MyApplication.intentLocalService != null){
            MyApplication.getContext().stopService(MyApplication.intentLocalService);
            intentLocalService = null;

            if (activity == null || !(canUseLeaving(activity))) return;

            if (Global.getInstance().getUSERNAME() == null) {
                Util.backToSignIn(activity);
                return;
            }

            final boolean isisAlreadyShowProgress = DialogCounterAlert.DialogProgress.isShow();
            if (!isisAlreadyShowProgress){
                new DialogCounterAlert.DialogProgress(activity).show();
            }

            new TermTemSignIn(activity,
                    TermTemSignIn.TYPE.RELOGIN,
                    isisAlreadyShowProgress).getTXIDfromServer();

        }

    }

    // start service นับถอยหลัง logout
    public static void startService(){
        if (MyApplication.intentLocalService != null) {
            MyApplication.getContext().stopService(MyApplication.intentLocalService);
            intentLocalService = null;
        }

        MyApplication.intentLocalService = new Intent(getContext(), LocalService.class);
        MyApplication.getContext().startService(MyApplication.intentLocalService);
    }


    public static class LeavingOrEntering {
        public static Activity currentActivity = null;

        public static void activityResumed( Activity activity ) {
            currentActivity = activity;
        }


        public static void activityStopped(final Activity activity ) {
            String strCurrentAtivity = (currentActivity == null) ? null:currentActivity.getLocalClassName();

            if (currentActivity == null || !canUseLeaving(activity)) return;

            if ( strCurrentAtivity.equals(activity.getLocalClassName()) ) {
                startService();
            }
        }


    }

    public static boolean canUseLeaving(Activity activity){

       boolean can = !(activity instanceof SplashScreenWWW |
               activity instanceof MainActivity |
               activity instanceof LockScreenActivity |
               activity instanceof ActivityRegister |
               activity instanceof ChatBotActivity |
               activity instanceof PhotoViewActivity);

        return can;
    }

    public static void unregisterReceiver(){
        try{
            getContext().unregisterReceiver(myReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showNotifyUpload(int id){

        String title = null, message = null;
        int smallicon = android.R.drawable.stat_sys_upload;

        switch (id){
            case NOTIUPLOAD:
                title = getContext().getString(R.string.title_upload);
                message = getContext().getString(R.string.msg_upload);
                smallicon = android.R.drawable.stat_sys_upload;
                break;
            case NOTITOPUP:
                if (!Global.getInstance().hasSubmit()) return;
                getContext().registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));

                RequestModel requestModel = Global.getInstance().getLastSubmit();
                SubmitTopupRequestModel submitModel = (SubmitTopupRequestModel) requestModel.getData();
                String carrier = submitModel.getCARRIER() == null ? "" : submitModel.getCARRIER();
                title = getTitleTypeToup(requestModel.getAction()) + " " + carrier
                        + " " + submitModel.getAMT() + getContext().getString(R.string.currency);
                message = getContext().getString(R.string.phone_number)+" "+submitModel.getPHONENO()+" "
                        +MyApplication.getContext().getString(R.string.processing);
                smallicon = android.R.drawable.stat_notify_sync;
                break;
        }

        mNotifyManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(MyApplication.getContext().getApplicationContext());
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.termtem_logo))
                .setSmallIcon(smallicon);

        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(id, mBuilder.build());
        isUpload = true;
    }

    public static void uploadSuccess(int id, String msg){

        isUpload = false;

        if (mBuilder == null || (mBuilder == null && (id != NOTIUPLOAD))) return;

        String title, message;
        int smallicon;

        switch (id){
            case NOTIUPLOAD:
                title = getContext().getString(R.string.title_upload_success);
                message = getContext().getString(R.string.msg_upload_success);
                smallicon = android.R.drawable.stat_sys_upload_done;

                mBuilder.setContentTitle(title);
                mBuilder.setContentText(message);
                mBuilder.setSmallIcon(smallicon);
                mBuilder.setProgress(0, 0, false);
                mBuilder.setOngoing(false);
                mBuilder.setAutoCancel(true);
                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

                mNotifyManager.notify(id, mBuilder.build());
                mBuilder = null;

                break;
            case NOTITOPUP:
                if (Global.getInstance().getLastSubmit() == null) return;
                final RequestModel requestModel = Global.getInstance().getLastSubmit();
                if (msg == null){
                    Call<ResponseBody> call = getServiceSubmit(requestModel);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Object objectValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                            String newMsg = null;
                            if (objectValues instanceof ResponseModel){
                                newMsg = ((ResponseModel)objectValues).getAppdisplay();
                            }

                            setSuccessForTopup(requestModel, newMsg);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            setSuccessForTopup(requestModel, null);
                        }
                    });
                } else
                    setSuccessForTopup(requestModel, msg);
                break;
        }


    }

    private static void setSuccessForTopup(RequestModel requestModel, String msg){
        String title, message = null;
        int smallicon;

        if (!startSlip(currentActivity, msg)) {
            SubmitTopupRequestModel submitTopupRequestModel = (SubmitTopupRequestModel) requestModel.getData();

            title = getTitleTypeToup(requestModel.getAction()) + " " +
                    submitTopupRequestModel.getCARRIER();
            smallicon = R.drawable.ic_check_circle_white;

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            ArrayList<String> events = new ArrayList<>();
            events.add(getContext().getString(R.string.success));

            if (msg != null && !msg.isEmpty()) {
                for (String str : msg.split(" ", 2)) {
                    events.add(str);
                }
            }
            events.add(getContext().getString(R.string.phone_number) + " " +
                    submitTopupRequestModel.getPHONENO());
            events.add(submitTopupRequestModel.getAMT() +
                    " " + getContext().getString(R.string.currency));

            for (int i = 0; i < events.size(); i++) {
                inboxStyle.addLine(events.get(i));
            }

            if (mBuilder == null) return;
//            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(message);
            mBuilder.setStyle(inboxStyle);
            mBuilder.setSmallIcon(smallicon);
            mBuilder.setProgress(0, 0, false);
            mBuilder.setOngoing(false);
            mBuilder.setAutoCancel(true);
            mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
            mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

            mNotifyManager.notify(NOTITOPUP, mBuilder.build());
        }

        mBuilder = null;


    }

    public static void uploadFail(int id, String msg){

        if (mBuilder == null || (mBuilder == null && (id != NOTIUPLOAD))){
            Global.getInstance().setLastSubmit(null, false);
            return;
        }

        String title = null, message = null;
        int smallicon = android.R.drawable.stat_notify_error;

        NotificationCompat.InboxStyle inboxStyle = null;
        switch (id){
            case NOTIUPLOAD:
                title = getContext().getString(R.string.title_upload_fail);
                message = msg + " " + MyApplication.getContext().getString(R.string.msg_upload_fail);
                smallicon = android.R.drawable.stat_notify_error;
                isUpload = false;

                break;
            case NOTITOPUP:
                msg = (msg == null || msg.isEmpty()) ?  MyApplication.getContext().getString(R.string.alert_topup_fail) : msg;
                RequestModel requestModel = Global.getInstance().getLastSubmit();
                SubmitTopupRequestModel submitTopupRequestModel = (SubmitTopupRequestModel) requestModel.getData();
                title = getTitleTypeToup(requestModel.getAction()) + submitTopupRequestModel.getCARRIER();

                String[] splipMsg = msg.split(" ", 2);
                ArrayList<String> events = new ArrayList<>();

                for (String str: splipMsg){
                    events.add(str);
                }

                events.add(getContext().getString(R.string.phone_number) + " " + submitTopupRequestModel.getPHONENO());
                events.add(submitTopupRequestModel.getCARRIER() + " " + submitTopupRequestModel.getAMT() + " "
                        + MyApplication.getContext().getString(R.string.currency));

                inboxStyle = new NotificationCompat.InboxStyle();

                for (int i = 0; i < events.size(); i++){
                    inboxStyle.addLine(events.get(i));
                }


                smallicon = android.R.drawable.stat_sys_warning;

                if (Global.getInstance().getStrSubmitStatus() == null){
                    mBuilder.setOngoing(true);
                    Intent retryIntent = new Intent(mContext, retryButtonListener.class);
                    PendingIntent pendingRetryIntent = PendingIntent.getBroadcast(mContext, 0,
                            retryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.addAction(R.drawable.ic_refresh, mContext.getString(R.string.retry), pendingRetryIntent);
                } else {
                    mBuilder.setOngoing(false);
                    isUpload = false;
                    Global.getInstance().setLastSubmit(null, false);
                    mBuilder.setAutoCancel(true);
                }

                break;
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        if (inboxStyle != null)
            mBuilder.setStyle(inboxStyle);

        mBuilder.setSmallIcon(smallicon);
        mBuilder.setProgress(0, 0, false);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        Log.e(TAG, "ID: "+id);
        mNotifyManager.notify(id, mBuilder.build());
        if (!isUpload) {
            mBuilder = null;
        }

    }

    public static void showCurrentStatusbar(int id){
        if (mBuilder == null) return;

        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public static boolean isUpload(Context context, int message){
        if (isUpload) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage(message)
                    .setPositiveButton(R.string.confirm, null)
                    .setCancelable(false)
                    .show();
        }
        return isUpload;
    }

    public static String getTypeToup(String action){
        switch (action){
            case APIServices.ACTIONGETOTP:
            case APIServices.ACTIONSUBMITTOPUP:
                return FragmentTopup.MOBILE;

            case APIServices.ACTION_GET_OTP_EPIN:
            case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                return FragmentTopup.PIN;

            case APIServices.ACTION_GETOTP_AGENT_CASHIN:
            case APIServices.ACTION_SUBMIT_AGENT_CASHIN:
                return FragmentAddCreditChoice.AGENT_CASHIN;

            case APIServices.ACTION_GETOTP_VAS:
            case APIServices.ACTION_SUBMIT_VAS:
                return FragmentTopup.VAS;

            case APIServices.ACTION_GETOTP_BILL:
            case APIServices.ACTION_SUBMIT_BILL:
                return BillPaymentActivity.BILLPAY;

        }

        return null;

    }

    public static String getTitleTypeToup(String action){
        switch (action){
            case APIServices.ACTIONSUBMITTOPUP:
                return MyApplication.getContext().getString(R.string.title_topup);
            case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                return MyApplication.getContext().getString(R.string.dashboard_pin);
            case APIServices.ACTION_SUBMIT_AGENT_CASHIN:
                return MyApplication.getContext().getString(R.string.add_credit_agent);
            case APIServices.ACTION_SUBMIT_VAS:
                return MyApplication.getContext().getString(R.string.vas);
            case APIServices.ACTION_SUBMIT_BILL:
                return MyApplication.getContext().getString(R.string.dashboard_bill_pay);
        }

        return null;

    }

    public static Call<ResponseBody> getServiceSubmit(RequestModel requestModel){
        APIServices services = APIServices.retrofit.create(APIServices.class);

        switch (getTypeToup(requestModel.getAction())){
            case BillPaymentActivity.BILLPAY:
                return services.billService(requestModel);
            default:
                return services.topupService(requestModel);
        }
    }


    public static class retryButtonListener extends BroadcastReceiver {
        APIServices services = APIServices.retrofit.create(APIServices.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Here", "I am here");
            topupService(Global.getInstance().getLastSubmit());


        }

        private void topupService(final RequestModel requestModel){
            final Call<ResponseBody> call = MyApplication.getServiceSubmit(requestModel);

            String title = null;
            String mTopup = null;
            SubmitTopupRequestModel submitModel = null;

            title = getTitleTypeToup(requestModel.getAction());
            mTopup = getTypeToup(requestModel.getAction());

            if (requestModel.getData() instanceof SubmitTopupRequestModel) {

                submitModel = (SubmitTopupRequestModel) requestModel.getData();

                MyApplication.showNotifyUpload(MyApplication.NOTITOPUP);

            }



            final String finalMTopup = mTopup;
            final String finalTitle = title;
            final SubmitTopupRequestModel finalSubmitModel = submitModel;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);

                            if (finalSubmitModel != null) {

                                if (responseValues == null) {
                                    Global.getInstance().setLastSubmit(null, false);
                                    return;
                                }

                                if (responseValues instanceof ResponseModel) {

                                    MyApplication.uploadSuccess(MyApplication.NOTITOPUP, ((ResponseModel)responseValues).getAppdisplay());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e(TAG, "Exception submit topup: " + t.getMessage());

                            if (finalSubmitModel != null) {
                                MyApplication.uploadFail(MyApplication.NOTITOPUP, null);
                            }

                        }
                    });
                }
            },5000);

        }

    }
}
