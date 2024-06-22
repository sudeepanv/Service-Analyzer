package com.example.ifix;

public class DealerClass {
    private String dataBalance;
    private String name;
    private String phone;
    private String datalastpaidtime;
    public String getDataBalance(){return dataBalance;}
    public String getDatalastpaidtime(){return datalastpaidtime;}
    public String getName(){return name;}

    public String getPhone(){return phone;}

    public DealerClass(String name,String phone,String Balance,String time){
        this.dataBalance=Balance;
        this.name=name;
        this.phone=phone;
        datalastpaidtime=time;
    }
    public DealerClass(String Balance,String time){
        this.dataBalance=Balance;
        datalastpaidtime=time;
    }
    public DealerClass(){

    }
}
