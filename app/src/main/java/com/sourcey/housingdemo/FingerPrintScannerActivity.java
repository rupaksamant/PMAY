package com.sourcey.housingdemo;


/**
 * *****************************************************************************
 * C O P Y R I G H T  A N D  C O N F I D E N T I A L I T Y  N O T I C E
 * <p>
 * Copyright Â© 2008-2009 Access Computech Pvt. Ltd. All rights reserved.
 * This is proprietary information of Access Computech Pvt. Ltd.and is
 * subject to applicable licensing agreements. Unauthorized reproduction,
 * transmission or distribution of this file and its contents is a
 * violation of applicable laws.
 * *****************************************************************************
 * <p>
 * project FM220_Android_SDK
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpl.access_computech_fm220_sdk.FM220_Scanner_Interface;
import com.acpl.access_computech_fm220_sdk.acpl_FM220_SDK;
import com.acpl.access_computech_fm220_sdk.fm220_Capture_Result;
import com.acpl.access_computech_fm220_sdk.fm220_Init_Result;
import com.pm.yojana.housingdemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FingerPrintScannerActivity extends AppCompatActivity implements FM220_Scanner_Interface {

    private acpl_FM220_SDK FM220SDK;
    private Button Capture_No_Preview,Capture_PreView,Capture_BackGround,Capture_match,btnsetF,btngetF,btnREsetF;
    private TextView textMessage;
    private ImageView imageView;
    /***************************************************
     * if you are use telecom device enter "Telecom_Device_Key" as your provided key otherwise send "" ;
     */
    private static final String Telecom_Device_Key = "";
    private byte[] t1,t2;

    //region USB intent and functions
    private UsbManager manager;
    private PendingIntent mPermissionIntent;
    private UsbDevice usb_Dev;
    private static final String ACTION_USB_PERMISSION = "com.access.testappfm220.USB_PERMISSION";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                int pid, vid;
                pid = device.getProductId();
                vid = device.getVendorId();
                if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                    FM220SDK.stopCaptureFM220();
                    FM220SDK.unInitFM220();
                    usb_Dev=null;
                    textMessage.setText(R.string.msg_scanner_not_connected);
                    DisableCapture();
                }
            }
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            int pid, vid;
                            pid = device.getProductId();
                            vid = device.getVendorId();
                            if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText(R.string.msg_scanner_ready);
                                    EnableCapture();
                                    //Toast.makeText(getApplicationContext(), "Ready 1 "+Capture_PreView.performClick(), Toast.LENGTH_SHORT).show();
                                    imageView.setBackgroundColor(getResources().getColor(R.color.white));
                                    Capture_PreView.performClick();
                                }
                                else {
                                    textMessage.setText(R.string.msg_scanner_finger_error);
                                    DisableCapture();
                                }
                            }
                        }
                    } else {
                        textMessage.setText("Failed to connect to finger print scanner device");
                        //textMessage.setText("Fingerprint Scanner is ready");
                        DisableCapture();
                    }
                }
            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        // call method to set up device communication
                        int pid, vid;
                        pid = device.getProductId();
                        vid = device.getVendorId();
                        if ((pid == 0x8225)  && (vid == 0x0bca) && !FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if ((pid == 0x8220)  && (vid == 0x0bca)&& FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!",Toast.LENGTH_LONG).show();
                            finish();
                        }

                        if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                            if (!manager.hasPermission(device)) {
                                textMessage.setText(R.string.msg_scanner_initialising);
                                manager.requestPermission(device, mPermissionIntent);
                            } else {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText(R.string.msg_scanner_ready);
                                   // Toast.makeText(getApplicationContext(), "Ready 2", Toast.LENGTH_SHORT).show();
                                    EnableCapture();
                                    Capture_PreView.performClick();
                                }
                                else {
                                    textMessage.setText(R.string.msg_scanner_finger_error);
                                    DisableCapture();
                                }
                            }
                        }
                    }
                }
            }
        }
    };
    PermissionUtil permissionUtil;
    @Override
    protected void onResume() {
        super.onResume();
        if (permissionUtil.checkMarshMellowPermission()) {
            if (permissionUtil.verifyPermissions(this, permissionUtil.getCameraPermissions())) {

            } else
                ActivityCompat.requestPermissions(this, permissionUtil.getCameraPermissions(), 8);
        } else {

        }

        if(isPendingClick) {
            isPendingClick = false;
            Capture_PreView.performClick();
           // Toast.makeText(getApplicationContext(), "execute now "+Capture_PreView.performClick(), Toast.LENGTH_SHORT).show();
        }
    }

    boolean isPendingClick = false;

    @Override
    protected void onNewIntent(Intent intent) {
        if (getIntent() != null) {
            return;
        }
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) && usb_Dev==null) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call method to set up device communication & Check pid
                    int pid, vid;
                    pid = device.getProductId();
                    vid = device.getVendorId();
                    if ((pid == 0x8225)  && (vid == 0x0bca)) {
                        if (manager != null) {
                            if (!manager.hasPermission(device)) {
                                textMessage.setText(R.string.msg_scanner_initialising);
                                manager.requestPermission(device, mPermissionIntent);
                            }
//                            else {
//                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
//                                if (res.getResult()) {
//                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
//                                    EnableCapture();
//                                }
//                                else {
//                                    textMessage.setText("Error :-"+res.getError());
//                                    DisableCapture();
//                                }
//                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }



    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mUsbReceiver);
            FM220SDK.unInitFM220();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    //endregion

    /*final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 23) {
               Capture_PreView.performClick();
            }
        }
    };*/

    public  void cancelClick(View v) {
        finish();
    }

    AppCompatButton save, retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_scan);

        Toolbar mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Finger Print scan");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        permissionUtil = new PermissionUtil();
