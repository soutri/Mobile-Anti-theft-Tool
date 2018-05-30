package com.example.aravind.antivol;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SecretCamActivity extends AppCompatActivity {

    File photo = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
    private String TAG = "Test";
    private Camera mCamera;
    private CameraPreview mPreview;
    SharedPreferences sharedPreferences;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("ERROR", "ON picture taken CALLED");
            Log.d("Look", new String(data));

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());

                fos.write(data);
                fos.close();
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_secret_cam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            // attempt to get a Camera instance
            Camera.CameraInfo info = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();

            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        c = Camera.open(i);
                    } catch (RuntimeException e) {
                        // Handle
                    }
                }
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                Toast.makeText(getApplicationContext(), "Surface Created", Toast.LENGTH_SHORT).show();
                Log.d("afsafdafdaf", "on surface created");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mCamera.takePicture(null, null, mPicture);
                        sharedPreferences = getSharedPreferences("AntiVolPreferences", Context.MODE_PRIVATE);
                        String SSN = (String)getIntent().getExtras().get("NewSSN");
                        String regEmailId = sharedPreferences.getString("emailIdKey","empty");
                        Session session = createSessionObject();
                        String subject = "SIM Change Alert!! Very Important!!";
                        String messageBody = "Your Sim has been changed and the SSN number that is currently present is "+SSN+". Please report to nearest Police Station if you don't recognize this simchange";
                        try {
                            Message message = createMessage(regEmailId, subject, messageBody, session);
                            new SendMailTask().execute(message);
                        } catch (AddressException e) {
                            e.printStackTrace();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d("afsafdafdaf", "taken picture");

                    }
                });
                thread.start();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }

    }


    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new GMailAuthenticator("antivol.grrr@gmail.com","antivol@123"));
    }

    public class GMailAuthenticator extends Authenticator {
        String user;
        String pw;
        public GMailAuthenticator (String username, String password)
        {
            super();
            this.user = username;
            this.pw = password;
        }
        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(user, pw);
        }
    }
    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email, email));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);

        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(messageBody, "text/html");
        mp.addBodyPart(htmlPart);

        File[] attachments = new File[2];

        attachments[0] = photo;
        attachments[1] = photo;
        Log.d("Look",photo
                .getAbsolutePath());
        for( int i = 0; i < attachments.length; i++ ) {

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            FileDataSource fileDataSource =new FileDataSource(attachments[i]);
            messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
            messageBodyPart.setFileName(attachments[i].getName());
            mp.addBodyPart(messageBodyPart);

        }

        message.setContent(mp);
        return message;
    }



    private class SendMailTask extends AsyncTask<Message, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(SecretCamActivity.this, "Please wait boot is not yet done", Toast.LENGTH_LONG).show();
    }
}
