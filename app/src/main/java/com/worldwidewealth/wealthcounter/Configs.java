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

}
