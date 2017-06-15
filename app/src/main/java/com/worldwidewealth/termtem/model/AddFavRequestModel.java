package com.worldwidewealth.termtem.model;

/**
 * Created by gozillatiamo on 6/15/17.
 */

public class AddFavRequestModel extends DataRequestModel {
    private String TRANID;
	private String FAVNAME;
    private String SERVICE;


    public AddFavRequestModel(String tranid, String favname, String service){
        this.TRANID = tranid;
        this.FAVNAME = favname;
        this.SERVICE = service;
    }

}
