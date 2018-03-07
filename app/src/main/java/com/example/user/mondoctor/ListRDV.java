package com.example.user.mondoctor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListRDV extends AppCompatActivity {
    ArrayList<AdapterItem>    listnewsData = new ArrayList<AdapterItem>();
    MyCustomAdapter myadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_rdv);
        SaveSettings saveSettings= new SaveSettings(getApplicationContext());
        String url="http://10.0.2.2:3000/users/AfficherPropreRDV?idClient="+saveSettings.ReturnId();
        // start background task
        new MyAsyncTaskgetNews().execute(url, "news");
        //listnewsData.add(new AdapterItem("1","2018-03-10","08:00","home/img.png","Riadh","Moussa","has rdv"));
       // myadapter=new MyCustomAdapter(listnewsData);
       // ListView  lsNews=(ListView)findViewById(R.id.LVNews);
       // lsNews.setAdapter(myadapter);//intisal with data
    }
    private class MyCustomAdapter extends BaseAdapter {
        public  ArrayList<AdapterItem>  listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<AdapterItem> listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
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


            final   AdapterItem s = listnewsDataAdpater.get(position);

if(s.type.equalsIgnoreCase("no rdv")){
    LayoutInflater mInflater = getLayoutInflater();
    View myView = mInflater.inflate(R.layout.news_ticket_no_news, null);
    return myView;
}else{
    LayoutInflater mInflater = getLayoutInflater();
    final View myView = mInflater.inflate(R.layout.single_row_rdv, null);
    TextView tvNomMedecin = (TextView)myView.findViewById(R.id.tvNomMedecin);
    tvNomMedecin.setText(s.NomMedecin+" "+s.PrenomMedecin);
    TextView tvDateTime = (TextView)myView.findViewById(R.id.tvDateTime);
    tvDateTime.setText(s.Date+" "+s.Time);
    return myView;
}

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

                JSONObject json= new JSONObject(progress[0]);
                if(json.getString("msg").equalsIgnoreCase("has rdv")){
                    JSONArray tweets=new JSONArray( json.getString("info"));
                    for (int i = 0; i <tweets.length() ; i++) {
                        // try to add the resourcess
                        JSONObject js=tweets.getJSONObject(i);

                        //add data and view it
                        listnewsData.add(new AdapterItem(js.getString("idDemandeRDV"),js.getString("DatePropose"),js.getString("DureeDemande"),js.getString("ImagePathMedecin"),js.getString("NomMedecin"),js.getString("PrenomMedecin"),"has rdv"));
                    }
                    myadapter=new MyCustomAdapter(listnewsData);
                    ListView  lsNews=(ListView)findViewById(R.id.LVNews);
                    lsNews.setAdapter(myadapter);//intisal with data

                }else{
                    listnewsData.add(new AdapterItem(null,null,null,null,null,null,"no rdv"));
                    myadapter=new MyCustomAdapter(listnewsData);
                    ListView  lsNews=(ListView)findViewById(R.id.LVNews);
                    lsNews.setAdapter(myadapter);//intisal with data
                }

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
