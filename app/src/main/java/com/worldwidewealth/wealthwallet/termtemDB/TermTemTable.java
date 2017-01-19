package com.worldwidewealth.wealthwallet.termtemDB;

/**
 * Created by user on 18-Jan-17.
 */

public class TermTemTable {

    public static final class UploadCashIn{
        public static final String TABLE_NAME = "table_upload_cash_in";

        public static final class Column{
            public static final String REQUEST = "request";
            public static final String RESPONSE = "response";
            public static final String RETRY = "retry";
        }
    }


}
