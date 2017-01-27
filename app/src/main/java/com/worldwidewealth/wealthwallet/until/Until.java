package com.worldwidewealth.wealthwallet.until;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.dashboard.report.ActivityReport;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.model.LoginResponseModel;
import com.worldwidewealth.wealthwallet.model.ResponseModel;
import com.worldwidewealth.wealthwallet.services.APIHelper;
import com.worldwidewealth.wealthwallet.services.APIServices;
import com.worldwidewealth.wealthwallet.Global;
import com.worldwidewealth.wealthwallet.MyApplication;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.SplashScreenWWW;
import com.worldwidewealth.wealthwallet.model.DataRequestModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;

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
import java.util.Date;

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

public class Until {

    public static final String KEYPF = "data";
    public static final String KEYTXID = "txid";
    public static final String KEYDEVICEID = "deviceid";
    public static final String LOGOUT = "LOGOUT";
    public static final String TAG = Until.class.getSimpleName();

    public static void initSpinnerCurrency(Context context, Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

    }

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
                    System.gc();
                    sink.write(converted);
                    buffer.close();
                    sink.close();
                }catch (OutOfMemoryError e){
                    /*AlertDialog alertDialog = new AlertDialog.Builder(MyApplication.getContext())
                            .*/
                }
            }
        };
    }

/*
    public static String ConvertJsonEncode(String encode){
*/
/*
        int cnt = encode.length()-1;
        while (cnt >= 0)
        {
            jsonconvert += encode.substring(cnt, cnt+1);
            cnt--;
        }
*//*

        return jsonconvert;
    }
*/

    public static String decode(String strEncode){
        String jsonconvert = new StringBuilder(strEncode).reverse().toString();
        byte[] decode = Base64.decode(jsonconvert, Base64.DEFAULT);
        return new String(decode);
    }
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setWindow(Activity activity){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = activity.getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static void setBalanceWallet(View myWallet){
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(KEYPF, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(LOGOUT, true)) return;

        TextView balanceDecimal = (TextView) myWallet.findViewById(R.id.txt_balance_decimal);
        TextView balanceInteger = (TextView) myWallet.findViewById(R.id.txt_balance_integer);
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        String[] balance = format.format(Global.getBALANCE()).split("\\.");
        balanceInteger.setText(balance[0]);
        balanceDecimal.setText("."+balance[1]);

    }

    public static void updateMyBalanceWallet(final Context context, final View includeMywallet){
        APIServices services = APIServices.retrofit.create(APIServices.class);
        boolean logout =  context.getSharedPreferences(KEYPF, Context.MODE_PRIVATE).getBoolean("LOGOUT", true);
        if (logout) return;
        Call<ResponseBody> call = services.getbalance(new RequestModel(APIServices.ACTIONGETBALANCE, new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(context, call, response.body(), this);

                if (values instanceof String){
                    LoginResponseModel loginResponseModel = new Gson().fromJson((String)values, LoginResponseModel.class);
                    Global.setBALANCE(loginResponseModel.getBALANCE());
                    setBalanceWallet(includeMywallet);
                }

                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                new ErrorNetworkThrowable(t).networkError(context, call, this);
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
                        updateMyBalanceWallet(context, includeMywallet);
                        v.clearAnimation();
                    }
                }, 2000);
            }
        });

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

    public static void setLogoutSharedPreferences(Context context, boolean remove){

        SharedPreferences sharedPref = context.getSharedPreferences(KEYPF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!remove) {
            editor.putBoolean("LOGOUT", false);
            editor.putString("AGENTID", Global.getAGENTID());
            editor.putString("DEVICEID", Global.getDEVICEID());
            editor.putString("TXID", Global.getTXID());
            editor.putString("USERID", Global.getUSERID());
        } else {
            editor.putBoolean("LOGOUT", true);
            editor.putString("AGENTID", null);
            editor.putString("DEVICEID", null);
            editor.putString("TXID", null);
            editor.putString("USERID", null);
        }

        editor.commit();

    }

    public static void setTXIDSharedPreferences(String txid, String deviceid){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEYTXID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEYTXID, txid);
        editor.putString(KEYDEVICEID, deviceid);
        editor.commit();
    }

    public static String getTXIDSharedPreferences(){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEYTXID, Context.MODE_PRIVATE);
        return preferences.getString(KEYTXID, null);
    }

    public static String getDEVICEIDSharedPreferences(){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEYTXID, Context.MODE_PRIVATE);
        return preferences.getString(KEYDEVICEID, null);
    }


    public static void logoutAPI(){
        if (Global.getTXID() == null || Global.getTXID().equals("")) return;
        APIServices services = APIServices.retrofit.create(APIServices.class);
        Call<ResponseBody> call = services.logout(new RequestModel(APIServices.ACTIONLOGOUT,
                new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(null, call, response.body(), this);
                if (values == null) return;
                Global.setAGENTID("");
                Global.setUSERID("");
                Global.setBALANCE(0.00);
                Global.setTXID("");
                Global.setDEVICEID("");
                Until.setLogoutSharedPreferences(MyApplication.getContext(), true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public static void backToSignIn(Activity activity){
        Intent intent = new Intent(activity.getApplicationContext(), SplashScreenWWW.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(MyApplication.getContext().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
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
/*
        Cursor cursor = MyApplication.getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
*/
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

            //Utils.InputToOutputStream(is, os);

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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    public static Bitmap decodeSampledBitmapFromResource(String imgPath,
                                                         int reqWidth, int reqHeight) {

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

    public static Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = MyApplication.getContext().getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap bitmap = null;
            in = MyApplication.getContext().getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                bitmap = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x,
                        (int) y, true);
                bitmap.recycle();
                bitmap = scaledBitmap;

                System.gc();
            } else {
                bitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG, "bitmap size - width: " + bitmap.getWidth() + ", height: " +
                    bitmap.getHeight());
            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}
