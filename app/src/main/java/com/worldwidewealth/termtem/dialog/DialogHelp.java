package com.worldwidewealth.termtem.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.BankInformationModel;

import java.util.regex.Pattern;

/**
 * Created by user on 12-Jan-17.
 */

public class DialogHelp extends Dialog {

    private ExpandableListView mExpandableList;

    public DialogHelp(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_help);
        initWidgets();
        initData();
    }

    private void initWidgets(){
        mExpandableList = (ExpandableListView) findViewById(R.id.expanded_menu);
    }

    private void initData(){
        mExpandableList.setAdapter(new HelpExpandableAdapter());
    }

    private class HelpExpandableAdapter extends BaseExpandableListAdapter{
        private String[] mMainMenu = getContext().getResources().getStringArray(R.array.menu_help);
        private BankInformationModel bankInformationModel = new BankInformationModel();
        @Override
        public int getGroupCount() {
            return mMainMenu.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition){
                case 0:
                    return bankInformationModel.getmListBank().size();
                case 1:
                    return 1;
            }

            return -1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mMainMenu[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {

            switch (groupPosition){
                case 0:
                    return bankInformationModel.getmListBank().get(childPosition);
            }

            return childPosition;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            LayoutInflater infalInflater = null;
            if (convertView == null){
                infalInflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);

            }
            TextView title = (TextView) convertView.findViewById(android.R.id.text1);
            title.setText((String)getGroup(groupPosition));
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.my_text_size));


            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView title;
            switch (groupPosition) {
                case 0:
                    final BankInformationModel.Bank bank = bankInformationModel.getmListBank().get(childPosition);
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
                    TypedValue typedValue = new TypedValue();

                    getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);

                    // it's probably a good idea to check if the color wasn't specified as a resource
                    if (typedValue.resourceId != 0) {
                        convertView.setBackgroundResource(typedValue.resourceId);
                    } else {
                        // this should work whether there was a resource id or not
                        convertView.setBackgroundColor(typedValue.data);
                    }
                    title = (TextView) convertView.findViewById(android.R.id.text1);
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.small_text_size));

                    title.setText(bank.getName());
                    Drawable drawable = ContextCompat.getDrawable(getContext(), bank.getLogo());
                    int logoSize = getContext().getResources().getDimensionPixelSize(R.dimen.icon_size);
                    if (childPosition == 0) {
                        int widesize = getContext().getResources().getDimensionPixelSize(R.dimen.icon_big_size);
                        drawable.setBounds(0, 0, widesize, logoSize);
                    }else
                        drawable.setBounds(0, 0, logoSize, logoSize);
                    title.setCompoundDrawables(drawable, null, null, null);
                    title.setCompoundDrawablePadding(getContext().getResources().getDimensionPixelSize(R.dimen.activity_small_space));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showBankInformation(bank);
                        }
                    });
                    break;
                case 1:
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
                    title = (TextView) convertView.findViewById(android.R.id.text1);
                    title.setText(getContext().getString(R.string.contact_phone_no));
                    title.setTypeface(Typeface.DEFAULT_BOLD);
                    title.setTextColor(getContext().getResources().getColor(android.R.color.secondary_text_dark));
                    title.setLinkTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.small_text_size));
                    Pattern pattern = Pattern.compile("(0\\d+)-\\d{3}-\\d{4}");

                    Linkify.addLinks(title, pattern, "tel:");

                    break;
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private void showBankInformation(BankInformationModel.Bank bank){
            AlertDialog alertDialog;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.item_bank_detail, null);
            dialogBuilder.setView(dialogView);
//            alertDialog.setContentView(R.layout.item_bank_detail);

            TextView mBankName, mAccountSaving, mAccountPromptPay, mAccountName, mBankBranch, mTitleBank;
            ImageView mBankLogo;

            mBankName = (TextView) dialogView.findViewById(R.id.bank_name);
            mTitleBank = (TextView) dialogView.findViewById(R.id.title_bank);
            mAccountSaving = (TextView) dialogView.findViewById(R.id.saving_account_id);
            mAccountPromptPay = (TextView) dialogView.findViewById(R.id.promptpay_account_id);
            mAccountName = (TextView) dialogView.findViewById(R.id.account_name);
            mBankBranch = (TextView) dialogView.findViewById(R.id.bank_branch);
            mBankLogo = (ImageView) dialogView.findViewById(R.id.bank_logo);

            mBankName.setText(bank.getName());
            if (bank.getSaving_account() != null){
                mAccountSaving.setText(bank.getSaving_account());
                dialogView.findViewById(R.id.layout_bank_saving_id).setVisibility(View.VISIBLE);
            } else {
                dialogView.findViewById(R.id.layout_bank_saving_id).setVisibility(View.GONE);
            }
            if (bank.getPromptpay_account() != null){
                mTitleBank.setText(R.string.promptpay_title);
                mAccountPromptPay.setText(bank.getPromptpay_account());
                dialogView.findViewById(R.id.layout_promptpay_id).setVisibility(View.VISIBLE);
            } else {
                dialogView.findViewById(R.id.layout_promptpay_id).setVisibility(View.GONE);
            }

            mAccountName.setText(bank.getAccount_name());
            if (bank.getBranch_name() != null){
                mBankBranch.setText(bank.getBranch_name());
                dialogView.findViewById(R.id.layout_bank_branch).setVisibility(View.VISIBLE);
            } else {
                dialogView.findViewById(R.id.layout_bank_branch).setVisibility(View.GONE);
            }
            mBankLogo.setImageResource(bank.getLogo());

            alertDialog = dialogBuilder.create();
            alertDialog.setOnShowListener(new MyShowListener());
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.show();
        }

    }
}
