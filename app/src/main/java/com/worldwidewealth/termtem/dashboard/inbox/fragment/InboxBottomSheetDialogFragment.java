package com.worldwidewealth.termtem.dashboard.inbox.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.InboxResponse;
import com.worldwidewealth.termtem.model.ActionItemRequest;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.widgets.InformationView;
import com.worldwidewealth.termtem.widgets.WidgetTypeInbox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 30-Mar-17.
 */

public class InboxBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final String KEY_INBOX_RESPONSE = "inboxresponse";

    private InboxResponse mDataInbox;
    private APIServices services = APIServices.retrofit.create(APIServices.class);

    private TextView mTitle, mDes;
    private ImageView mBtnDel;
    private WidgetTypeInbox mWidgetType;

    public static final int BUFFER_SEGMENT_SIZE = 16 * 1024; // Original value was 64 * 1024
    public static final int VIDEO_BUFFER_SEGMENTS = 50; // Original value was 200
    public static final int AUDIO_BUFFER_SEGMENTS = 20; // Original value was 54
    public static final int BUFFER_SEGMENT_COUNT = 64; // Original value was 256

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallbak = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN){
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

     public static InboxBottomSheetDialogFragment newInstance(InboxResponse response, int position, int page) {
         Bundle args = new Bundle();

         InboxBottomSheetDialogFragment fragment = new InboxBottomSheetDialogFragment();
         args.putParcelable(KEY_INBOX_RESPONSE, response);
         args.putInt(InboxFragment.POSITION, position);
         fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDataInbox = getArguments().getParcelable(KEY_INBOX_RESPONSE);
        getArguments().remove(KEY_INBOX_RESPONSE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_inbox_bottom_sheet, null);
        bindView(contentView);
        dialog.setContentView(contentView);

        callRead();
        bindDialogBottomSheet(contentView);
        setupView();
    }

    private void bindView(View contentView){
        mTitle = (TextView) contentView.findViewById(R.id.inbox_title);
        mDes = (TextView) contentView.findViewById(R.id.inbox_des);
        mBtnDel = (ImageView) contentView.findViewById(R.id.btn_del);
        mWidgetType = (WidgetTypeInbox) contentView.findViewById(R.id.widget_type);
    }

    private void bindDialogBottomSheet(View contentView){

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View)contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View)contentView.getParent()).setFitsSystemWindows(true);

        if (behavior != null && behavior instanceof BottomSheetBehavior){
            ((BottomSheetBehavior)behavior).setBottomSheetCallback(mBottomSheetBehaviorCallbak);
        }

        View parent = (View) contentView.getParent();
//        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        contentView.measure(0, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(screenHeight);

        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior)params.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallbak);
        }

        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        parent.setLayoutParams(params);

    }

    private void setupView(){
        mTitle.setText(mDataInbox.getTitle());
        mDes.setText(mDataInbox.getMsg());

        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return match.group();
            }
        };

        Pattern pattern = Pattern.compile("(\\d{9,13})");
        Linkify.addLinks(mDes, pattern, "tel:", null, filter);

        Pattern urlPattern = Patterns.WEB_URL;
        Linkify.addLinks(mDes, urlPattern, "http://", null, filter);

        Pattern urlsPattern = Patterns.WEB_URL;
        Linkify.addLinks(mDes, urlsPattern, "https://", null, filter);


        mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog builderInner = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogError)
                        .setMessage(getContext().getString(R.string.confirm_delete_msg))
                        .setTitle(getContext().getString(R.string.confirm_delete_title))
                        .setCancelable(false)
                        .setPositiveButton(getContext().getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                InboxBottomSheetDialogFragment.this.dismiss();
                                Intent data = new Intent();
                                data.putExtras(InboxBottomSheetDialogFragment.this.getArguments());
                                getTargetFragment().onActivityResult(InboxFragment.DELETE_INBOX,
                                        Activity.RESULT_OK,
                                        data);
                            }
                        })
                        .setNegativeButton(getContext().getString(R.string.cancel),null)
                        .create();

                builderInner.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE)
                                .setTextColor(getContext().getResources()
                                        .getColor(android.R.color.holo_red_dark));
                    }
                });
                builderInner.show();

            }
        });

        setupType(mDataInbox.get_type());


