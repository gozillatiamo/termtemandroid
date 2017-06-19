package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by gozillatiamo on 6/15/17.
 */

public class LoadFavResponseModel implements Parcelable{

    private String favid;
    private String no;
    private String name;
    private String service;
    private Date Create_Date;
    private List<TopupListModel> topuplist;
    private List<CashInListModel> cashinlist;
    private List<EpinListModel> epinlist;
    private List<VasListModel> vaslist;
    private List<BillListModel> billlist;

    protected LoadFavResponseModel(Parcel in) {
        favid = in.readString();
        no = in.readString();
        name = in.readString();
        service = in.readString();
        topuplist = in.createTypedArrayList(TopupListModel.CREATOR);
        cashinlist = in.createTypedArrayList(CashInListModel.CREATOR);
        epinlist = in.createTypedArrayList(EpinListModel.CREATOR);
        vaslist = in.createTypedArrayList(VasListModel.CREATOR);
        billlist = in.createTypedArrayList(BillListModel.CREATOR);
        Create_Date.setTime(in.readLong());
    }

    public static final Creator<LoadFavResponseModel> CREATOR = new Creator<LoadFavResponseModel>() {
        @Override
        public LoadFavResponseModel createFromParcel(Parcel in) {
            return new LoadFavResponseModel(in);
        }

        @Override
        public LoadFavResponseModel[] newArray(int size) {
            return new LoadFavResponseModel[size];
        }
    };

    public String getFavid() {
        return favid;
    }

    public String getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public String getService() {
        return service;
    }

    public Date getCreate_Date() {
        return Create_Date;
    }

    public List<TopupListModel> getTopuplist() {
        return topuplist;
    }

    public List<CashInListModel> getCashinlist() {
        return cashinlist;
    }

    public List<EpinListModel> getEpinlist() {
        return epinlist;
    }

    public List<VasListModel> getVaslist() {
        return vaslist;
    }

    public List<BillListModel> getBilllist() {
        return billlist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(favid);
        dest.writeString(no);
        dest.writeString(name);
        dest.writeString(service);
        dest.writeTypedList(topuplist);
        dest.writeLong(Create_Date.getTime());
    }

    public static class TopupListModel implements Parcelable{
        private String PhoneNo;
        private String CarrierCode;
        private Double Amt;

        protected TopupListModel(Parcel in) {
            PhoneNo = in.readString();
            CarrierCode = in.readString();
            Amt = in.readDouble();
        }

        public static final Creator<TopupListModel> CREATOR = new Creator<TopupListModel>() {
            @Override
            public TopupListModel createFromParcel(Parcel in) {
                return new TopupListModel(in);
            }

            @Override
            public TopupListModel[] newArray(int size) {
                return new TopupListModel[size];
            }
        };

        public String getPhoneNo() {
            return PhoneNo;
        }

        public String getCarrierCode() {
            return CarrierCode;
        }

        public Double getAmt() {
            return Amt;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(PhoneNo);
            dest.writeString(CarrierCode);
            dest.writeDouble(Amt);
        }
    }

    public static class CashInListModel implements Parcelable{
        private String AgentId;
        private String AgentCode;
        private String AgentFirstName;
        private String AgentLastName;
        private Double Amt;

        protected CashInListModel(Parcel in) {
            AgentId = in.readString();
            AgentCode = in.readString();
            AgentFirstName = in.readString();
            AgentLastName = in.readString();
            Amt = in.readDouble();
        }

        public static final Creator<CashInListModel> CREATOR = new Creator<CashInListModel>() {
            @Override
            public CashInListModel createFromParcel(Parcel in) {
                return new CashInListModel(in);
            }

            @Override
            public CashInListModel[] newArray(int size) {
                return new CashInListModel[size];
            }
        };

        public String getAgentId() {
            return AgentId;
        }

        public String getAgentCode() {
            return AgentCode;
        }

        public String getAgentFirstName() {
            return AgentFirstName;
        }

        public String getAgentLastName() {
            return AgentLastName;
        }

        public Double getAmt() {
            return Amt;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(AgentId);
            dest.writeString(AgentCode);
            dest.writeString(AgentFirstName);
            dest.writeString(AgentLastName);
            dest.writeDouble(Amt);
        }
    }

    public static class EpinListModel implements Parcelable{
        private String PhoneNo;
        private String CarrierCode;
        private Double Amt;

        protected EpinListModel(Parcel in) {
            PhoneNo = in.readString();
            CarrierCode = in.readString();
            Amt = in.readDouble();

        }

        public static final Creator<EpinListModel> CREATOR = new Creator<EpinListModel>() {
            @Override
            public EpinListModel createFromParcel(Parcel in) {
                return new EpinListModel(in);
            }

            @Override
            public EpinListModel[] newArray(int size) {
                return new EpinListModel[size];
            }
        };

        public String getPhoneNo() {
            return PhoneNo;
        }

        public String getCarrierCode() {
            return CarrierCode;
        }

        public Double getAmt() {
            return Amt;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(PhoneNo);
            dest.writeString(CarrierCode);
            dest.writeDouble(Amt);

        }
    }

    public static class VasListModel implements Parcelable{
        private String PhoneNo;
        private String CarrierCode;
        private String Desc;
        private Double Amt;

        protected VasListModel(Parcel in) {
            PhoneNo = in.readString();
            CarrierCode = in.readString();
            Desc = in.readString();
            Amt = in.readDouble();
        }

        public static final Creator<VasListModel> CREATOR = new Creator<VasListModel>() {
            @Override
            public VasListModel createFromParcel(Parcel in) {
                return new VasListModel(in);
            }

            @Override
            public VasListModel[] newArray(int size) {
                return new VasListModel[size];
            }
        };

        public String getPhoneNo() {
            return PhoneNo;
        }

        public String getCarrierCode() {
            return CarrierCode;
        }

        public String getDesc() {
            return Desc;
        }

        public Double getAmt() {
            return Amt;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(PhoneNo);
            dest.writeString(CarrierCode);
            dest.writeString(Desc);
            dest.writeDouble(Amt);
        }
    }

    public static class BillListModel implements Parcelable{
        private String PhoneNo;
        private String CarrierCode;
        private String CarrierServiceCode;
        private Double Amt;
        private String Ref1;
        private String Ref2;

        protected BillListModel(Parcel in) {
            PhoneNo = in.readString();
            CarrierCode = in.readString();
            CarrierServiceCode = in.readString();
            Ref1 = in.readString();
            Ref2 = in.readString();
            Amt = in.readDouble();
        }

        public static final Creator<BillListModel> CREATOR = new Creator<BillListModel>() {
            @Override
            public BillListModel createFromParcel(Parcel in) {
                return new BillListModel(in);
            }

            @Override
            public BillListModel[] newArray(int size) {
                return new BillListModel[size];
            }
        };

        public String getPhoneNo() {
            return PhoneNo;
        }

        public String getCarrierCode() {
            return CarrierCode;
        }

        public String getCarrierServiceCode() {
            return CarrierServiceCode;
        }

        public Double getAmt() {
            return Amt;
        }

        public String getRef1() {
            return Ref1;
        }

        public String getRef2() {
            return Ref2;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(PhoneNo);
            dest.writeString(CarrierCode);
            dest.writeString(CarrierServiceCode);
            dest.writeString(Ref1);
            dest.writeString(Ref2);
            dest.writeDouble(Amt);
        }
    }

}
