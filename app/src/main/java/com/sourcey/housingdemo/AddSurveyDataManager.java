package com.sourcey.housingdemo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.util.Log;

import com.sourcey.housingdemo.adapter.SurveyDataAdapter;
import com.sourcey.housingdemo.adapter.SurveyDataAdapterNonSlum;
import com.sourcey.housingdemo.modal.PmayDatabaseHelper;
import com.sourcey.housingdemo.modal.SurveyDataModal;
import com.sourcey.housingdemo.restservice.AddSurveyRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Biswajit on 03-03-2018.
 */

public class AddSurveyDataManager {

    public static final String APPLICANT_PHTO = "applicantPhoto";
    public static final String ID_PHTO = "idPhoto";
    public static final String PRESENT_HOUSE_PHTO = "presentInfrontHousePic";
    public static final String LOCATION_PHTO =  "locationDetailsPic";
    public static final String LAND_RECORD_PHTO = "landRecordPic";
    public static final String LAND1_PHTO = "landPhoto1";
    public static final String LAND2_PHTO = "landPhoto2";
    public static final String BPL_PHTO = "bplPicture";
    public static final String RATIONCD_PHTO = "rationCardPic";

    public static final String SIGNATURE_PHTO = "applicantSignature";
    public static final String VEHICLE_PHTO = "vehiclePhoto";
    public static final String INCOME_PHTO = "incomeProofPhoto";

   public File applicantPhotoFile;
   public File IdPhotoFile;
   public File presentHousePhotoFile;
   public File locationPhotoFile;
   public File landRecordPhotoFile;
   public File land1PhotoFile;
   public File land2PhotoFile;
   public File bplPhotoFile;

   public File rationPhotoFile;
   public File signaturePhotoFile;
   public File vehiclePhotoFile;
   public File incomePhotoFile;

    public byte[] slumBiometricDetails;

    public byte[] biometricDetails;

    public  int SAVED_COUNT = 0;
    public  int SUBMITTED_COUNT = 0;

    public AddSurveyRequest mAddSurveyRequest;
    public AddSurveyRequest mAddEditSurveyRequest;
    public static int mFieldscount = 0;
    List<MultipartBody.Part> mAttachments;
    public Context ctx;

    int missionSpinnerSaved = 0;
    boolean eligibleSaved = false;
    String eligibleReasonSaved;

    public boolean IsSyncEnabled = false;

    //offline

    Bitmap mBitmapApplicantOffline;
    Bitmap mBitmapHousePicOffline;
    Bitmap mBitmapFingerPrintOffline;

    private LocationListener mLocationListener;
    LocationManager mLocationMgr;
    public double latitude = 0;
    public double longitude = 0;

    public boolean isEdited = false;

    public static boolean IS_BIOMETRIC_RESTRICTED = true; //False = No BIO, true = with Bio

    public static AddSurveyDataManager mAddSurveyDataManager = null;
    public PmayDatabaseHelper mPmayDatabaseHelper;
    public List<SurveyDataModal> surveyDataModals; // full list to display

    public List<SurveyDataModal> SurveyDataListToDisplay; // search list to display

    public SurveyDataAdapter mSurveyDataAdapter;
    public SurveyDataAdapterNonSlum mSurveyDataAdapterNonSlum;

    public static AddSurveyDataManager getInstance() {
        if( mAddSurveyDataManager == null) {
            mAddSurveyDataManager = new AddSurveyDataManager();
        }
        return  mAddSurveyDataManager;
    }

    public int offlineRecords = 0;

    public int getRecordsCount(boolean isSubmitted) {
        int count = 0;
        offlineRecords = 0;
        List recordsList = surveyDataModals;
        if(recordsList.size() > 0 && !recordsList.isEmpty()) {
            Iterator<SurveyDataModal> itr = recordsList.iterator();
            while (itr.hasNext()) {
                SurveyDataModal surveyDataModal = itr.next();
                if(surveyDataModal == null) {
                    continue;
                }
                if(isSubmitted) {
                    if("Y".equals(surveyDataModal.isSubmitted)) {
                        count++;
                    }
                } else {
                    if(!"Y".equals(surveyDataModal.isSubmitted)) {
                        count++;
                    }
                }
                if("O".equals(surveyDataModal.isSubmitted) || "OSB".equals(surveyDataModal.isSubmitted)) {
                    offlineRecords++;
                }
            }
        }
        Log.v("PMAY", " Total records : "+count +"  , isSubmit: "+isSubmitted);
        return count;
    }

    private AddSurveyDataManager() {
        this.mAddSurveyRequest = new AddSurveyRequest();
        this.mAttachments = new ArrayList<MultipartBody.Part>();
        this.mPmayDatabaseHelper = new PmayDatabaseHelper(ctx);
        surveyDataModals = new ArrayList<>();
        SurveyDataListToDisplay = new ArrayList<>();
        mSurveyDataAdapter = new SurveyDataAdapter();
        mSurveyDataAdapterNonSlum = new SurveyDataAdapterNonSlum();
        initLocationListener();
    }

    void initLocationListener() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v("PMAY", " onLocationChanged() " + location.toString());
                if(location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
    }

