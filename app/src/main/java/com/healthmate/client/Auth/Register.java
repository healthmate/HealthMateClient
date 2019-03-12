package com.healthmate.client.Auth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.healthmate.client.R;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText Ed_firstname = findViewById(R.id.firstname);
        final EditText Ed_lastname = findViewById(R.id.lastname);
        final EditText Ed_email = findViewById(R.id.email);
        final EditText Ed_username = findViewById(R.id.username);
        final EditText Ed_password = findViewById(R.id.password);
        final Button btn_register = findViewById(R.id.register);

        String firstname = Ed_firstname.getText().toString();
        String lastname = Ed_lastname.getText().toString();
        String email = Ed_email.getText().toString();
    }
}
