package com.worldwidewealth.termtem.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 10-Apr-17.
 */

public class BankInformationModel {
    private static Context mContext = MyApplication.getContext();
    private static final ArrayList<Bank> mListBank = new ArrayList<>(Arrays.asList(
            new Bank(
                    mContext.getString(R.string.kbank),
                    R.drawable.kbank,
                    mContext.getString(R.string.kbank_saving_account_number),
                    null,
                    mContext.getString(R.string.bank_account_name),
                    mContext.getString(R.string.kbank_branch)
            ),
            new Bank(
                    mContext.getString(R.string.bbl),
                    R.drawable.bbl,
                    mContext.getString(R.string.bbl_saving_account_number),
                    mContext.getString(R.string.bbl_promptpay_account_number),
                    mContext.getString(R.string.bank_account_name),
                    mContext.getString(R.string.bbl_branch)
            )
    ));

    public ArrayList<Bank> getmListBank() {
        return mListBank;
    }

    public static class Bank{
        private String name;
        private int logo;
        private String saving_account;
        private String promptpay_account;
        private String account_name;
        private String branch_name;

        public Bank(String name, int logo, String saving_account, String promptpay_account,
                    String account_name, String branch_name){
            this.name = name;
            this.logo = logo;
            this.saving_account = saving_account;
            this.promptpay_account = promptpay_account;
            this.account_name = account_name;
            this.branch_name = branch_name;
        }

        public int getLogo() {
            return logo;
        }

        public String getName() {
            return name;
        }

        public String getSaving_account() {
            return saving_account;
        }

        public String getPromptpay_account() {
            return promptpay_account;
        }

        public String getAccount_name() {
            return account_name;
        }

        public String getBranch_name() {
            return branch_name;
        }
    }

}
