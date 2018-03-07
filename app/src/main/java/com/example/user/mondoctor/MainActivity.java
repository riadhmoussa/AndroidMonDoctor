package com.example.user.mondoctor;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
MyCustomAdapter myadapter;
    ArrayList<AdapterItems>    listnewsData = new ArrayList<AdapterItems>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SaveSettings saveSettings= new SaveSettings(getApplicationContext());
        saveSettings.LoadData();



        LoadMedecin();
        if(ServiceNotification.ServiceIsRun==false ) {
            ServiceNotification.ServiceIsRun  = true;
            //register the services to run in background
            Intent intent = new Intent(this, ServiceNotification.class);
            // start the services
            startService(intent);

        }
    }
    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<AdapterItems> listnewsDataAdpater ;
        Context context;
        public MyCustomAdapter(Context context,ArrayList<AdapterItems>  listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
            this.context=context;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            final   AdapterItems s = listnewsDataAdpater.get(position);


            if(s.status.equals("loading")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.news_ticket_loading, null);
                return myView;
            }
            else if(s.status.equals("notweet")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.news_ticket_no_news, null);
                return myView;
            }else{
                LayoutInflater mInflater = getLayoutInflater();
                final View myView = mInflater.inflate(R.layout.single_row_conact, null);
                TextView tvNomPrenom=( TextView)myView.findViewById(R.id.tvNomPrenom);
                tvNomPrenom.setText(s.NomMedecin+" "+s.PrenomMedecin);
                TextView tvTelephone = (TextView)myView.findViewById(R.id.tvTelephone);
                tvTelephone.setText(s.NumTelephone);
                ImageView imMedecin = (ImageView)myView.findViewById(R.id.imMedecin);
                myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),MedecinProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("ID",s.ID);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                return myView;
            }



        } }
        void LoadMedecin(){
            String url="http://10.0.2.2:3000/users/AfficherListeMedecin";
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
                JSONObject json= new JSONObject(progress[0]);
                //Toast.makeText(getApplicationContext(),json.getString("msg"),Toast.LENGTH_LONG).show();
                if(json.getString("msg").equalsIgnoreCase("has medecin")){
                    JSONArray tweets=new JSONArray( json.getString("info"));
                    for (int i = 0; i <tweets.length() ; i++) {
                        // try to add the resourcess
                        JSONObject js=tweets.getJSONObject(i);

                        //add data and view it
                        listnewsData.add(new AdapterItems(js.getString("idMedecin"),js.getString("NomMedecin"),js.getString("PrenomMedecin"),js.getString("ImagePathMedecin"),js.getString("NumTelPortableMedecin"),"loadingggg"));
                    }
                   // listnewsData.add(new AdapterItems(null,null,null,null,null,"loading"));
                    //listnewsData.add(new AdapterItems(null,"Bayen","Moussa",null,"97275910","loadingggg"));
                    myadapter=new MyCustomAdapter(getApplicationContext(),listnewsData);
                    ListView lsNews=(ListView)findViewById(R.id.LVNews);
                    lsNews.setAdapter(myadapter);//intisal with data
                }else{
                    listnewsData.add(new AdapterItems(null,null,null,null,null,"notweet"));
                    myadapter=new MyCustomAdapter(getApplicationContext(),listnewsData);
                    ListView lsNews=(ListView)findViewById(R.id.LVNews);
                    lsNews.setAdapter(myadapter);//intisal with data
                }


            } catch (Exception ex) {
            }


        }

        protected void onPostExecute(String  result2){


        }




    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.RDV:
                Intent intent = new Intent(getApplicationContext(),ListRDV.class);
                startActivity(intent);
                Toast.makeText(this,"rdv",Toast.LENGTH_LONG).show();
                return true;
            case R.id.setting:
                SaveSettings saveSettings= new SaveSettings(getApplicationContext());
                saveSettings.Deconnection();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    }

