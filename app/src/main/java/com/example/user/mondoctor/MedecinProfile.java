package com.example.user.mondoctor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MedecinProfile extends AppCompatActivity {
String ID;
TextView txtName;
TextView txtPhone;
TextView txtVille;
TextView CodePostalMedecin;
TextView txtAdresse;
Button buRDV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medecin_profile);
        Bundle bundle = getIntent().getExtras();
        txtName = (TextView) findViewById(R.id.txtName);
        txtPhone = (TextView)findViewById(R.id.txtPhone);
        txtVille = (TextView)findViewById(R.id.txtVille);
        txtAdresse = (TextView)findViewById(R.id.txtAdresse);
        CodePostalMedecin = (TextView)findViewById(R.id.CodePostalMedecin);
        buRDV = (Button)findViewById(R.id.buRDV);
        this.ID = bundle.getString("ID");
        String url="http://10.0.2.2:3000/users/AfficherMedecin?idMedecin="+ID;
        new MyAsyncTaskgetNews().execute(url, "news");
        buRDV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),AddRDV.class);
                Bundle bundle = new Bundle();
                bundle.putString("IDMedecin",ID);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

    }
    public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }
        @Override
        protected String  doInBackground(String... params) {

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
                JSONObject json= new JSONObject(progress[0]);
                JSONArray Medecin=new JSONArray( json.getString("info"));
                JSONObject js=Medecin.getJSONObject(0);
                //display response data
                txtName.setText(js.getString("NomMedecin")+" "+js.getString("PrenomMedecin"));
                txtPhone.setText(js.getString("NumTelPortableMedecin")+"/"+js.getString("NumTelFixMedecin"));
                txtVille.setText("Ville : "+js.getString("VilleMedecin"));
                CodePostalMedecin.setText("Code Postale : "+js.getString("CodePostalMedecin"));
                txtAdresse.setText("Adresse : "+js.getString("AdresseMedecin"));

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
