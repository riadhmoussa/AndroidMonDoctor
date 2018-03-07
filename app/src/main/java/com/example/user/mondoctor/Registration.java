package com.example.user.mondoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Registration extends AppCompatActivity {
ImageView ImageClient ;
EditText EDNom;
EditText EDPrenom;
EditText EDAdresse;
EditText EDPhoneNumber;
EditText EDEmail;
EditText EDpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ImageClient = (ImageView)findViewById(R.id.ImageClient);
        EDNom = (EditText)findViewById(R.id.EDNom);
        EDPrenom = (EditText)findViewById(R.id.EDPrenom);
        EDAdresse = (EditText)findViewById(R.id.EDAdresse);
        EDPhoneNumber = (EditText)findViewById(R.id.EDPhoneNumber) ;
        EDEmail = (EditText)findViewById(R.id.EDEmail);
        EDpassword = (EditText)findViewById(R.id.EDpassword);
        ImageClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckUserPermsions();
            }
        });
    }


    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        LoadImage();// init the contact list

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadImage();// init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText( this,"your message" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    int RESULT_LOAD_IMAGE=346;
    void LoadImage(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageClient.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    public void Register(View view) {
        showProgressDialog();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://mondoctor-e0a88.appspot.com");
        DateFormat df = new SimpleDateFormat("ddMMyyHHmmss");
        Date dateobj = new Date();
        // System.out.println(df.format(dateobj));
// Create a reference to "mountains.jpg"
        final String ImagePath= df.format(dateobj) +".jpg";
        StorageReference mountainsRef = storageRef.child( ImagePath);
        ImageClient.setDrawingCacheEnabled(true);
        ImageClient.buildDrawingCache();
        // Bitmap bitmap = imageView.getDrawingCache();
        BitmapDrawable drawable=(BitmapDrawable)ImageClient.getDrawable();
        Bitmap bitmap =drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                String NomClient="";
                try {
                    //for space with name
                    NomClient = java.net.URLEncoder.encode( EDNom.getText().toString() , "UTF-8");
                    downloadUrl= java.net.URLEncoder.encode(downloadUrl , "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                String PrenomClient="";
                try {
                    //for space with name
                    PrenomClient = java.net.URLEncoder.encode( EDPrenom.getText().toString() , "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                String AdresseClient="";
                try {
                    //for space with name
                    AdresseClient = java.net.URLEncoder.encode( EDAdresse.getText().toString() , "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                //TODO:  login and register
               // String url = "http://10.0.2.2:3000/users?NomClient="+EDNom.getText().toString()+"&PrenomClient="+EDPrenom.getText().toString()+"&AdresseClient="+EDAdresse.getText().toString()+"&NumTelPortableClient="+EDPhoneNumber.getText().toString()+"&Email="+EDEmail.getText().toString()+"&MotDePasse="+EDpassword.getText().toString()+"&ImagePathClient="+downloadUrl+"";
                  String url = "http://10.0.2.2:3000/users/AjouterClient?NomClient="+NomClient+"&PrenomClient="+PrenomClient+"&AdresseClient="+AdresseClient+"&NumTelPortableClient="+EDPhoneNumber.getText().toString()+"&Email="+EDEmail.getText().toString()+"&MotDePasse="+EDpassword.getText().toString()+"&ImagePathClient="+downloadUrl+"";
               new MyAsyncTaskgetNews().execute(url);
                //hideProgressDialog();

            }
        });
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
                if (json.getString("msg").equalsIgnoreCase("Client Add")) {
                    Toast.makeText(getApplicationContext(), json.getString("msg"), Toast.LENGTH_LONG).show();
//login

                   // String url = "http://10.0.2.2:3000/users/ConnexioClient?Email="+EDAdresse.getText().toString()+"&MotDePasse="+EDpassword.getText().toString()+"";
                    //new MyAsyncTaskgetNews().execute(url);
                    //Toast.makeText(getApplicationContext(),json.getString("msg"),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);
                }

               /* if (json.getString("msg").equalsIgnoreCase("pass login")) {
                    JSONArray UserInfo=new JSONArray( json.getString("info"));
                    JSONObject UserCreintal= UserInfo.getJSONObject(0);
                    Toast.makeText(getApplicationContext(),UserCreintal.getString("user_id"),Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    SaveSettings saveSettings= new SaveSettings(getApplicationContext());
                    saveSettings.SaveData(UserCreintal.getString("idClient"));
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }*/

            } catch (Exception ex) {
                Log.d("er",  ex.getMessage());
            }


        }

        protected void onPostExecute(String  result2){


        }




    }
}
