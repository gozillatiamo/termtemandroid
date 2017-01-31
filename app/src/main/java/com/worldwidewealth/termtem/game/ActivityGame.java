package com.worldwidewealth.termtem.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class ActivityGame extends AppCompatActivity {
    private WebView mWebGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebGame = new WebView(this);
        setContentView(mWebGame);
        initWebGame();
//        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityGame.this);
//                dialog.setMessage("ยินดีด้วยคุณได้รับรางวัล!!!");
//                dialog.setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ActivityGame.this.finish();
//                    }
//                });
//                dialog.show();
//            }
//        });
    }

    private void initWebGame(){
        mWebGame.setInitialScale(1);
        mWebGame.getSettings().setJavaScriptEnabled(true);
        mWebGame.getSettings().setLoadWithOverviewMode(true);
        mWebGame.getSettings().setUseWideViewPort(true);
        mWebGame.getSettings().setSupportZoom(false);
        mWebGame.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mWebGame.setLayoutParams(params);

        mWebGame.setWebChromeClient(new WebChromeClient());
        mWebGame.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return  true;

            }
        });

        mWebGame.loadUrl("http://www.wwwholding.co/www/lucky.html");
    }
}
