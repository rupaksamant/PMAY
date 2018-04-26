package com.sourcey.housingdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.pm.yojana.housingdemo.R;



/**
 * Created by Biswajit on 25-02-2018.
 */

public class PermissionUtil {
    public static final int OPEN_CAMERA = 1;
    public static final int OPEN_GALLERY = 2;
    public static final int CAM_PERMISSION = 3;
    public static final int GAL_PERMISSION = 4;

    public static final int CAMERA_REQUEST_OTHER_LAND = 12;
    public static final int  GALLERY_REQUEST_OTHER_LAND = 13;

    public static final int  CAMERA_REQUEST_PRESENT_HOUSE = 14;
    public static final int  GALLERY_REQUEST_PRESENT_HOUSE = 15;

    public static final int  CAMERA_REQUEST_LAND1 = 16;
    public static final int  GALLERY_REQUEST_LAND2 = 17;

    public static final int  CAMERA_REQUEST_LAND2 = 22;
    public static final int  GALLERY_REQUEST_LAND1 = 23;

    public static final int  CAMERA_REQUEST_INCOME = 18;
    public static final int  GALLERY_REQUEST_INCOME = 19;

    public static final int  CAMERA_REQUEST_BPL = 20;
    public static final int  GALLERY_REQUEST_RATION = 21;

    public static final int  CAMERA_REQUEST_RATION = 24;
    public static final int  GALLERY_REQUEST_BPL= 25;

    public static final int  CAMERA_REQUEST_IDPROOF = 26;
    public static final int  GALLERY_REQUEST_IDPROOF = 27;

    Context ctx;

    private String[] galleryPermissions = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    private String[] cameraPermissions = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
    };

    public String[] gpsPermissions = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
    };

    public String[] getGalleryPermissions(){
        return galleryPermissions;
    }

    public String[] getCameraPermissions() {
        return cameraPermissions;
    }

    public boolean verifyPermissions(int[] grantResults) {
        if(grantResults.length < 1){
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static int DIALOG_RESULT = 0;

    public void showAttachmentChooserDialog(final Context ctx, final AttachmentSelectionListener listener, final String attachmentName) {
        final CharSequence[] items = {"Take Picture", "Choose from Gallery"};

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx,
                R.style.AppCompatAlertDialogStyle);
        //alertDialog.setIndeterminate(true);
        alertDialog.setTitle(R.string.confirm_attachment);
        alertDialog.setCancelable(true);
        //alertDialog.setMessage(R.string.confirm_message);
        alertDialog.setItems(items , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Take Picture")) {
                    if (checkMarshMellowPermission()) {
                        if (verifyPermissions(ctx, getCameraPermissions()))
                            DIALOG_RESULT = OPEN_CAMERA;
                        else
                            DIALOG_RESULT = CAM_PERMISSION;
                        listener.onCameraItemClick(DIALOG_RESULT, attachmentName);
                    }
                } else {
                    if (verifyPermissions(ctx, getGalleryPermissions()))
                        DIALOG_RESULT = OPEN_GALLERY;
                    else
                        DIALOG_RESULT = GAL_PERMISSION;
                    listener.onGalleryItemClick(DIALOG_RESULT, attachmentName);
                }
            }
        });
        alertDialog.show();
    }




    public boolean verifyPermissions(Context context, String[] grantResults) {
        for (String result : grantResults) {
            if (ActivityCompat.checkSelfPermission(context, result) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean checkMarshMellowPermission(){
        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public boolean checkJellyBean(){
        return(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN);
    }

}
