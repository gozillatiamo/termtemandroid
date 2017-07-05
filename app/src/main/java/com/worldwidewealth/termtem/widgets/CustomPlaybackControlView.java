/*
package com.worldwidewealth.termtem.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.worldwidewealth.termtem.R;

*/
/**
 * Created by user on 08-May-17.
 *//*


public class CustomPlaybackControlView extends PlaybackControlView
    implements View.OnClickListener{

    private Button mBtnSelectorResolution;
    private ImageButton mBtnFullScreen;

    public CustomPlaybackControlView(Context context) {
        super(context);
        setup(null);

    }

    public CustomPlaybackControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);

    }

    public CustomPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs){
        bindView();
    }

    private void bindView(){
        mBtnFullScreen = (ImageButton) findViewById(R.id.btn_fullscreen);
        mBtnSelectorResolution = (Button) findViewById(R.id.btn_change_resolution);

        mBtnSelectorResolution.setOnClickListener(this);
        mBtnFullScreen.setOnClickListener(this);
    }

    private void setupView(){

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_fullscreen:
                Toast.makeText(getContext(), "FullScreen", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_change_resolution:
                Toast.makeText(getContext(), "ChangeResolution", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
*/
