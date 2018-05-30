package com.example.aravind.antivol;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button login_btn;
    EditText password_txtview;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle("Antivol");


        sharedpreferences = getSharedPreferences("AntiVolPreferences", Context.MODE_PRIVATE);

        password_txtview = (EditText)findViewById(R.id.password);

        login_btn = (Button)findViewById(R.id.Login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regPassword = sharedpreferences.getString("passwordKey","");

                if(password_txtview.getText().toString().equals(regPassword)){
                    Toast.makeText(LoginActivity.this, "Password Correct", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(),PreferencesActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, regPassword, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
