package com.example.ifix;

public class DealerClass {
    private String dataBalance;
    private String datalastpaidtime;
    public String getDataBalance(){return dataBalance;}
    public String getDatalastpaidtime(){return datalastpaidtime;}

    public DealerClass(String Balance,String time){
        this.dataBalance=Balance;
        datalastpaidtime=time;
    }
    public DealerClass(){

    }
}
