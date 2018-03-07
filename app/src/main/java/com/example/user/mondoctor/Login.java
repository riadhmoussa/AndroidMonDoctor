package com.example.user.mondoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {
    EditText EDEmail;
    EditText EDPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EDEmail = (EditText)findViewById(R.id.EDEmail);
        EDPassword = (EditText)findViewById(R.id.EDPassword);
    }

    public void Register(View view) {

        Intent intent = new Intent(getApplicationContext(),Registration.class);
        startActivity(intent);
        finish();
    }

    public void Connection(View view) {
         String url = "http://10.0.2.2:3000/users/ConnexioClient?Email="+EDEmail.getText().toString()+"&MotDePasse="+EDPassword.getText().toString()+"";
        new MyAsyncTaskgetNews().execute(url);
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
                    Operations operations=new Operations(getApplicationContext());
                    NewsData = operations.ConvertInputToStringNoChange(in);
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
                //display response data
                if (json.getString("msg")==null)
                    return;
                else if (json.getString("msg").equalsIgnoreCase("pass login")) {
                    Toast.makeText(getApplicationContext(), json.getString("msg"), Toast.LENGTH_LONG).show();
                    JSONArray UserInfo=new JSONArray( json.getString("info"));
                    JSONObject UserCreintal= UserInfo.getJSONObject(0);
                    hideProgressDialog();
                    SaveSettings saveSettings= new SaveSettings(getApplicationContext());
                    saveSettings.SaveData(UserCreintal.getString("idClient"));
                    if(ServiceNotification.ServiceIsRun==false ) {
                        ServiceNotification.ServiceIsRun  = true;
                        //register the services to run in background
                        Intent intent = new Intent(getApplicationContext(), ServiceNotification.class);
                        // start the services
                        startService(intent);

                    }
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Email Or Password is Failed",Toast.LENGTH_LONG).show();
                }

            } catch (Exception ex) {
                Log.d("er",  ex.getMessage());
            }


        }

        protected void onPostExecute(String  result2){


        }




    }
    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
