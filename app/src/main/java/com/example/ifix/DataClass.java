package com.example.ifix;


public class DataClass {

    private String dataName;
    private String dataPhone;
    private String dataBrand;
    private String dataModel;
    private String dataColour;
    private String dataPassword;
    private String dataComplaint;
    private String dataImage;
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
    public String getDataModel() {
        return dataModel;
    }
    public String getDataColour() {
        return dataColour;
    }
    public String getDataPassword() {
        return dataPassword;
    }
    public String getDataComplaint() {
        return dataComplaint;
    }


    public String getDataImage() {
        return dataImage;
    }

    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage) {
        this.dataName = dataName;
        this.dataPhone = dataPhone;
        this.dataBrand = dataBrand;
        this.dataImage = dataImage;
    }
    public DataClass(){

    }
}