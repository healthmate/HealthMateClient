package com.healthmate.client.Community;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.healthmate.client.Auth.LogIn;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.healthmate.client.Auth.LogIn.MY_PREFS_NAME;

public class Post extends AppCompatActivity {

    Uri imageUri;
    String myurl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView image_added;
    TextView post;
    EditText desciption;

    InputStream is = null;
    String line = null;
    String result = null;
    ProgressBar progressBar;
    String token;

    String message;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        image_added = findViewById(R.id.user_image);
        post = findViewById(R.id.commandPost);
        desciption = findViewById((R.id.description));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);
            Log.e("TOKEN", token);

        }

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();

            }
        });

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(Post.this);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        if(imageUri != null){

            final StorageReference filereference = storageReference.child(System.currentTimeMillis()
            + "." + getFileExtension(imageUri));
             uploadTask = filereference.putFile(imageUri);
             uploadTask.continueWithTask(new Continuation() {
                 @Override
                 public Object then(@NonNull Task task) throws Exception {
                     if(!task.isSuccessful()){
                         throw task.getException();
                     }

                     return filereference.getDownloadUrl();
                 }
             }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                 @Override
                 public void onComplete(@NonNull Task task) {
                     if(task.isSuccessful()){
                         Uri downloadUri = (Uri) task.getResult();
                         myurl = downloadUri.toString();

                         new PostTask().execute(desciption.getText().toString(),myurl, token);

                         progressDialog.dismiss();
                         finish();

                     }else{
                         Toast.makeText(getApplicationContext(),"Outer Failed",Toast.LENGTH_SHORT).show();
                     }
                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                 }
             });
        }else{
            Toast.makeText(this,"No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(result != null) {
                imageUri = result.getUri();

                image_added.setImageURI(imageUri);
                desciption.setText(imageUri.toString());
            }else{
                finish();
            }
        }else{
            Toast.makeText(this,"Something gone wrong", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @SuppressLint("StaticFieldLeak")
    class PostTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {


            progressBar = new ProgressBar(Post.this, null, android.R.attr.progressBarStyleLarge);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            String description = strings[0];
            String image_url = strings[1];
            String auth_token = strings[2];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/posts");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                JSONObject jsonparam = new JSONObject();
                jsonparam.put("description",description);
                jsonparam.put("image_url",image_url);

                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                out.write(jsonparam.toString());
                out.close();
                int resp=con.getResponseCode();
                Log.e("IOexcep", "connnect");
                Log.e("IOexcep", Integer.toString(resp));
                is = new BufferedInputStream(con.getInputStream());
                //READ IS content into a string
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();


                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                result = sb.toString();
                is.close();
                br.close();
                con.disconnect();

                return new JSONObject(result);


            } catch (MalformedURLException e) {
                Log.e("IOexcep", "Malformed URL");
            } catch (IOException e) {
                Log.e("IOexcep", "Not Connected");
            } catch (JSONException e) {
                Log.e("JSONexcep", "JSON Error");
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            try {
                message = s.getString("message");
                status = s.getString("status");


                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
