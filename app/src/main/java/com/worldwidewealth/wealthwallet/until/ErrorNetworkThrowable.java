package com.worldwidewealth.wealthwallet.until;

import android.content.Context;
import android.os.Environment;

import com.worldwidewealth.wealthwallet.MyApplication;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.dialog.DialogNetworkError;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by MyNet on 17/11/2559.
 */

public class ErrorNetworkThrowable extends Throwable {

    public ErrorNetworkThrowable(Throwable t){
        super(t);
    }

    public void networkError(Context context, String msg, Call call, Callback callback){
        //printStackTrace();
        if (DialogCounterAlert.DialogProgress.isShow()) {
            DialogCounterAlert.DialogProgress.dismiss();
        }
        new DialogNetworkError(context, msg, call, callback);

    }

    public void networkError(Context context, Call call, Callback callback){
        networkError(context, "", call, callback);
    }

    @Override
    public void printStackTrace() {
        writeToFile();
        super.printStackTrace();
    }

    private void writeToFile() {
        try{
            final File path =
                    Environment.getExternalStoragePublicDirectory(MyApplication.getContext().getFilesDir().getAbsolutePath());

            // Make sure the path directory exists.
            if(!path.exists())
            {
                // Make it, if it doesn't exit
                path.mkdirs();
            }
            final File file = new File(path, "WealthCounterError.txt");


            String error = DateFormat.getDateTimeInstance().format(new Date()) + "\n";
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            printStackTrace(printWriter);
            error += writer.toString();

            OutputStreamWriter out = new OutputStreamWriter(MyApplication.getContext().openFileOutput("WealthCounterError.txt", Context.MODE_APPEND));
            out.append("\n\n" + error);
            out.close();
        }
        catch (IOException e) {
        }
    }
}
