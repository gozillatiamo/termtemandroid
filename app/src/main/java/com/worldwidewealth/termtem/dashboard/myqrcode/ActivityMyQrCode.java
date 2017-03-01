package com.worldwidewealth.termtem.dashboard.myqrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.AgentResponse;
import com.worldwidewealth.termtem.util.Util;

public class ActivityMyQrCode extends MyAppcompatActivity {

    private ViewHoler mHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr_code);
        mHolder = new ViewHoler(this);

        initToolbar();
        initQRCode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initQRCode(){
        AgentResponse agentResponse = new AgentResponse(
                Global.getInstance().getAGENTID(),
                Global.getInstance().getAGENTCODE(),
                Global.getInstance().getFIRSTNAME(),
                Global.getInstance().getLASTNAME(),
                Global.getInstance().getPHONENO(),
                Global.getInstance().getTXID()
        );

        mHolder.mMyQRCode.setImageBitmap(Util.generateQR(new Gson().toJson(agentResponse, AgentResponse.class)));
/*
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode("https://www.google.com", BarcodeFormat.QR_CODE, 1024, 1024);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bmQR = barcodeEncoder.createBitmap(bitMatrix);
            mHolder.mMyQRCode.setImageBitmap(bmQR);
        } catch (WriterException e) {
            e.printStackTrace();
        }
*/

    }



    private class ViewHoler{
        private ImageView mMyQRCode;
        private Toolbar mToolbar;
        public ViewHoler(Activity itemView){
            mMyQRCode = (ImageView) itemView.findViewById(R.id.my_qr);
            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
        }
    }

}
