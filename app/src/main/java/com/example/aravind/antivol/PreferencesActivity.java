package com.example.aravind.antivol;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.security.Permission;

public class PreferencesActivity extends PreferenceActivity{


    EditTextPreference dataOnPref,dataOffPref,passwordPref,backupNumPref,callBackPref,locationPref,ringerPref;
    SwitchPreference simPref;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Antivol");

        addPreferencesFromResource(R.xml.settings);

        PackageManager pm = getApplicationContext().getPackageManager();
        int hasCamPerm = pm.checkPermission(Manifest.permission.CAMERA, getApplicationContext().getPackageName());
        int hasNetPerm = pm.checkPermission(Manifest.permission.INTERNET, getApplicationContext().getPackageName());
        int hasExtStoragePerm = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,getApplicationContext().getPackageName());
        int hasRecieveSmsPerm = pm.checkPermission(Manifest.permission.RECEIVE_SMS,getApplicationContext().getPackageName());
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
            Toast.makeText(PreferencesActivity.this, "Please provide all the permissions required", Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            getApplicationContext().startActivity(i);

        }
        preferences = getSharedPreferences("AntiVolPreferences", Context.MODE_PRIVATE);

        simPref = (SwitchPreference)findPreference("simKey");
        Toast.makeText(PreferencesActivity.this, simPref.getSwitchTextOn(), Toast.LENGTH_SHORT).show();
        simPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    Toast.makeText(PreferencesActivity.this, "turned on", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("keyforsim", "on");
                    editor.commit();
                } else {
                    Toast.makeText(PreferencesActivity.this, "turned off", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("keyforsim", "off");
                    editor.commit();
                }
                return true;
            }
        });

        dataOnPref = (EditTextPreference)findPreference("dataonkey");
        dataOnPref.setText(preferences.getString("keyfordataon", "ondata"));
        dataOnPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                dataOnPref.setText((String) newValue);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("keyfordataon", (String) newValue);
                editor.commit();
                Toast.makeText(PreferencesActivity.this, (String) newValue, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        dataOffPref = (EditTextPreference)findPreference("dataoffkey");
        dataOffPref.setText(preferences.getString("keyfordataoff", "offdata"));
        dataOffPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                dataOffPref.setText((String) newValue);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("keyfordataoff", (String) newValue);
                editor.commit();
                Toast.makeText(PreferencesActivity.this, (String) newValue, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        passwordPref = (EditTextPreference)findPreference("antivolpasswordkey");
        passwordPref.setText(preferences.getString("passwordKey", ""));
        passwordPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().trim().length() < 6) {
                    Toast.makeText(PreferencesActivity.this, "Password less than 6 characters. So, setting to previous value", Toast.LENGTH_SHORT).show();
                    passwordPref.setText(preferences.getString("passwordKey", ""));
                    return false;
                } else {
                    passwordPref.setText((String) newValue);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("passwordKey", (String) newValue);
                    editor.commit();
                    Toast.makeText(PreferencesActivity.this, (String) newValue, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


        backupNumPref = (EditTextPreference)findPreference("backupnumberkey");
        backupNumPref.setText(preferences.getString("backupKey", ""));
        backupNumPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().trim().length() != 10 || phoneNoValidation(newValue.toString())) {
                    Toast.makeText(PreferencesActivity.this, "Phone number incorrect. So, setting the previous value", Toast.LENGTH_SHORT).show();
                    backupNumPref.setText(preferences.getString("backupKey", ""));
                    return false;
                } else {
                    passwordPref.setText((String) newValue);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("backupKey", (String) newValue);
                    editor.commit();
                    Toast.makeText(PreferencesActivity.this, (String) newValue, Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });

        callBackPref = (EditTextPreference)findPreference("callbackkey");
        callBackPref.setText(preferences.getString("keyforcallback","callback"));
        callBackPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                callBackPref.setText((String) newValue);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("keyforcallback", (String) newValue);
                editor.commit();
                Toast.makeText(PreferencesActivity.this, (String) newValue, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        locationPref = (EditTextPreference)findPreference("locationkey");
        locationPref.setText(preferences.getString("keyforlocation","getlocation"));
        locationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                locationPref.setText((String) newValue);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("keyforlocation", (String) newValue);
                editor.commit();
                Toast.makeText(PreferencesActivity.this, (String) newValue, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ringerPref = (EditTextPreference)findPreference("ringervolumekey");
        ringerPref.setText(preferences.getString("keyforringer", "ringer"));
        ringerPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ringerPref.setText((String) newValue);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("keyforringer", (String) newValue);
                editor.commit();
                Toast.makeText(PreferencesActivity.this, (String) newValue, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        Toast.makeText(PreferencesActivity.this, "Executed On create", Toast.LENGTH_SHORT).show();
        Log.d("Here","Look");
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

        backupNumPref.setText(preferences.getString("backupKey", ""));

        PackageManager pm = getApplicationContext().getPackageManager();
        int hasCamPerm = pm.checkPermission(Manifest.permission.CAMERA, getApplicationContext().getPackageName());
        int  hasNetPerm = pm.checkPermission(Manifest.permission.INTERNET, getApplicationContext().getPackageName());
        int hasExtStoragePerm = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,getApplicationContext().getPackageName());
        int hasRecieveSmsPerm = pm.checkPermission(Manifest.permission.RECEIVE_SMS,getApplicationContext().getPackageName());
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
                &&hasReadPhoneStatePrm == PackageManager.PERMISSION_GRANTED&&hasCallPhonePerm==PackageManager.PERMISSION_GRANTED
                &&hasCoarseLocPerm==PackageManager.PERMISSION_GRANTED&&hasFineLocPerm == PackageManager.PERMISSION_GRANTED)) {
            // do stuff
            Toast.makeText(PreferencesActivity.this, "Please provide all the permissions required", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_screen, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,UserDetailsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(this,FeedbackActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(this,HelpActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
