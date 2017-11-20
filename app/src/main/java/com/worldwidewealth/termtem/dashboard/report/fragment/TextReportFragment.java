package com.worldwidewealth.termtem.dashboard.report.fragment;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.dashboard.report.adapter.ReportAdapter;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.EslipRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SalerptResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.RecyclerItemClickListener;
import com.worldwidewealth.termtem.util.SimpleDividerItemDecoration;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String KEY_DATA = "data";

    private RecyclerView mListReport;
    private ReportAdapter mAdapter;
    private APIServices services = APIServices.retrofit.create(APIServices.class);
    private byte[] mImageByte;
    private String mFileName;
    private Bitmap mImageBitmap;
    private ImageView mImageSlip;
    private RecyclerItemClickListener mItemClickListener;

    private List mListData = null;

    public static final String TAG = TextReportFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters

    public TextReportFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TextReportFragment newInstance() {
        TextReportFragment fragment = new TextReportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_report, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setup();
    }

    private void setup(){
        bindView();
        setupRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        DialogCounterAlert.DialogProgress.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mItemClickListener != null){
            mListReport.removeOnItemTouchListener(mItemClickListener);
        }
    }

    private void bindView(){
        mListReport = (RecyclerView) getView().findViewById(R.id.list_report);

    }

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new ReportAdapter(getContext(), mListData);
        mListReport.setLayoutManager(layoutManager);
        mListReport.setAdapter(new AlphaInAnimationAdapter(mAdapter));
        mItemClickListener = new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String currentType = ((ActivityReport)getActivity()).getmCurrentType();

                if (currentType.equals(ActivityReport.FUNDIN_REPORT)) return;

                new DialogCounterAlert.DialogProgress(getContext()).show();

                if (mImageBitmap != null && !mImageBitmap.isRecycled()){
                    mImageBitmap.recycle();
                    mImageByte = null;
                    mImageSlip.destroyDrawingCache();
                    System.gc();
                }
                serviceEslip(position);
/*
                ((ActivityReport)getActivity()).setEnableTotal(false);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container_report, FragmentTopupSlip.newInstance(FragmentTopupSlip.ESLIP,
                                getTypeTopup(),
                                mAdapter.getItem(position).getTransactionId(),
                                false)).commit();

*/
            }
        });
        mListReport.addOnItemTouchListener(mItemClickListener);
    }

    private String getActionSlip(){
        String currentTypeReport = ((ActivityReport)getActivity()).getmCurrentType();

        switch (currentTypeReport){
            case ActivityReport.TOPUP_REPORT:
                return APIServices.ACTIONESLIP;
            case ActivityReport.EPIN_REPORT:
                return APIServices.ACTION_ESLIP_EPIN;
            case ActivityReport.VAS_REPORT:
                return APIServices.ACTION_ESLIP_VAS;
            case ActivityReport.CASHIN_REPORT:
                return APIServices.ACTION_ESLIP_AGENT_CASHIN;
            case ActivityReport.BILL_REPORT:
                return APIServices.ACTION_ESLIP_BILL;
        }

        return null;

    }

    public void updateDataReport(List listdata){
        mListData = listdata;
        String currentTypeReport = ((ActivityReport)getActivity()).getmCurrentType();
        mAdapter.setCurrentType(currentTypeReport);
        mAdapter.updateAll(mListData);
    }

    private void serviceEslip(final int position){
//        if (!(MyApplication.LeavingOrEntering.currentActivity instanceof ActivityTopup)) return;

        Call<ResponseBody> call = null;
        String currentType = ((ActivityReport)getActivity()).getmCurrentType();
        final SalerptResponseModel model = (SalerptResponseModel) mAdapter.getItem(position);
        switch (currentType){
            case ActivityReport.CASHIN_REPORT:
                call = services.eslip(new RequestModel(getActionSlip(),
                        new EslipRequestModel(model.getTransactionId(),
                                model.getPHONENO())));
                break;
            case ActivityReport.BILL_REPORT:
                call = services.billService(new RequestModel(getActionSlip(),
                        new EslipRequestModel(model.getTransactionId(), null)));
                break;
            default:
                call = services.eslip(new RequestModel(getActionSlip(),
                        new EslipRequestModel(model.getTransactionId(), null)));
                break;
        }
/*
        if (((ActivityReport)getActivity()).getmCurrentType().equals(ActivityReport.CASHIN_REPORT)) {
            call = services.eslip(new RequestModel(getActionSlip(),
                    new EslipRequestModel(mAdapter.getItem(position).getTransactionId(),
                            mAdapter.getItem(position).getPHONENO())));
        } else {
            call = services.eslip(new RequestModel(getActionSlip(),
                    new EslipRequestModel(mAdapter.getItem(position).getTransactionId(), null)));
        }
*/


        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);

                if (responseValues == null) {
                    return;
                }

                if (responseValues instanceof ResponseModel){
                    mImageByte = Base64.decode(((ResponseModel)responseValues).getFf()
                            , Base64.NO_WRAP);

                    initEslip(model.getTransactionId());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), null, call, this, false);
            }
        });
    }

    private void initEslip(String mTransID){
        mFileName = mTransID + ".jpg";

        mImageBitmap = BitmapFactory.decodeByteArray(mImageByte, 0, mImageByte.length);

        mImageSlip = new ImageView(getContext());
        mImageSlip.setAdjustViewBounds(true);


        mImageSlip.setImageBitmap(mImageBitmap);

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(mImageSlip)
                .show();

        mImageSlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        saveImage();
    }

    private void saveImage(){
/*
        NotificationManager nMgr = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
*/
        if (mFileName == null) return;


        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File myDirOld = new File(root + "/WealthCounterSlip");
        File myDir = new File(root + "/TermTemSlip");
        if(myDirOld.exists()){
            Log.e(TAG, "isHasOldDir");
            myDirOld.renameTo(myDir);
        }
        myDir.mkdirs();

        File file = new File(myDir, mFileName);
        if (file.exists())
            file.delete();
        else
            Toast.makeText(getContext(), getString(R.string.save_eslip_success), Toast.LENGTH_LONG).show();

        try {
            FileOutputStream out = new FileOutputStream(file);
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            MediaScannerConnection.scanFile(getContext(), new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);

            DialogCounterAlert.DialogProgress.dismiss();


        } catch (Exception e) {
            e.printStackTrace();
            new DialogCounterAlert(getContext(), getString(R.string.error), getString(R.string.save_eslip_fail), null);
        }


    }



}
