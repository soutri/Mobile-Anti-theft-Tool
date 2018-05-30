package com.example.aravind.antivol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.provider.ContactsContract;
import android.telephony.CellLocation;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by aravind on 23-01-2016.
 */
public class BootReceiver extends BroadcastReceiver {

    static String demo = "ANtivolDebug";
    SharedPreferences sharedpreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Antivol Boot Done", Toast.LENGTH_LONG).show();
        Log.d("MOBI", "done");

        sharedpreferences = context.getSharedPreferences("AntiVolPreferences", Context.MODE_PRIVATE);
        String simSwitchBtnStatus = sharedpreferences.getString("keyforsim","off");

        if(simSwitchBtnStatus.equalsIgnoreCase("off")) {

            Log.d("ANtivolDebug","switch is off");
            TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String getSimSerialNumber = telemamanger.getSimSerialNumber();

            Log.d("ANtivolDebug",getSimSerialNumber);
            Log.d("ANtivolDebug",telemamanger.getSimOperatorName());
            String SSN = sharedpreferences.getString("SSN","");

            if(SSN.equals(getSimSerialNumber)){
                Log.d(demo,"Sim not changed");
            }
            else{
                    Toast.makeText(context, "Sim changed", Toast.LENGTH_SHORT).show();
                    Log.d(demo, "Sim changed");

                    try {
                        setMobileDataEnabled(context,true);
                    } catch (Exception e) {
                    e.printStackTrace();
                    }

                //Sending SMS to backup number
                    String phoneNumber = sharedpreferences.getString("backupKey","");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, "Your Sim has been changed. The new SSN is"+getSimSerialNumber, null, null);
                //Call the Camera
                    Intent intent1 = new Intent();
                    intent1.setClassName("com.example.aravind.antivol","com.example.aravind.antivol.SecretCamActivity");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("NewSSN",getSimSerialNumber);
                    context.startActivity(intent1);
            }
        }
        else{
            Log.d("ANtivolDebug","switch is on");
            Toast.makeText(context, "The switch was on", Toast.LENGTH_LONG).show();
            TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String getSimSerialNumber = telemamanger.getSimSerialNumber();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("SSN", getSimSerialNumber);
            editor.commit();
            Log.d(demo,"SSN is updated");

        }
    }

    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class conmanClass = null;
        try {
            conmanClass = Class.forName(conman.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field connectivityManagerField = null;
        try {
            connectivityManagerField = conmanClass.getDeclaredField("mService");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        connectivityManagerField.setAccessible(true);

        Object connectivityManager = null;
        try {
            connectivityManager = connectivityManagerField.get(conman);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Class connectivityManagerClass = null;
        try {
            connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method setMobileDataEnabledMethod = null;
        try {
            setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        setMobileDataEnabledMethod.setAccessible(true);

        try {
            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.d("Debug","done");
        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
    }

}
