package com.example.aravind.antivol;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SMSReceiver extends BroadcastReceiver implements LocationListener {

    SharedPreferences sharedpreferences;


    @Override
    public void onReceive(Context context, Intent intent) {

        sharedpreferences = context.getSharedPreferences("AntiVolPreferences", Context.MODE_PRIVATE);

        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "Test senderNum: " + senderNum + ", message: " + message, duration);
                    toast.show();

                    if(message.contains(sharedpreferences.getString("keyfordataon","ondata"))){
                        Toast.makeText(context, "inside dataon", Toast.LENGTH_SHORT).show();
                        Log.d("Debug","DataOn");
                        setMobileDataEnabled(context, true);
                    }

                    else if(message.contains(sharedpreferences.getString("keyfordataoff","offdata"))){
                        Log.d("Debug","DataOff");
                        setMobileDataEnabled(context,false);
                    }

                    else if(message.contains(sharedpreferences.getString("keyforlocation","onlocation"))){
                        Log.d("Debug","location");
                        LocationManager mLocationManager = (LocationManager)context.getApplicationContext().getSystemService(context.LOCATION_SERVICE);
                        List<String> providers = mLocationManager.getProviders(true);
                        Location bestLocation = null;
                        try {
                            for (String provider : providers) {
                                Location l = mLocationManager.getLastKnownLocation(provider);
                                if (l == null) {
                                    continue;
                                }
                                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                                    // Found best last known location: %s", l);
                                    bestLocation = l;
                                }
                            }
                            Log.d("Debug",bestLocation.getLatitude()+" hi "+ bestLocation.getLongitude());
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(context, Locale.getDefault());

                            addresses = geocoder.getFromLocation(bestLocation.getLatitude(), bestLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();

                            String loc_msg = "Your phone is located in address: "+address+" ,city: "+city+" state: "+state+" postalcode: "+postalCode+" .Latitude is "+bestLocation.getLatitude()+" and Longitude is "+bestLocation.getLongitude();
                            SmsManager smsManager = SmsManager.getDefault();
                            ArrayList<String> msgArray = smsManager.divideMessage(loc_msg);

                            smsManager.sendMultipartTextMessage(phoneNumber, null,
                                    msgArray, null, null);

                            Log.d("Debug",address+" "+city+" "+state+" "+country+" "+postalCode);
                        }
                        catch (Exception e){
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, "Sorry, GPS was off!!", null, null);
                            Log.d("Debug","GPS off");
                        }

                    } else if (message.contains(sharedpreferences.getString("keyforcallback", "callback"))) {
                        Log.d("Debug", "callback");
                        try {
                            Toast.makeText(context, phoneNumber, Toast.LENGTH_SHORT).show();
                            Log.d("Hi", "calling");
                            /*Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phoneNumber));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            */
                            Intent intentcall = new Intent();
                            intentcall.setAction(Intent.ACTION_CALL);
                            intentcall.setData(Uri.parse("tel:" + phoneNumber));
                            intentcall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentcall);

                            Log.d("Hi","call done");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    else if(message.contains(sharedpreferences.getString("keyforringer","ringer"))){
                        Log.d("Debug","ringer");
                        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                        Uri defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(context.getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
                        Ringtone defaultRingtone = RingtoneManager.getRingtone(context.getApplicationContext(), defaultRintoneUri);
                        defaultRingtone.play();

                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

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

    @Override
    public void onLocationChanged(Location location) {
        Log.d("debug","Location changed");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("debug","Location changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("debug","Location changed");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("debug","on provider disabled");
    }
}
