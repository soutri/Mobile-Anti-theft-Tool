package com.example.aravind.antivol;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartScreen extends AppCompatActivity {

    Button submit_btn;
    EditText emailId_txtView,backupNum_txtView,password,rePassword;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle("Antivol");

        PackageManager pm = getApplicationContext().getPackageManager();
        int hasCamPerm = pm.checkPermission(Manifest.permission.CAMERA, getApplicationContext().getPackageName());
        int  hasNetPerm = pm.checkPermission(Manifest.permission.INTERNET, getApplicationContext().getPackageName());
        int hasExtStoragePerm = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getApplicationContext().getPackageName());
        int hasRecieveSmsPerm = pm.checkPermission(Manifest.permission.RECEIVE_SMS, getApplicationContext().getPackageName());
        int hasReadSmsPerm = pm.checkPermission(Manifest.permission.READ_SMS,getApplicationContext().getPackageName());
        int hasSendSmsPerm = pm.checkPermission(Manifest.permission.SEND_SMS,getApplicationContext().getPackageName());
        int hasChangeNetStatePerm = pm.checkPermission(Manifest.permission.CHANGE_NETWORK_STATE,getApplicationContext().getPackageName());
        int hasBootCompletePerm =pm.checkPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED,getApplicationContext().getPackageName());
        int hasReadPhoneStatePrm = pm.checkPermission(Manifest.permission.READ_PHONE_STATE,getApplicationContext().getPackageName());
        int hasCallPhonePerm = pm.checkPermission(Manifest.permission.CALL_PHONE,getApplicationContext().getPackageName());
        int hasCoarseLocPerm = pm.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,getApplicationContext().getPackageName());
        int hasFineLocPerm = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext().getPackageName());

        if (!(hasCamPerm == PackageManager.PERMISSION_GRANTED&&hasNetPerm ==PackageManager.PERMISSION_GRANTED
                &&hasExtStoragePerm == PackageManager.PERMISSION_GRANTED&&hasRecieveSmsPerm == PackageManager.PERMISSION_GRANTED
                &&hasReadSmsPerm == PackageManager.PERMISSION_GRANTED&&hasSendSmsPerm==PackageManager.PERMISSION_GRANTED
                &&hasChangeNetStatePerm == PackageManager.PERMISSION_GRANTED&&hasBootCompletePerm==PackageManager.PERMISSION_GRANTED
                &&hasReadPhoneStatePrm == PackageManager.PERMISSION_GRANTED&&hasCallPhonePerm == PackageManager.PERMISSION_GRANTED
                &&hasCoarseLocPerm == PackageManager.PERMISSION_GRANTED&&hasFineLocPerm == PackageManager.PERMISSION_GRANTED)) {
            // do stuff
            Toast.makeText(this, "Please provide all the permissions required", Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            getApplicationContext().startActivity(i);

        }

        sharedpreferences = getSharedPreferences("AntiVolPreferences", Context.MODE_PRIVATE);
        String regEmailId = sharedpreferences.getString("emailIdKey","empty");
        String regBackupNo = sharedpreferences.getString("backupKey","empty");
        String regPassword = sharedpreferences.getString("passwordKey","empty");

        if(!regEmailId.equalsIgnoreCase("empty")){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        emailId_txtView = (EditText)findViewById(R.id.registeredEmailId);
        backupNum_txtView = (EditText)findViewById(R.id.registeredBackUpNumber);
        password =(EditText)findViewById(R.id.AntivolPassword);
        rePassword = (EditText)findViewById(R.id.ReenterAntivolPassword);

        submit_btn = (Button)findViewById(R.id.submit);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = emailId_txtView.getText().toString();
                String backupNumber = backupNum_txtView.getText().toString();
                String pass = password.getText().toString();
                String repass = rePassword.getText().toString();
                if(emailId.trim().length()==0||backupNumber.trim().length()==0||
                        pass.trim().length()==0||repass.trim().length()==0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Fields can't be empty")
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
                else if(pass.length()<6){
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Password can't be less than 6characters")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else if(!pass.equals(repass)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Passwords don't match")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else{
                    TelephonyManager telemamanger = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String getSimSerialNumber = telemamanger.getSimSerialNumber();

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("emailIdKey", emailId);
                    editor.putString("backupKey", backupNumber);
                    editor.putString("passwordKey", pass);
                    editor.putString("keyfordataon","ondata");
                    editor.putString("keyfordataoff","offdata");
                    editor.putString("keyforcallback","callback");
                    editor.putString("keyforlocation","getlocation");
                    editor.putString("keyforringer","ringer");
                    editor.putString("keyforsim","off");
                    editor.putString("SSN",getSimSerialNumber);

                    editor.commit();
                    Log.d("ANtivolDebug","Start screen "+getSimSerialNumber);
                    Intent intent = new Intent(v.getContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
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
    protected void onResume() {

        PackageManager pm = getApplicationContext().getPackageManager();
        int hasCamPerm = pm.checkPermission(Manifest.permission.CAMERA, getApplicationContext().getPackageName());
        int  hasNetPerm = pm.checkPermission(Manifest.permission.INTERNET, getApplicationContext().getPackageName());
        int hasExtStoragePerm = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getApplicationContext().getPackageName());
        int hasRecieveSmsPerm = pm.checkPermission(Manifest.permission.RECEIVE_SMS, getApplicationContext().getPackageName());
        int hasReadSmsPerm = pm.checkPermission(Manifest.permission.READ_SMS,getApplicationContext().getPackageName());
        int hasSendSmsPerm = pm.checkPermission(Manifest.permission.SEND_SMS,getApplicationContext().getPackageName());
        int hasChangeNetStatePerm = pm.checkPermission(Manifest.permission.CHANGE_NETWORK_STATE,getApplicationContext().getPackageName());
        int hasBootCompletePerm =pm.checkPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED,getApplicationContext().getPackageName());
        int hasReadPhoneStatePrm = pm.checkPermission(Manifest.permission.READ_PHONE_STATE,getApplicationContext().getPackageName());
        int hasCallPhonePerm = pm.checkPermission(Manifest.permission.CALL_PHONE,getApplicationContext().getPackageName());
        int hasCoarseLocPerm = pm.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,getApplicationContext().getPackageName());
        int hasFineLocPerm = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext().getPackageName());

        if (!(hasCamPerm == PackageManager.PERMISSION_GRANTED&&hasNetPerm ==PackageManager.PERMISSION_GRANTED
                &&hasExtStoragePerm == PackageManager.PERMISSION_GRANTED&&hasRecieveSmsPerm == PackageManager.PERMISSION_GRANTED
                &&hasReadSmsPerm == PackageManager.PERMISSION_GRANTED&&hasSendSmsPerm==PackageManager.PERMISSION_GRANTED
                &&hasChangeNetStatePerm == PackageManager.PERMISSION_GRANTED&&hasBootCompletePerm==PackageManager.PERMISSION_GRANTED
                &&hasReadPhoneStatePrm == PackageManager.PERMISSION_GRANTED&&hasCallPhonePerm == PackageManager.PERMISSION_GRANTED
                &&hasCoarseLocPerm == PackageManager.PERMISSION_GRANTED&& hasFineLocPerm == PackageManager.PERMISSION_GRANTED)) {
            // do stuff
            Toast.makeText(this, "Please provide all the permissions required", Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            getApplicationContext().startActivity(i);

        }
        super.onResume();
    }

}