//        FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this);
        textMessage = (TextView) findViewById(R.id.textMessage);
        Capture_PreView = (Button) findViewById(R.id.button2);
        Capture_No_Preview = (Button) findViewById(R.id.button);
        Capture_BackGround= (Button) findViewById(R.id.button3);
        Capture_match =(Button) findViewById(R.id.button4);
        imageView = (ImageView)  findViewById(R.id.imageView);

        save = (AppCompatButton) findViewById(R.id.btn_save);
        save.setEnabled(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        retry = (AppCompatButton) findViewById(R.id.btn_retry);
        retry.setEnabled(false);
        retry.setTextColor(getResources().getColor(R.color.iron));
        save.setTextColor(getResources().getColor(R.color.iron));

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry.setEnabled(false);
                retry.setTextColor(getResources().getColor(R.color.iron));
                save.setEnabled(false);
                save.setTextColor(getResources().getColor(R.color.iron));
                textMessage.setText(R.string.msg_scanner_ready);
               Capture_PreView.performClick();
            }
        });

        /*btnsetF =(Button) findViewById(R.id.setflag);
        btngetF =(Button) findViewById(R.id.getflag);
        btnREsetF=(Button) findViewById(R.id.resetflag);*/

        //Region USB initialisation and Scanning for device
        SharedPreferences sp = getSharedPreferences("last_FM220_type", Activity.MODE_PRIVATE);
        boolean oldDevType = sp.getBoolean("FM220type", true);

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        final Intent piIntent = new Intent(ACTION_USB_PERMISSION);
        if (Build.VERSION.SDK_INT >= 16) piIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        mPermissionIntent = PendingIntent.getBroadcast(getBaseContext(), 1, piIntent, 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbReceiver, filter);
        UsbDevice device = null;
        for ( UsbDevice mdevice : manager.getDeviceList().values()) {
            int pid, vid;
            pid = mdevice.getProductId();
            vid = mdevice.getVendorId();
            boolean devType;
            if ((pid == 0x8225) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,true);
                devType=true;
            }
            else if ((pid == 0x8220) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,false);
                devType=false;
            } else {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
                devType=oldDevType;
            }
            if (oldDevType != devType) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("FM220type", devType);
                editor.apply();
            }
            if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                device  = mdevice;
                if (!manager.hasPermission(device)) {
                    textMessage.setText(R.string.msg_scanner_initialising);
                    manager.requestPermission(device, mPermissionIntent);
                } else {
                    Intent intent = this.getIntent();
                    if (intent != null && intent.getAction() != null) {
                        if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                            finishAffinity();
                        }
                    }
                    textMessage.setText(R.string.msg_scanner_initialising);
                    fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                    if (res.getResult()) {
                        textMessage.setText(R.string.msg_scanner_ready);
                        EnableCapture();
                        isPendingClick = true;
                        /* mHandler.sendEmptyMessageDelayed(23, 3000);
                        Toast.makeText(getApplicationContext(), "Ready 3: "  , Toast.LENGTH_SHORT).show();*/
                    }
                    else {
                        textMessage.setText(R.string.msg_scanner_finger_error);
                        DisableCapture();
                    }
                }
                break;
            }
        }
        if (device == null) {
            textMessage.setText(R.string.msg_scanner_not_connected);
            FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
        }

        //endregion


        Capture_BackGround.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableCapture();
                textMessage.setText(R.string.msg_scanner_initialising);
                imageView.setImageBitmap(null);
                FM220SDK.CaptureFM220(2);

            }
        });

         final String[] cameraPermissions = {
                "android.permission.CAMERA",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"
        };

