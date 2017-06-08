package com.worldwidewealth.termtem.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.EslipRequestModel;
import com.worldwidewealth.termtem.model.LoginResponseModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.SplashScreenWWW;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MyNet on 17/10/2559.
 */

public class Util {
    public static final String TAG = Util.class.getSimpleName();

    public static RequestBody encode(final RequestBody body){
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try {
                    Buffer buffer = new Buffer();
                    body.writeTo(buffer);
                    String encoded = Base64.encodeToString(buffer.readByteArray(), Base64.NO_WRAP);
                    byte[] converted = new StringBuilder(encoded).reverse().toString().getBytes();
                    String decoded = Util.decode(new StringBuilder(encoded).reverse().toString());

                    System.gc();
                    sink.write(converted);
                    buffer.close();
                    sink.close();
                }catch (OutOfMemoryError e){
                }
            }
        };
    }

    public static String decode(String strEncode){
        String jsonconvert = new StringBuilder(strEncode).reverse().toString();
        byte[] decode = Base64.decode(jsonconvert, Base64.DEFAULT);
        return new String(decode);
    }


    public static void setBalanceWallet(View myWallet){

        if (Global.getInstance().getTXID() == null) return;

        TextView balanceDecimal = (TextView) myWallet.findViewById(R.id.txt_balance_decimal);
        TextView balanceInteger = (TextView) myWallet.findViewById(R.id.txt_balance_integer);
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        String[] balance = format.format(Global.getInstance().getBALANCE()).split("\\.");
        balanceInteger.setText(balance[0]);
        balanceDecimal.setText("."+balance[1]);

    }

    public static void  updateMyBalanceWallet(final Context context, final View includeMywallet, final LayerDrawable iconNoti){
        APIServices services = APIServices.retrofit.create(APIServices.class);
        if (Global.getInstance().getTXID() == null) return;
        Call<ResponseBody> call = services.getbalance(new RequestModel(APIServices.ACTIONGETBALANCE, new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(context, call, response.body(), this);

                if (values instanceof String){
                    LoginResponseModel loginResponseModel = new Gson().fromJson((String)values, LoginResponseModel.class);
                    Global.getInstance().setBALANCE(loginResponseModel.getBALANCE());
                    Global.getInstance().setMSGREAD(loginResponseModel.getMSGREAD());
                    setBalanceWallet(includeMywallet);

                    if (iconNoti != null){
                        Log.e(TAG, "MSGREAD: "+loginResponseModel.getMSGREAD());
                        BadgeDrawable.setBadgeCount(context, iconNoti, loginResponseModel.getMSGREAD());
                    }
                }

//                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

        ImageButton btnRefreshWallet = (ImageButton) includeMywallet.findViewById(R.id.btn_refresh_wallet);
        btnRefreshWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                float ROTATE_FROM = 0.0f;
                final float ROTATE_TO = 360.0f;
                RotateAnimation rotateAnimation = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration((long) 1000);
                v.startAnimation(rotateAnimation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateMyBalanceWallet(context, includeMywallet, iconNoti);
                        v.clearAnimation();
                    }
                }, 2000);
            }
        });

    }

    public static void updateMyBalanceWallet(final Context context, final View includeMywallet){
        updateMyBalanceWallet(context, includeMywallet, null);
    }


    public static class JsonDateDeserializer implements JsonDeserializer<Date> {

        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String s = json.getAsJsonPrimitive().getAsString();
            long l = Long.parseLong(s.substring(6, s.length() - 2));
            Date d = new Date(l);
            return d;
        }
    }

    public static Fragment getFragmentFromViewpager(FragmentManager fm, ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return  fm.findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    public static String convertToStringRequest(RequestBody body){
        try {
            final RequestBody copy = body;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return null;
            return buffer.readUtf8();
        } catch (final IOException e) {
            return null;
        }

    }

    public static void logoutAPI(final Context context, final boolean clearData){
        if (Global.getInstance().getTXID() == null) return;
        APIServices services = APIServices.retrofit.create(APIServices.class);
        Call<ResponseBody> call = services.logout(new RequestModel(APIServices.ACTIONLOGOUT,
                new DataRequestModel()));

        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(null, call, response.body(), this);
                if (values == null) return;

                Global.getInstance().clearUserData(clearData);

                if (!clearData) {
                    new TermTemSignIn(context,
                            TermTemSignIn.TYPE.RELOGIN,
                            new DialogCounterAlert.DialogProgress(context).show()).getTXIDfromServer();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    public static void backToSignIn(Activity activity){
        MyApplication.LeavingOrEntering.currentActivity = null;
        Intent intent = new Intent(activity.getApplicationContext(), SplashScreenWWW.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }


    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static String getRealPathFromURI(Uri uri) {
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = MyApplication.getContext().getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        // for other file managers
        return uri.getPath();
    }

    public static String getPicasaImage(Uri imageUri) {

        File cacheDir;
        // if the device has an SD card
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),".OCFL311");
        } else {
            // it does not have an SD card
            cacheDir = MyApplication.getContext().getCacheDir();
        }

        if(!cacheDir.exists()) cacheDir.mkdirs();

        File f = new File(cacheDir, "tempPicasa");

        try {

            InputStream is = null;
            if (imageUri.toString().startsWith("content://com.google.android.gallery3d")
                    || imageUri.toString().startsWith("content://com.sec.android.gallery3d.provider")) {

                is = MyApplication.getContext().getContentResolver().openInputStream(imageUri);
            } else {
                is = new URL(imageUri.toString()).openStream();
            }

            OutputStream os = new FileOutputStream(f);


            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }

            return f.getAbsolutePath();
        } catch (Exception ex) {
            // something went wrong
            ex.printStackTrace();
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String imgPath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    public static Bitmap flip(Bitmap bitmap, String photoPath) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bitmap, 90);

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bitmap, 180);

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bitmap, 270);

                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static String encodeBitmapToUpload(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageData, Base64.NO_WRAP);
        System.gc();
        return encodedImage;
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)  MyApplication.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setupUI(final View view) {
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(view);
                    return false;
                }

            });
        }

    }

    public static long getTimestamp(long timestamp, int hourOfDay, int minute, int second){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTimeInMillis();
    }

    public static Bitmap generateQR(String data){

        QRCodeWriter writer = new QRCodeWriter();
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    public static void getPreviousEslip(final Context context, final String transid, String topupType, final int rootview){
        String mActionEslip = APIServices.ACTIONESLIP;

        if (topupType.equals(FragmentTopup.PIN)){
            mActionEslip = APIServices.ACTION_ESLIP_EPIN;
        }

        APIServices services = APIServices.retrofit.create(APIServices.class);
        Call<ResponseBody> call = services.eslip(new RequestModel(mActionEslip, new EslipRequestModel(transid, null)));

        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(MyApplication.getContext(), call, response.body(), this);

                if (responseValues == null) ((Activity)context).finish();

/*
                if (responseValues == null) {
                    mBottomAction.setEnable(true);
                    return;
                }
*/

                if (responseValues instanceof ResponseModel) {
                    byte[] imageByte = Base64.decode(((ResponseModel) responseValues).getFf()
                            , Base64.NO_WRAP);
                    AppCompatActivity activity = (AppCompatActivity) context;
//                    activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    try {
                        if (activity == null) return;
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(rootview, FragmentTopupSlip.newInstance(imageByte, transid)).commit();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

                }

                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(context, null, call, this, false);
            }

        });
    }

    public static class ParcelableUtil {
        public static byte[] toByteArray(Parcelable parcelable) {
            Parcel parcel=Parcel.obtain();

            parcelable.writeToParcel(parcel, 0);

            byte[] result=parcel.marshall();

            parcel.recycle();

            return(result);
        }

        public static <T> T toParcelable(byte[] bytes,
                                         Parcelable.Creator<T> creator) {
            Parcel parcel=Parcel.obtain();

            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);

            T result=creator.createFromParcel(parcel);

            parcel.recycle();

            return(result);
        }
    }

}
