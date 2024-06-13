package com.example.ifix;


public class DataClass {

    private String dataName;
    private String dataPhone;
    private String dataBrand;
    private String dataModel;
    private String dataColour;
    private String dataPassword;
    private String dataComplaint;
    private String dataStatus;
    private String dataImage;
    private String dataExpense;
    private String dataAmount;
    private String dataPaymentVia;
    private String dataTime;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataName() {
        return dataName;
    }

    public String getDataPhone() {
        return dataPhone;
    }

    public String getDataBrand() {
        return dataBrand;
    }
    public String getDataModel() {return dataModel;}
    public String getDataColour() {
        return dataColour;
    }
    public String getDataPassword() {
        return dataPassword;
    }
    public String getDataComplaint() {
        return dataComplaint;
    }
    public String getDataStatus(){return dataStatus;}
    public String getDataExpense(){return dataExpense;}

    public String getDataAmount(){return dataAmount;}

    public String getDataPaymentVia(){return dataPaymentVia;}
    public String getDataTime(){return dataTime;}


    public String getDataImage() {
        return dataImage;
    }
    public DataClass(String dataName, String dataPhone, String dataBrand, String dataModel, String dataColour, String dataPassword, String dataComplaint, String dataStatus, String dataImage) {
        this.dataName = dataName;
        this.dataPhone = dataPhone;
        this.dataBrand = dataBrand;
        this.dataModel = dataModel;
        this.dataColour = dataColour;
        this.dataPassword = dataPassword;
        this.dataComplaint = dataComplaint;
        this.dataImage = dataImage;
        this.dataStatus = dataStatus;
    }

    public DataClass(String dataName, String dataPhone, String dataBrand, String dataModel, String dataColour, String dataPassword, String dataComplaint, String dataStatus, String dataImage,String dataTime) {
        this.dataName = dataName;
        this.dataPhone = dataPhone;
        this.dataBrand = dataBrand;
        this.dataModel = dataModel;
        this.dataColour = dataColour;
        this.dataPassword = dataPassword;
        this.dataComplaint = dataComplaint;
        this.dataImage = dataImage;
        this.dataStatus = dataStatus;
        this.dataTime=dataTime;
    }
    public DataClass(String dataName, String dataPhone, String dataBrand, String dataModel, String dataColour, String dataPassword, String dataComplaint, String dataStatus, String dataImage,String dataExpense,String dataAmount,String dataPaymentvia) {
        this.dataName = dataName;
        this.dataPhone = dataPhone;
        this.dataBrand = dataBrand;
        this.dataModel = dataModel;
        this.dataColour = dataColour;
        this.dataPassword = dataPassword;
        this.dataComplaint = dataComplaint;
        this.dataImage = dataImage;
        this.dataStatus = dataStatus;
        this.dataExpense = dataExpense;
        this.dataAmount= dataAmount;
        this.dataPaymentVia =dataPaymentvia;
    }
    public DataClass(){

    }
}