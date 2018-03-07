package com.example.user.mondoctor;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by USER on 05/03/2018.
 */

public class ServiceNotification extends IntentService {
    public static boolean ServiceIsRun=false;
    public static int CommnetID=0;
    public ServiceNotification() {
        super("MyWebRequestService");
    }
    protected void onHandleIntent(Intent workIntent) {

        // continue sending the messages
        while ( ServiceIsRun) {
            //* get new news


            SaveSettings saveSettings= new SaveSettings(getApplicationContext());


            String url = "http://10.0.2.2:3000/users/NotificatioRDV?idClient="+saveSettings.ReturnId()+"&lastid="+CommnetID;
            // start background task
            new MyAsyncTaskgetNews().execute(url, "news");


            try{
                Thread.sleep(20000);
            }catch (Exception ex){}


        }
    }

public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {
    @Override
    protected void onPreExecute() {
        //before works
    }
    @Override
    protected String  doInBackground(String... params) {
        // TODO Auto-generated method stub
        try {
            String NewsData;
            //define the url we have to connect with
            URL url = new URL(params[0]);
            //make connect with url and send request
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //waiting for 7000ms for response
            urlConnection.setConnectTimeout(7000);//set timeout to 5 seconds

            try {
                //getting the response data
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //convert the stream to string
                NewsData = ConvertInputToStringNoChange(in);
                //send to display data
                publishProgress(NewsData);
            } finally {
                //end connection
                urlConnection.disconnect();
            }

        }catch (Exception ex){}
        return null;
    }
    protected void onProgressUpdate(String... progress) {

        try {
            //display response data
            //Toast.makeText(getApplicationContext(),progress[0],Toast.LENGTH_LONG).show();
            JSONObject json= new JSONObject(progress[0]);
            JSONArray tweets=new JSONArray( json.getString("info"));
            String msg="Bonjour , nous vous rappelons votre rendez-vous du";
            for (int i = 0; i <tweets.length() ; i++) {
                // try to add the resourcess
                JSONObject js=tweets.getJSONObject(i);
                CommnetID=js.getInt("idDemandeRDV");
               msg=msg+js.getString("DatePropose")+" "+js.getString("DureeDemande")+" avec Dr. "+js.getString("NomMedecin")+" "+js.getString("PrenomMedecin");

            }
            // creat new intent
            Intent intent = new Intent();
            //set the action that will receive our broadcast
            intent.setAction("com.example.Broadcast");
            // add data to the bundle

            intent.putExtra("msg", msg);
            // send the data to broadcast
            sendBroadcast(intent);
            //delay for 50000ms
        } catch (Exception ex) {
        }


    }

    protected void onPostExecute(String  result2){


    }




}

    // this method convert any stream to string
    public static String ConvertInputToStringNoChange(InputStream inputStream) {

        BufferedReader bureader=new BufferedReader( new InputStreamReader(inputStream));
        String line ;
        String linereultcal="";

        try{
            while((line=bureader.readLine())!=null) {

                linereultcal+=line;

            }
            inputStream.close();


        }catch (Exception ex){}

        return linereultcal;
    }
}