/*        Capture_No_Preview.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableCapture();
                FM220SDK.CaptureFM220(2,true,false);
            }
        });*/

        Capture_PreView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityCompat.requestPermissions(FingerPrintScannerActivity.this, cameraPermissions, 10);
                //DisableCapture();
                FM220SDK.CaptureFM220(2,true,true);
            }
        });
        Capture_match.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***
                 * if t1 and t2 is byte so you can use MatchFM220(byte[],baye[]) function
                 and its String so you can use MatchFm220String(StringTmp1,StringTmp2) function
                 for your Matching templets. and you want at time match your finger with scaning process use
                 FM220SDK.MatchFM220(2, true, true, oldfingerprintisovale) function with pass old templet as perameter.
                 */

//                DisableCapture();
//                FM220SDK.MatchFM220(2, true, true, t1);

                if (t1 != null && t2 != null) {
                    if (FM220SDK.MatchFM220(t1, t2)) {
                        textMessage.setText("Finger matched");
                        t1 = null;
                        t2 = null;
                    } else {
                        textMessage.setText("Finger not matched");
                    }
                } else {
                    textMessage.setText("Pl capture first");
                }
//                String teamplet match example using FunctionBAse64 function .....
                FunctionBase64();
            }
        });
    }

    private void DisableCapture() {
        Capture_BackGround.setEnabled(false);
        Capture_No_Preview.setEnabled(false);
        Capture_PreView.setEnabled(true);
        Capture_match.setEnabled(false);
        retry.setEnabled(false);
        retry.setTextColor(getResources().getColor(R.color.iron));
        save.setEnabled(false);
        save.setTextColor(getResources().getColor(R.color.iron));
       // imageView.setImageBitmap(null);
    }
    private void EnableCapture() {
        Capture_BackGround.setEnabled(true);
        Capture_No_Preview.setEnabled(true);
        Capture_PreView.setEnabled(true);
        Capture_match.setEnabled(true);
        retry.setEnabled(false);
        save.setEnabled(false);
        retry.setTextColor(getResources().getColor(R.color.iron));
        save.setTextColor(getResources().getColor(R.color.iron));
    }
    private void FunctionBase64() {
        try {
            String t1base64, t2base64;
            if (t1 != null && t2 != null) {
                t1base64 = Base64.encodeToString(t1, Base64.NO_WRAP);
                t2base64 = Base64.encodeToString(t2, Base64.NO_WRAP);
                if (FM220SDK.MatchFM220String(t1base64, t2base64)) {
                    Toast.makeText(getBaseContext(), "Finger matched", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ScannerProgressFM220(final boolean DisplayImage,final Bitmap ScanImage,final boolean DisplayText,final String statusMessage) {
        FingerPrintScannerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DisplayText) {
                    textMessage.setText(R.string.msg_scanner_finger_error);
                    Log.v("PMAY", " Fingerprint statusMessage "+ statusMessage);
                    textMessage.invalidate();
                    if(statusMessage != null && statusMessage.contains("Capture fail") && statusMessage.contains("Aborted")) {
                        Capture_PreView.performClick();
                    }
                }
                if (DisplayImage) {
                    imageView.setImageBitmap(ScanImage);
                    imageView.setBackgroundColor(getResources().getColor(R.color.white));
                    imageView.invalidate();
                }
            }
        });
    }

    @Override
    public void ScanCompleteFM220(final fm220_Capture_Result result) {
        FingerPrintScannerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized())  EnableCapture();
                if (result.getResult()) {
                    imageView.setImageBitmap(result.getScanImage());
                    byte [] isotem  = result.getISO_Template();   // ISO TEMPLET of FingerPrint.....
//                    isotem is byte value of fingerprints
                    AddSurveyDataManager.getInstance().mBitmapFingerPrintOffline = result.getScanImage();
                    AddSurveyDataManager.getInstance().signaturePhotoFile = createImageFile(result.getScanImage(),
                            AddSurveyDataManager.SIGNATURE_PHTO );
                    if("S".equals(AddSurveyDataManager.getInstance().mAddSurveyRequest.chckSlumRadio)) {
                        AddSurveyDataManager.getInstance().slumBiometricDetails = isotem;
                    } else {
                        AddSurveyDataManager.getInstance().biometricDetails = isotem;
                    }

                    Log.v("fingerPrint", "ISO Byte  "+isotem.toString());
                    if (t1 == null) {
                        t1 = result.getISO_Template();
                    } else {
                        t2 = result.getISO_Template();
                    }
                    textMessage.setText(R.string.msg_finger_success);
                    if(AddSurveyDataManager.getInstance().signaturePhotoFile != null) {
                    }

                } else {
                   // imageView.setImageBitmap(null);
                    textMessage.setText(R.string.msg_scanner_finger_error);
                    Capture_PreView.performClick();
                    retry.setEnabled(false);
                    save.setEnabled(false);
                }
                imageView.invalidate();
                textMessage.invalidate();
            }
        });
    }

    public File createImageFile(Bitmap thumbnail, String fileName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File file = new File(Environment.getExternalStorageDirectory(),
                fileName + ".jpg");
        FileOutputStream fo = null;
        try {
            file.createNewFile();
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
            //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            retry.setEnabled(true);
            save.setEnabled(true);
            retry.setEnabled(true);
            save.setEnabled(true);
            retry.setTextColor(getResources().getColor(R.color.white));
            save.setTextColor(getResources().getColor(R.color.white));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fo  != null ) {

            }
        }

        Log.v("PMAY" ," destination  "+file.getPath());
        return file;

    }


    @Override
    public void ScanMatchFM220(final fm220_Capture_Result _result) {
        FingerPrintScannerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized()) EnableCapture();
                if (_result.getResult()) {
                    imageView.setImageBitmap(_result.getScanImage());
                    textMessage.setText("Finger matched\n" + "Success NFIQ:" + Integer.toString(_result.getNFIQ()));
                } else {
                    imageView.setImageBitmap(null);
                    textMessage.setText("Finger not matched\n" + _result.getError());
                }
                imageView.invalidate();
                textMessage.invalidate();
            }
        });
    }

}
