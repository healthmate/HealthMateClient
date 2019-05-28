package com.healthmate.client.Auth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
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

public class Register extends AppCompatActivity {

    Uri imageUri;
    String myurl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    String firstname;
    String lastname;
    String email;
    String username;
    String password;
    String age;
    String gender;
    ImageView profile_pic;

    InputStream is = null;
    String line = null;
    String result = null;
    ProgressDialog progressDialog;
    String message,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText Ed_firstname = findViewById(R.id.firstname);
        final EditText Ed_lastname = findViewById(R.id.lastname);
        final EditText Ed_email = findViewById(R.id.email);
        final EditText Ed_username = findViewById(R.id.username);
        final EditText Ed_password = findViewById(R.id.password);
        final EditText Ed_age = findViewById(R.id.age);
        final Spinner spinner = findViewById(R.id.gender);
        profile_pic = findViewById(R.id.profile_image);
        final Button btn_register = findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference("users");

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Register.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname = Ed_firstname.getText().toString();
                lastname = Ed_lastname.getText().toString();
                email = Ed_email.getText().toString();
                username = Ed_username.getText().toString();
                password = Ed_password.getText().toString();
                age = Ed_age.getText().toString();
                gender = spinner.getSelectedItem().toString();
                uploadImage();

            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(Register.this);
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        progressDialog.setMessage("Registering...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(imageUri != null){

            final StorageReference filereference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        progressDialog.dismiss();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                        new RegisterTask().execute(firstname, lastname,email, username, password,
                                age, gender, myurl);


                    }else{
                        Toast.makeText(getApplicationContext(),"Outer Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
        }else{
            Toast.makeText(this,"No Image Selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(result != null) {
                imageUri = result.getUri();

                profile_pic.setImageURI(imageUri);

            }else{
                finish();
            }
        }else{
            Toast.makeText(this,"Something gone wrong", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class RegisterTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            String i_firstname = strings[0];
            String i_lastname = strings[1];
            String i_email = strings[2];
            String i_username = strings[3];
            String i_password = strings[4];
            String i_age = strings[5];
            String i_gender = strings[6];
            String i_profile_pic = strings[7];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/auth/register");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                JSONObject jsonparam = new JSONObject();
                jsonparam.put("first_name",i_firstname)
                        .put("last_name",i_lastname)
                        .put("email",i_email)
                        .put("password",i_password)
                        .put("username",i_username)
                        .put("age",i_age)
                        .put("gender",i_gender)
                        .put("profile_pic",i_profile_pic);

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
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if(s != null){
                try {
                    message = s.getString("message");
                    status = s.getString("status");


                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Register.this, LogIn.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Registration failed",Toast.LENGTH_LONG).show();
            }

        }
    }

}