//        handleVideo();
//        handleExoplayer();
    }

    private void callRead(){
        if (!mDataInbox.isReaded()) {
            MyApplication.getBus().post(mDataInbox.getMsgid());
            Call<ResponseBody> call = services.service(
                    new RequestModel(APIServices.ACTIONREADMSG,
                            ActionItemRequest.MSG(mDataInbox.getMsgid())));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Intent data = new Intent();
                    data.putExtras(InboxBottomSheetDialogFragment.this.getArguments());
                    getTargetFragment().onActivityResult(InboxFragment.READ_INBOX,
                            Activity.RESULT_OK,
                            data);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }

    }

    public void setupType(int type){

        switch (InformationView.TYPE.getTypeAt(type)){
            case TEXT:
//                mWidgetType.setWidgetType(WidgetTypeInbox.WIDGET_TYPE.VIDEO.getWidgetType());
                break;

            case IMAGE:
                mWidgetType.setWidgetType(WidgetTypeInbox.WIDGET_TYPE.IMAGE.getWidgetType());
                mWidgetType.setImage(mDataInbox.getAttachlist());

                break;

        }
    }

    private void handleExoplayer(){
/*
        Uri uri = Uri.parse("https://tungsten.aaplimg.com/VOD/bipbop_adv_fmp4_example/master.m3u8");
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        Handler mainHandler = new Handler();

        mExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(true);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), getString(R.string.app_name)), bandwidthMeter);
*/
/*
        MediaSource mediaSource = new HlsMediaSource(Uri.parse("https://tungsten.aaplimg.com/VOD/bipbop_adv_example_v2/master.m3u8"),
                dataSourceFactory, mainHandler, eventListener);
*/
//        player.prepare(mediaSource);



/*

        DefaultBandwidthMeter defaultBandwidthMeter = new DefauNiltBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), getString(R.string.app_name)), defaultBandwidthMeter);

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse("http://videos.electroteque.org/bitrate/big_buck_bunny_600k.mp4"),
                dataSourceFactory, extractorsFactory, null, null);
        player.prepare(videoSource);
//        player.release();

        player.setPlayWhenReady(true);
        mExoPlayerView.setPlayer(player);
*/
    }

    private void handleVideo(){
/*
        videoOptions.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
        videoOptions.inputType = VrVideoView.Options.TYPE_MONO;


        if (backgroundVideoLoaderTask != null){
            backgroundVideoLoaderTask.cancel(true);
        }

        backgroundVideoLoaderTask = new VideoLoaderTask();
        backgroundVideoLoaderTask.execute(Pair.create(Uri.parse("https://flowplayer.electroteque.org/video/360/ultra_light_flight_720p.mp4"), videoOptions));
*/
    }

/*
    private class ActivityEventListener extends VrVideoEventListener {
        public String TAG = ActivityEventListener.class.getSimpleName();
        @Override
        public void onClick() {
            super.onClick();
        }

        @Override
        public void onCompletion() {
            super.onCompletion();
//            mVideoView.seekTo(0);
        }

        @Override
        public void onNewFrame() {
            super.onNewFrame();
        }

        @Override
        public void onLoadSuccess() {
            super.onLoadSuccess();
        }

        @Override
        public void onLoadError(String errorMessage) {
            super.onLoadError(errorMessage);
            Log.e(TAG, "Error loading video: "+errorMessage);
        }
    }
*/

/*
    class VideoLoaderTask extends AsyncTask<Pair<Uri, VrVideoView.Options>, Pair, Pair<Uri, VrVideoView.Options>>{
        @Override
        protected Pair<Uri, VrVideoView.Options> doInBackground(Pair<Uri, VrVideoView.Options>... pairs) {
            return new Pair<>(pairs[0].first, pairs[0].second);
        }

        @Override
        protected void onPostExecute(Pair<Uri, VrVideoView.Options> uriOptionsPair) {
            super.onPostExecute(uriOptionsPair);
            try {
                mVideoView.loadVideo(uriOptionsPair.first, uriOptionsPair.second);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
*/
}
