package com.worldwidewealth.wealthcounter;

/**
 * Created by MyNet on 17/10/2559.
 */

public class Configs {

    private static String PLATFORM = "android";

    public static String getPLATFORM() {
        return PLATFORM;
    }

    public static final class Slip{
        public static final String MT_RECEIVE = "mt_receive";
        public static final String MT_SEND = "mt_send";
    }

    public static final class TopupServices{
        public static final String AIS = "ais";
        public static final String TRUEMOVE = "truemove";
        public static final String DTAC = "dtac";
    }
}
