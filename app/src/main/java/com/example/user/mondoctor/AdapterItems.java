package com.example.user.mondoctor;

/**
 * Created by USER on 28/02/2018.
 */

public class AdapterItems {
    public   String  ID;
    public  String NomMedecin;
    public  String PrenomMedecin;
    public String NumTelephone;
    public String  PathImage;
    public String status;
    //for news details
    AdapterItems( String ID, String NomMedecin,String PrenomMedecin,String PathImage,String NumTelephone,String status)
    {
        this. ID=ID;
        this. NomMedecin= NomMedecin;
        this. PrenomMedecin = PrenomMedecin;
        this.PathImage = PathImage;
        this.status = status;
        this.NumTelephone = NumTelephone;
    }
}
