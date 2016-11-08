package com.worldwidewealth.wealthcounter;

/**
 * Created by MyNet on 11/10/2559.
 */

public class Global {
    private static int page;
    private static String DEVICEID;
    private static String TOKEN;
    private static String TXID;

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
