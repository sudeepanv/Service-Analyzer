package com.example.ifix;

import java.util.List;

public class DataClass {

    private String dataName;
    private String dataPhone;
    private String dataBrand;
    private String dataModel;
    private String dataColour;
    private String dataPassword;
    private String dataComplaint;
    private String dataStatus;
    private List<String> dataImage;
    private String dataExpense;
    private String dataAmount;
    private String dataPaymentVia;
    private String dataTime;
    private String dataBoughtfrom;
    private String dataDeliveryTime;
    private String key;
    private String dataEstimate;

    private String dataJobNo;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataName() {
        return dataName;
    }
    public String getDataBoughtfrom() {
        return dataBoughtfrom;
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
    public String getDataEstimate(){return dataEstimate;}
    public String getDataAmount(){return dataAmount;}

    public String getDataPaymentVia(){return dataPaymentVia;}
    public String getDataTime(){return dataTime;}
    public String getDataDeliveryTime(){return dataDeliveryTime;}
    public String setDataJobNo(){
        dataJobNo= Integer.toString(Integer.parseInt(dataJobNo)+1);
        return dataJobNo;
    }
    public String getDataJobNo(){
        return dataJobNo;
    }


//update
    public List<String> getDataImage() {
        return dataImage;
    }
    public DataClass(String dataName, String dataPhone, String dataBrand, String dataModel, String dataColour, String dataPassword, String dataComplaint, String dataStatus, List<String> dataImage,String dataTime,String maxjob) {
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
        this.dataJobNo=maxjob;
    }

//new entry
//    public DataClass(String dataName, String dataPhone, String dataBrand, String dataModel, String dataColour, String dataPassword, String dataComplaint, String dataStatus, List<String> dataImage,String dataTime,String maxjob,String estimate) {
//        this.dataName = dataName;
//        this.dataPhone = dataPhone;
//        this.dataBrand = dataBrand;
//        this.dataModel = dataModel;
//        this.dataColour = dataColour;
//        this.dataPassword = dataPassword;
//        this.dataComplaint = dataComplaint;
//        this.dataImage = dataImage;
//        this.dataStatus = dataStatus;
//        this.dataTime=dataTime;
//        this.dataJobNo=maxjob;
//        this.dataEstimate=estimate;
//    }
    //delivery
//    public DataClass(String dataStatus,String dataExpense,String dataAmount,String dataPaymentvia,String dataDeliveryTime) {
//        this.dataStatus = dataStatus;
//        this.dataExpense = dataExpense;
//        this.dataAmount= dataAmount;
//        this.dataPaymentVia =dataPaymentvia;
//        this.dataDeliveryTime=dataDeliveryTime;
//    }
    public DataClass(String dataName, String dataPhone, String dataBrand, String dataModel, String dataColour, String dataPassword, String dataComplaint, String dataStatus, List<String> dataImage,String dataTime,String maxjob,String estimate,String dataExpense,String dataAmount,String dataPaymentvia,String dataDeliveryTime,String from) {
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
        this.dataJobNo=maxjob;
        this.dataEstimate=estimate;
        this.dataExpense = dataExpense;
        this.dataAmount= dataAmount;
        this.dataPaymentVia =dataPaymentvia;
        this.dataDeliveryTime=dataDeliveryTime;
        this.dataBoughtfrom=from;
    }
    public DataClass(){

    }
}