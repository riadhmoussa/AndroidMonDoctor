package com.example.user.mondoctor;

/**
 * Created by USER on 04/03/2018.
 */

public class AdapterItem {
    public   String ID;
    public  String Date;
    public  String Time;
    public String PictrePath;
    public String NomMedecin;
    public String PrenomMedecin;
public String type;
    //for news details
    AdapterItem( String ID, String Date,String Time,String PictrePath,String NomMedecin,String PrenomMedecin,String type)
    {
        this. ID=ID;
        this. Date = Date;
        this. Time = Time;
        this. PictrePath = PictrePath;
        this. PrenomMedecin = PrenomMedecin;
        this. NomMedecin = NomMedecin;
        this. type = type;
    }
}