    public String getConvertedGeoCoordinates(double longitude) {

        if (longitude == 0) return "0/1,0/1,0/1000";
        // You can adapt this to latitude very easily by passing location.getLatitude()
        String[] degMinSec = Location.convert(longitude, Location.FORMAT_SECONDS).split(":");
        return degMinSec[0] + "/1," + degMinSec[1] + "/1," + degMinSec[2] + "/1000";
    }

   /* public String getlatitudeGeoCoordinates(double latitude ) {

        if (latitude == 0) return "0/1,0/1,0/1000";
        // You can adapt this to latitude very easily by passing location.getLatitude()
        String[] degMinSec = Location.convert(latitude, Location.FORMAT_SECONDS).split(":");
        return degMinSec[0] + "/1," + degMinSec[1] + "/1," + degMinSec[2] + "/1000";
    }*/

    public void requestLocationUpdates(Context ctx) {
        mLocationMgr =(LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        PermissionUtil permissionUtil = new PermissionUtil();
        try {
            if (permissionUtil.verifyPermissions(ctx, permissionUtil.gpsPermissions)) {
                //40 seconds, 200 meters
                mLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 200, mLocationListener);
                Log.v("PMAY", " requestLocationUpdates ");
            }
        } catch (SecurityException e) {
            Log.v("PMAY", " requestLocationUpdates() exception ");
        }
    }

   public void checkLastKnowLocation() {
       try {
           if (latitude == 0 || longitude == 0) {
               Location loc = mLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               if (loc != null) {
                   latitude = loc.getLatitude();
                   longitude = loc.getLongitude();
                   Log.v("PMAY", " checkLastKnowLocation() >>  lastKnownLoc is used <<<  lat : " + latitude);
               }
           }
       } catch (SecurityException e) {

       }
    }

   public void setGeoTaggingAttributes(String imagePath) {
        try {
            if (latitude == 0 || longitude == 0) {
                Location loc = mLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(loc != null) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                    Log.v("PMAY", " setGeoTaggingAttributes() >>  lastKnownLoc is used <<<  lat : "+latitude);
                }
            }
            AddSurveyDataManager.getInstance().mAddSurveyRequest.geoLatitude = Double.toString(latitude);
            AddSurveyDataManager.getInstance().mAddSurveyRequest.geoLongitude = Double.toString(longitude);
            ExifInterface exif = new ExifInterface(imagePath);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, getConvertedGeoCoordinates(latitude));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, getConvertedGeoCoordinates(longitude));
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitude < 0 ? "S" : "N");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitude < 0 ? "W" : "E");
            exif.saveAttributes();
            Log.v("PMAY", " setGeoTaggingAttributes() success : long, lat :   " +longitude +" , "+latitude);
        } catch(SecurityException e) {

        } catch(Exception e) {

        }
    }


    public void stopLocationUpdates() {
        Log.v("PMAY", " stopLocationUpdates() "+mLocationMgr);
        latitude = 0;
        longitude = 0;
        if(mLocationMgr != null)
            mLocationMgr.removeUpdates(mLocationListener);
    }


    public MultipartBody.Part getMultiPartFile(File file, String actualFileName) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(actualFileName, file.getName(), requestBody);
    }

    public File getImageFilefromGallery(Uri uri, Context ctx) {
        //Bitmap thumbnail = null;
        File file = null;
        try {
            //thumbnail = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), uri);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = ctx.getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            if (filePath != null) {
                file = new File(filePath);
                Log.v("PMAY", " Gallery item : " + filePath +"  , file : "+file.getName());
        }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("PMAY", " exception getImageFilefromGallery : "+uri);
        }
        return file;
    }

    public File createImageFile(Bitmap thumbnail, String fileName, File file) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        file = new File(Environment.getExternalStorageDirectory(),
                fileName + ".jpg");
        FileOutputStream fo;
        try {
            file.createNewFile();
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.v("PMAY" ," destination  "+file.getPath());
        return file;

    }

    public File getImageFileSaved(Context ctx, String aadhar, String fileName) {
        File internalStorage = ctx.getDir("OfflineImages", Context.MODE_PRIVATE);
        File reportFilePath = new File(internalStorage, aadhar+"_"+fileName+ ".jpg");
        if(reportFilePath != null && reportFilePath.exists() && reportFilePath.isFile()) {
            Log.v("PMAY", " getImageFileSaved : " +reportFilePath.getName());
            return  reportFilePath;
        }
        return  null;
    }
    //fileName : adhar_name
    public void storeOfflineImage(Context ctx, Bitmap picture, String aadhar, String fileName) {
        String picturePath = "";
        File internalStorage = ctx.getDir("OfflineImages", Context.MODE_PRIVATE);
        File reportFilePath = new File(internalStorage, aadhar+"_"+fileName+ ".jpg");
        picturePath = reportFilePath.toString();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(reportFilePath);
            picture.compress(Bitmap.CompressFormat.JPEG, 100 /*quality*/, fos);
            fos.close();
            fos = null;
            Log.v("PMAY", " storeOfflineImage picturePath  "+internalStorage +"  , file : "+reportFilePath.getName() +" , "+reportFilePath.isFile());
        }
        catch (Exception ex) {
            Log.v("PMAY", "storeOfflineImage exception " +ex.getMessage());
            picturePath = "";
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                }catch (Exception e) {

                }
            }
        }

    }

}
