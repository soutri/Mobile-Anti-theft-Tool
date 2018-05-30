package com.example.aravind.antivol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserDetailsActivity extends AppCompatActivity {

    Button change_btn;
    EditText emailId_txtView,backupNum_txtView,oldPassword;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedpreferences = getSharedPreferences("AntiVolPreferences", Context.MODE_PRIVATE);
        emailId_txtView = (EditText)findViewById(R.id.antivolEmailId);
        emailId_txtView.setText(sharedpreferences.getString("emailIdKey",""));

        backupNum_txtView = (EditText)findViewById(R.id.antivolBackUpNumber);
        backupNum_txtView.setText(sharedpreferences.getString("backupKey",""));

        oldPassword = (EditText)findViewById(R.id.AntivolPasswordChange);

        change_btn = (Button)findViewById(R.id.change);
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = emailId_txtView.getText().toString();
                String backupNumber = backupNum_txtView.getText().toString();
                String pass = oldPassword.getText().toString();

                if(emailId.trim().length()==0||backupNumber.trim().length()==0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Emailid and backup number  can't be empty")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else if(emailValidation(emailId)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Email id format is incorrect")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else if(backupNumber.length()!=10){
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Incorrect mobile number")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else if(phoneNoValidation(backupNumber)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Mobile number should contain only digits")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else if(pass.length()==0){
                    Toast.makeText(UserDetailsActivity.this, "Enter password to update the fields", Toast.LENGTH_SHORT).show();
                }
                else if(pass.equals(sharedpreferences.getString("passwordKey",""))){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("emailIdKey", emailId);
                    editor.putString("backupKey", backupNumber);
                    editor.commit();
                    Toast.makeText(UserDetailsActivity.this, "Details updated successfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(UserDetailsActivity.this, "Password is incorrect. So, can't update details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean emailValidation(String email){

        if(email.lastIndexOf("@")<email.lastIndexOf(".")){
            if(email.endsWith(".com")||email.endsWith(".co.in")){
                return false;
            }
            return true;
        }
        return true;
    }

    public boolean phoneNoValidation(String phoneNum){
        String regex = "\\d+";
        if(phoneNum.matches(regex)){
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //do your own thing here
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

}
