package com.worldwidewealth.wealthcounter.until;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
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

import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;

import java.io.IOException;
import java.text.NumberFormat;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;

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

}
