package com.worldwidewealth.wealthcounter.until;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by MyNet on 17/10/2559.
 */

public class Until {

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
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                String encoded = Base64.encodeToString(buffer.readByteArray(), Base64.DEFAULT);
                byte[] converted = ConvertJsonEncode(encoded).getBytes();
                sink.write(converted);
                Log.e("encoded", encoded);
                Log.e("converted", ConvertJsonEncode(encoded));
                buffer.close();
                sink.close();
            }
        };
    }

    public static String ConvertJsonEncode(String encode){
        String jsonconvert = "";
        int cnt = encode.length()-1;
        while (cnt >= 0)
        {
            jsonconvert += encode.substring(cnt, cnt+1);
            cnt--;
        }
        return jsonconvert;
    }

    public static String decode(String strEncode){
        byte[] decode = Base64.decode(strEncode, Base64.DEFAULT);
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

    public static void setBalanceWallet(TextView balanceInteger, TextView balanceDecimal){

        NumberFormat format = NumberFormat.getCurrencyInstance();

        String[] balance = format.format(Global.getBALANCE()).split("\\.");
        balanceInteger.setText(balance[0]);
        balanceDecimal.setText("."+balance[1]);

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

    public static class ToStringConverterFactory extends Converter.Factory {
        private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");


        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            if (String.class.equals(type)) {
                return new Converter<ResponseBody, String>() {
                    @Override
                    public String convert(ResponseBody value) throws IOException {
                        return value.string();
                    }
                };
            }
            return null;
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                              Annotation[] methodAnnotations, Retrofit retrofit) {

            if (String.class.equals(type)) {
                return new Converter<String, RequestBody>() {
                    @Override
                    public RequestBody convert(String value) throws IOException {
                        return RequestBody.create(MEDIA_TYPE, value);
                    }
                };
            }
            return null;
        }
    }

}
