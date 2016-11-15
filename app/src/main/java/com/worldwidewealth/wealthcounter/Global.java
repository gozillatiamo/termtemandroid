package com.worldwidewealth.wealthcounter;

/**
 * Created by MyNet on 11/10/2559.
 */

public class Global {
    private static int page;
    private static String DEVICEID;
    private static String USERID;
    private static String AGENTID;
    private static String TOKEN;
    private static String TXID;
    private static Double BALANCE;

    public static Double getBALANCE() {
        return BALANCE;
    }

    public static void setBALANCE(Double BALANCE) {
        Global.BALANCE = BALANCE;
    }

    public static String getUSERID() {
        return USERID;
    }

    public static void setUSERID(String USERID) {
        Global.USERID = USERID;
    }

    public static String getAGENTID() {
        return AGENTID;
    }

    public static void setAGENTID(String AGENTID) {
        Global.AGENTID = AGENTID;
    }

    public static String getTXID() {
        return TXID;
    }

    public static void setTXID(String TXID) {
        Global.TXID = TXID;
    }

    public static String getDEVICEID() {
        return DEVICEID;
    }

    public static void setDEVICEID(String DEVICEID) {
        Global.DEVICEID = DEVICEID;
    }

    public static String getTOKEN() {
        return TOKEN;
    }

    public static void setTOKEN(String TOKEN) {
        Global.TOKEN = TOKEN;
    }

    public static int getPage() {
        return page;
    }

    public static void setPage(int page) {
        Global.page = page;
    }
}
