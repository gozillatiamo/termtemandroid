package com.worldwidewealth.wealthcounter.until;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.worldwidewealth.wealthcounter.R;

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
}
