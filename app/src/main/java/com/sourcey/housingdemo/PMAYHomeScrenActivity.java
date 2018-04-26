package com.sourcey.housingdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Base64;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.pm.yojana.housingdemo.BuildConfig;
import com.sourcey.housingdemo.fragments.SlumFragment;
import com.sourcey.housingdemo.fragments.NonSlumFragment;
import com.pm.yojana.housingdemo.R;
import com.sourcey.housingdemo.modal.PmayDatabaseHelper;
import com.sourcey.housingdemo.modal.SurveyDataModal;
import com.sourcey.housingdemo.restservice.APIClient;
import com.sourcey.housingdemo.restservice.APIInterface;
import com.sourcey.housingdemo.restservice.AddSurveyRequest;
import com.sourcey.housingdemo.restservice.AddSurveyResponse;
import com.sourcey.housingdemo.restservice.Credential;
import com.sourcey.housingdemo.restservice.RetrofitSurveyDataResponse;
import com.sourcey.housingdemo.restservice.SurveyDataResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PMAYHomeScrenActivity extends AppCompatActivity {
    APIInterface apiInterface;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    TabsFragmentPagerAdapter adapter;

    SharedPreferences defaultPref;
    PmayDatabaseHelper mPmayDatabaseHelper;

    ArrayList<String> updatedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmay_homescreen);
        PMAYLogger.getWriteLogInstance(getApplicationContext());
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        Toolbar mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("PMAY-HFAPoA");
        //getSupportActionBar().ge
        mPmayDatabaseHelper = new PmayDatabaseHelper(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        if(!isWiFiDATAConnected()) {
            //fetch records from DB locally
            AddSurveyDataManager.getInstance().surveyDataModals =  mPmayDatabaseHelper.getAllUser();
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        addFragmentsToViewPager(viewPager);

        defaultPref = PreferenceManager.getDefaultSharedPreferences(this);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        updatedList = new ArrayList();
        Log.d("PMAY"," HomeScreen - onCreate() Device Model : "+Build.MODEL);
    }
    private void addFragmentsToViewPager(ViewPager viewPager) {
        adapter = new TabsFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SlumFragment(), "Saved Surveys");
        adapter.addFragment(new NonSlumFragment(), "Submitted Surveys");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    getSupportActionBar().setTitle("PMAY-HFAPoA Records: "+AddSurveyDataManager.getInstance().getRecordsCount(false));
                } else {
                    getSupportActionBar().setTitle("PMAY-HFAPoA Records: "+AddSurveyDataManager.getInstance().getRecordsCount(true));
                }
            }
        });
    }

    void refreshList() {
        Log.v("PMAY", "refreshList() slum List : "+AddSurveyDataManager.getInstance().surveyDataModals.size());
        if(AddSurveyDataManager.getInstance().surveyDataModals.size() >0 ) {
            AddSurveyDataManager.getInstance().mSurveyDataAdapter.mSurveyDataModals = AddSurveyDataManager.getInstance().surveyDataModals;
            Log.v("PMAY", " slum List : "+AddSurveyDataManager.getInstance().surveyDataModals.size());
            AddSurveyDataManager.getInstance().mSurveyDataAdapter.notifyDataSetChanged();

            AddSurveyDataManager.getInstance().mSurveyDataAdapterNonSlum.mSurveyDataModals = AddSurveyDataManager.getInstance().surveyDataModals;
            AddSurveyDataManager.getInstance().mSurveyDataAdapterNonSlum.notifyDataSetChanged();
            Log.v("PMAY", "Non slum List : "+AddSurveyDataManager.getInstance().surveyDataModals.size());
        }
        if(viewPager.getCurrentItem() == 0) {
            getSupportActionBar().setTitle("PMAY-HFAPoA Records: "+AddSurveyDataManager.getInstance().getRecordsCount(false));
        } else {
            getSupportActionBar().setTitle("PMAY-HFAPoA Records: "+AddSurveyDataManager.getInstance().getRecordsCount(true));
        }
    }

    void getAllSurveyDataFromServer() {

        Log.v(" PMAY ", " getAllSurveyDataFromServer ");
        if(!isWiFiDATAConnected()) {
            return;
        }
        progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Survey data..");
        progressDialog.show();


        UserDetails user = new UserDetails();
        user.userId = AddSurveyDataManager.getInstance().mAddSurveyRequest.userId;
        Call<List<SurveyDataResponse>> syncResponse =  apiInterface.getSurveyUserReportFromServer(user);

        syncResponse.enqueue(new Callback<List<SurveyDataResponse>>() {

            @Override
            public void onResponse(Call<List<SurveyDataResponse>> call, Response<List<SurveyDataResponse>> response) {

                // Log.v(" PMAY ", " getAllSurveyDataFromServer() : onResponse " +response.body());
                if(progressDialog != null) {
                    progressDialog.dismiss();
                }
                if(response != null && response.isSuccessful()) {
                    Log.v(" PMAY ", " getAllSurveyDataFromServer() : onResponse " +response.isSuccessful() + " , "+response.body().size());
                    AddSurveyDataManager.getInstance().surveyDataModals.clear();
                    mPmayDatabaseHelper.deleteAllRecords(); // online records
                    AddSurveyDataManager.getInstance().surveyDataModals =  mPmayDatabaseHelper.getAllUser(); // offline records
                    if(response.body() != null && !response.body().isEmpty()) {
                        Iterator<SurveyDataResponse> itr = response.body().iterator();
                        if (itr != null) {
                            while (itr.hasNext()) {
                                SurveyDataModal listItem = mapReceivedSurveyRecordFromServer(itr.next());
                                if(listItem != null) {
                                    AddSurveyDataManager.getInstance().surveyDataModals.add(listItem);
                                }
                            }
                        }
                    }
                    mPmayDatabaseHelper.closeDb();
                   refreshList();
                }
            }

            @Override
            public void onFailure(Call<List<SurveyDataResponse>> call, Throwable t) {
                if(progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    SurveyDataModal mapReceivedSurveyRecordFromServer(SurveyDataResponse surveyDataResponse) {
        SurveyDataModal listItem = null;
        if(surveyDataResponse != null) {
            listItem = new SurveyDataModal();
            AddSurveyRequest addSurveyRequest = new AddSurveyRequest();
            while (surveyDataResponse.aadharCardNumber !=null && surveyDataResponse.aadharCardNumber.trim().length() < 12) {
                surveyDataResponse.aadharCardNumber = "0"+surveyDataResponse.aadharCardNumber;
            }
            listItem.mSurveyId = surveyDataResponse.userSurveyId;
            listItem.mName = surveyDataResponse.familyHead;
            listItem.mAdharNo = surveyDataResponse.aadharCardNumber;
            listItem.mFatherName = surveyDataResponse.fatherOrHusbandName;
            listItem.mSlum = surveyDataResponse.slumNonSlum;
            listItem.isSubmitted = surveyDataResponse.submittedData;

            //Prepare for DB

            addSurveyRequest.surveyId = surveyDataResponse.userSurveyId;
            addSurveyRequest.familyHeadName = surveyDataResponse.familyHead;
            addSurveyRequest.adharNo = surveyDataResponse.aadharCardNumber;
            addSurveyRequest.fatherHusbandName = surveyDataResponse.fatherOrHusbandName;
            addSurveyRequest.slumRadio = surveyDataResponse.slumNonSlum;
            addSurveyRequest.isSubmitted = surveyDataResponse.submittedData;

            addSurveyRequest.ulbNameId = surveyDataResponse.ulbNameId;
            addSurveyRequest.wardDetails = surveyDataResponse.wardId;
            addSurveyRequest.genderId = surveyDataResponse.genderId;
            addSurveyRequest.dob = surveyDataResponse.dob;
            addSurveyRequest.eligibleStatus = surveyDataResponse.eligibilityForScheme;
            addSurveyRequest.nonEligibleReason = surveyDataResponse.reasonForNonEligibility;
            addSurveyRequest.preferredAssistanceHfa = surveyDataResponse.hfaCategoryId;
            if (surveyDataResponse.photoAttachmentInFrontOfHouse != null && !surveyDataResponse.photoAttachmentInFrontOfHouse.isEmpty()) {
                addSurveyRequest.isHousePicUploaded = true;
            } else {
                addSurveyRequest.isHousePicUploaded = false;
            }
            /*addSurveyRequest.adharNo = surveyDataResponse.validationPendingNonSlum;
            addSurveyRequest.adharNo = surveyDataResponse.slumNonSlumStatus;
            addSurveyRequest.adharNo = surveyDataResponse.aadharCardNumber;*/
            mPmayDatabaseHelper.addUser(addSurveyRequest);
        }
        return listItem;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("PMAY"," HomeScreen - onStart() ");
        if(AddSurveyDataManager.getInstance().IsSyncEnabled) {
            AddSurveyDataManager.getInstance().IsSyncEnabled = false;
            getAllSurveyDataFromServer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(searchView !=  null && searchView.getQuery() != null && searchView.getQuery().toString().isEmpty()) {
            refreshList();
        } else {
            if(AddSurveyDataManager.getInstance().isEdited && filterable != null && searchView.getQuery() != null) {
             //refresh search with new records
                AddSurveyDataManager.getInstance().isEdited = false;
                filterable.getFilter().filter(searchView.getQuery().toString());
            }
        }
       //
        if(AddSurveyDataManager.getInstance().mAddSurveyRequest != null &&
             "Y".equals(AddSurveyDataManager.getInstance().mAddSurveyRequest.isSubmitted)) {
            viewPager.setCurrentItem(1);
        } else {
            if(AddSurveyDataManager.getInstance().mAddSurveyRequest != null &&
                    (AddSurveyDataManager.getInstance().mAddSurveyRequest.isSubmitted != null))
                viewPager.setCurrentItem(0);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("PMAY"," HomeScreen - onStop() ");
        //AddSurveyDataManager.getInstance().SAVED_COUNT = 0;
        //AddSurveyDataManager.getInstance().SUBMITTED_COUNT = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("PMAY"," HomeScreen - onDestroy() ");
    }

    MenuItem searchMenuItem ;
    SearchView searchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchMenuItem = menu.findItem(R.id.search);
        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
            if (searchView != null) {
                searchView.setInputType(InputType.TYPE_CLASS_NUMBER);

                searchView.setQueryHint("search by Aadhar");
                initSearchQueryListner();
                //SearchViewCompat.setInputType(searchView, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
            }
        }
        return true;
    }

    void refreshSearchList() {
        if(AddSurveyDataManager.getInstance().SurveyDataListToDisplay == null) {
            return;
        }
        Log.v("PMAY", " refreshSearchList : "+AddSurveyDataManager.getInstance().SurveyDataListToDisplay.size());
        if(AddSurveyDataManager.getInstance().SurveyDataListToDisplay.size() >0 ) {
            AddSurveyDataManager.getInstance().mSurveyDataAdapter.mSurveyDataModals = AddSurveyDataManager.getInstance().SurveyDataListToDisplay;
            //Log.v("PMAY", " refreshSearchList: "+AddSurveyDataManager.getInstance().surveyDataModals.size());
            AddSurveyDataManager.getInstance().mSurveyDataAdapter.notifyDataSetChanged();

            AddSurveyDataManager.getInstance().mSurveyDataAdapterNonSlum.mSurveyDataModals = AddSurveyDataManager.getInstance().SurveyDataListToDisplay;
            AddSurveyDataManager.getInstance().mSurveyDataAdapterNonSlum.notifyDataSetChanged();
            //Log.v("PMAY", "refreshSearchList: "+AddSurveyDataManager.getInstance().SurveyDataListToDisplay.size());
        }
        /*if(viewPager.getCurrentItem() == 0) {
            getSupportActionBar().setTitle("PMAY-HFAPoA   Records: "+AddSurveyDataManager.getInstance().getRecordsCount(false));
        } else {
            getSupportActionBar().setTitle("PMAY-HFAPoA   Records: "+AddSurveyDataManager.getInstance().getRecordsCount(true));
        }*/
    }

    Filterable filterable = null;

    void initSearchQueryListner() {
        if(searchView == null)
            return;
        filterable = new Filterable() {

            @Override
            public Filter getFilter() {
                final AddSurveyDataManager mAddSurveyDataManager=  AddSurveyDataManager.getInstance();
               return new Filter() {

                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        Log.v(" PMAY ", " performFiltering() " +constraint);
                        FilterResults filterRes = new FilterResults();

                        if (constraint == null || constraint.toString().isEmpty()) {
                            mAddSurveyDataManager.SurveyDataListToDisplay.clear();
                            mAddSurveyDataManager.SurveyDataListToDisplay.addAll(mAddSurveyDataManager.surveyDataModals);
                            filterRes.values = mAddSurveyDataManager.SurveyDataListToDisplay; // full list
                        } else {
                            if(!mAddSurveyDataManager.surveyDataModals.isEmpty()) {
                                Iterator<SurveyDataModal> itr = mAddSurveyDataManager.surveyDataModals.iterator();
                                mAddSurveyDataManager.SurveyDataListToDisplay.clear();
                                if (itr != null) {
                                    for (Iterator<SurveyDataModal> itr1 = mAddSurveyDataManager.surveyDataModals.iterator(); itr1.hasNext();) {
                                        SurveyDataModal listItem = itr1.next();
                                        if(listItem != null && listItem.mAdharNo.contains(constraint.toString())) {
                                            mAddSurveyDataManager.SurveyDataListToDisplay.add(listItem);
                                        }
                                    }
                                }
                                filterRes.values = mAddSurveyDataManager.SurveyDataListToDisplay;
                            }
                        }
                        return filterRes;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        mAddSurveyDataManager.SurveyDataListToDisplay = (ArrayList<SurveyDataModal>)results.values;
                        refreshSearchList();
                    }
                };

            }
        };

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                filterable.getFilter().filter(newText);
                return false;

            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(" PMAY ", " onQueryTextSubmit() " +query);
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

      if(MenuItemCompat.isActionViewExpanded(searchMenuItem)) {
          MenuItemCompat.collapseActionView(searchMenuItem);
          //MenuItemCompat.
          return;
      }
        if(searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        moveTaskToBack(true);
    }

    AddSurveyRequest mAddSurveyEditRecord;

    String lastUploadedAdhar = null;



    void uploadOfflineRecords() {
        Log.v("PMAY", " uploadOfflineRecords() - Enter " );
        if(!isWiFiDATAConnected()) {
            Toast.makeText(this, "Please check your internet connections", Toast.LENGTH_LONG).show();
            return ;
        }
        List recordsList = AddSurveyDataManager.getInstance().surveyDataModals;
        if(updatedList == null) {
            updatedList = new ArrayList();
        }
        if(recordsList != null && recordsList.size() > 0 && !recordsList.isEmpty()) {
            Iterator<SurveyDataModal> itr = recordsList.iterator();
            while (itr != null && itr.hasNext()) {
                SurveyDataModal surveyDataModal = itr.next();
                if(surveyDataModal == null) {
                    continue;
                } if(updatedList.size() >0) {
                    for( String adhar : updatedList) {
                        if(adhar != null && adhar.equals(surveyDataModal.mAdharNo)) {
                            continue;
                        }
                    }
                }
                if("O".equals(surveyDataModal.isSubmitted) || "OSB".equals(surveyDataModal.isSubmitted)/*&& "0".equals(surveyDataModal.mSurveyId)*/) {
                    // non 0 also for saved and edited in offline
                    PmayDatabaseHelper helper =  new PmayDatabaseHelper(this);
                    updatedList.add(surveyDataModal.mAdharNo);
                    mAddSurveyEditRecord = helper.getSurveyDataById("0", surveyDataModal.mAdharNo);
                    Log.v("PMAY", " uploadOfflineRecords for aadhar : "+mAddSurveyEditRecord.slumAdharNo +" "+mAddSurveyEditRecord.adharNo);
                    /*Log.v("PMAY", " slum radio : "+mAddSurveyEditRecord.chckSlumRadio);
                    Log.v("PMAY", " non slum aadhar : "+mAddSurveyEditRecord.adharNo);
                    Log.v("PMAY", " non slum radio : "+mAddSurveyEditRecord.slumRadio);
                    Log.v("PMAY", " surveyDataModal.mAdharNo: "+surveyDataModal.mAdharNo);*/
                    if(mAddSurveyEditRecord != null) {
                        fetchOfflineImages(mAddSurveyEditRecord.adharNo);
                        createUploadRequest(mAddSurveyEditRecord);
                        break;
                    }

                }
            }

        }
        AddSurveyDataManager.getInstance().getRecordsCount(false);
        Log.v("PMAY", " uploadOfflineRecords : " +AddSurveyDataManager.getInstance().offlineRecords);

    }
    ProgressDialog mProgressDialog;

    void createUploadRequest(final AddSurveyRequest req) {

        AddSurveyDataManager addSurveyDataManager = AddSurveyDataManager.getInstance();
        addSurveyDataManager.isEdited = true;
        String userId = addSurveyDataManager.mAddSurveyRequest.userId;
        if(userId != null) {
            AddSurveyDataManager.getInstance().mAddSurveyRequest.userId = userId;
        } else {
            AddSurveyDataManager.getInstance().mAddSurveyRequest.userId = defaultPref.getString("USER_ID", null);
        }
        addSurveyDataManager.mAttachments.clear();
        List<MultipartBody.Part> attachments = addSurveyDataManager.mAttachments;

        if( addSurveyDataManager.applicantPhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.applicantPhotoFile, AddSurveyDataManager.APPLICANT_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.signaturePhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.signaturePhotoFile, AddSurveyDataManager.SIGNATURE_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.presentHousePhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.presentHousePhotoFile, AddSurveyDataManager.PRESENT_HOUSE_PHTO);
            attachments.add(applicantImage);
        }
        final Call<AddSurveyResponse> callUpload;

        //Geo-coordinates for offline saved records
        if(req.bplNo != null) {
           req.geoLatitude = req.bplNo;
            req.bplNo = null;
        }
        if(req.rationCardNo != null) {
            req.geoLongitude = req.rationCardNo;
            req.rationCardNo = null;
            Log.v("PMAY", "Geo-coordinates for offline records lat, long  "+ req.geoLatitude +" , "+req.geoLongitude);
        }
        Log.v("PMAY", "offline records lat, "+ req.bplNo +" , "+ req.isSubmitted +" flag : "+req.isSubmittedFlag);

        if("S".equals(req.slumRadio)) {
            if("OSB".equals(req.isSubmittedFlag) || "OSB".equals(req.isSubmitted)) {
                req.isSubmittedFlag = "Y";
            } else {
                req.isSubmittedFlag = "N";
            }
            req.slumAdharNo = req.adharNo;
            req.chckSlumRadio = "S";
            req.slumRadio = null;
            req.isSubmitted = null;
            req.adharNo = null;
            req.isSubmitted = null;
            //addSurveyDataManager.slumBiometricDetails = new byte[1];

            if(req.familyHeadName != null) {
                req.slumFamilyHeadName = req.familyHeadName;
                req.familyHeadName = null;
            }

            if(req.fatherHusbandName != null) {
                req.slumFatherHusbandName = req.fatherHusbandName;
                req.fatherHusbandName = null;
            }

            if(req.genderId != null) {
                req.genderValue = req.genderId;
            }
            if(req.ulbNameId != null) {
                req.slumUlbNameId = req.ulbNameId;
            }
            if(req.wardDetails != null) {
                req.slumWardDetails = req.wardDetails;
            }

            if(addSurveyDataManager.signaturePhotoFile != null) {
                addSurveyDataManager.slumBiometricDetails = getByteArray(addSurveyDataManager.signaturePhotoFile);
            }
            if(addSurveyDataManager.slumBiometricDetails != null) {
                RequestBody description = RequestBody.create(MultipartBody.FORM, addSurveyDataManager.slumBiometricDetails);
                callUpload =  apiInterface.uploadMultiFilesSurveyDataSlum(description, AddSurveyDataManager.getInstance().mAttachments, req);
            } else {
                callUpload =  apiInterface.uploadMultiFilesSurveyDataSlum(AddSurveyDataManager.getInstance().mAttachments, req);
            }

        } else {
            req.isSubmittedFlag = null;
            if("OSB".equals(req.isSubmitted)) {
                req.isSubmitted = "Y";
            } else {
                req.isSubmitted = "N";
            }

            req.slumRadio = "N";
            if(req.adharNo != null) {
                req.slumAdharNo = null;
                req.chckSlumRadio = null;
            }
            if(addSurveyDataManager.signaturePhotoFile != null) {
                addSurveyDataManager.biometricDetails = getByteArray(addSurveyDataManager.signaturePhotoFile);
            }
           // addSurveyDataManager.biometricDetails = new byte[1];

            if(addSurveyDataManager.biometricDetails != null) {
                RequestBody description = RequestBody.create(MultipartBody.FORM, addSurveyDataManager.biometricDetails);
                callUpload =  apiInterface.uploadMultiFilesSurveyData(description, AddSurveyDataManager.getInstance().mAttachments, req);
            } else {
                callUpload =  apiInterface.uploadMultiFilesSurveyData(AddSurveyDataManager.getInstance().mAttachments, req);
            }
        }

        callUpload.enqueue(new Callback<AddSurveyResponse>() {

            @Override
            public void onResponse(Call<AddSurveyResponse> call, Response<AddSurveyResponse> response) {
                Log.d("PMAY"," onResponse : ");
                if(response != null && response.body() != null) {
                    if(response.body().success) {
                        updatedSurveyDataToDB(response.body().surveyId, req);
                        Log.d("PMAY","UPLOAD response offline record  : "+response.body().surveyId +" is success : "+callUpload.isExecuted());
                        deleteImagesForthisRecord();
                        try{
                           Thread.sleep(350);
                           uploadOfflineRecords();

                        } catch (Exception e){

                        }
                        if( mProgressDialog != null && AddSurveyDataManager.getInstance().offlineRecords == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Successfully uploaded offline records", Toast.LENGTH_LONG).show();
                            if(filterable != null) {
                                filterable.getFilter().filter("");
                            }
                        }
                    } else {
                        if( mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            //Toast.makeText(PMAYHomeScrenActivity.this, "Failed to uploade offline records", Toast.LENGTH_LONG).show();
                            Log.d("PMAY"," onResponse - Not success : ");
                            showDuplicateAadharDialog(req);
                        }
                    }
                } else {
                    if( mProgressDialog != null) {
                        mProgressDialog.dismiss();
                       // Toast.makeText(PMAYHomeScrenActivity.this, "Failed to uploade offline records", Toast.LENGTH_LONG).show();
                        showDuplicateAadharDialog(req);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddSurveyResponse> call, Throwable t) {
                Log.d("PMAY"," onFailure() ");
               /* if( mProgressDialog != null) {
                    mProgressDialog.dismiss();               }*/
                    if( mProgressDialog != null) {
                        mProgressDialog.dismiss();
                       // Toast.makeText(PMAYHomeScrenActivity.this, "Failed to uploade offline records", Toast.LENGTH_LONG).show();
                        showDuplicateAadharDialog(req);

                }
            }
        });
    }

    void updatedSurveyDataToDB(String surveyId, AddSurveyRequest addSurveyRequest) {

        SurveyDataModal record = new SurveyDataModal();

        Log.v("PMAY", " updated record : "+ record.mAdharNo +" surveyId : "+surveyId);

        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            // slum
            if(addSurveyRequest.slumFamilyHeadName != null && !addSurveyRequest.slumFamilyHeadName.isEmpty()) {
                record.mName = addSurveyRequest.slumFamilyHeadName;
            }
            if (addSurveyRequest.slumAdharNo != null && !addSurveyRequest.slumAdharNo.isEmpty()) {
                record.mAdharNo = addSurveyRequest.slumAdharNo;
            }

            if(addSurveyRequest.slumFatherHusbandName != null && !addSurveyRequest.slumFatherHusbandName.isEmpty()) {
                record.mFatherName = addSurveyRequest.slumFatherHusbandName;
            }
            if(addSurveyRequest.chckSlumRadio != null)
                record.mSlum = addSurveyRequest.chckSlumRadio;
        }
        else {
            if(addSurveyRequest.familyHeadName != null && !addSurveyRequest.familyHeadName.isEmpty()) {
                record.mName = addSurveyRequest.familyHeadName;
            }
            if(addSurveyRequest.adharNo != null && !addSurveyRequest.adharNo.isEmpty())
                record.mAdharNo = addSurveyRequest.adharNo;

            if(addSurveyRequest.fatherHusbandName != null && !addSurveyRequest.fatherHusbandName.isEmpty()) {
                record.mFatherName = addSurveyRequest.fatherHusbandName;
            }
            if(addSurveyRequest.slumRadio != null)
                record.mSlum = addSurveyRequest.slumRadio;
        }


        if(surveyId != null)
            record.mSurveyId = surveyId;

        if(addSurveyRequest.bankAccNo != null) {
            record.mBankAccNo = addSurveyRequest.bankAccNo;
        }
        addSurveyRequest.surveyId = surveyId;

        if(updateDuplicateRecords(addSurveyRequest.adharNo, addSurveyRequest)) {
            mPmayDatabaseHelper.updateUser(addSurveyRequest, true);
        } else {
            //mPmayDatabaseHelper.addUser(addSurveyRequest);
        }
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            record.isSubmitted = addSurveyRequest.isSubmittedFlag;
        } else {
            record.isSubmitted = addSurveyRequest.isSubmitted;
        }
        Log.v("PMAY", "Record  surveyId: "+addSurveyRequest.surveyId  +", "+addSurveyRequest.slumAdharNo +" , "+addSurveyRequest.adharNo);
       // AddSurveyDataManager.getInstance().surveyDataModals.add(record);
        AddSurveyDataManager.getInstance().surveyDataModals = mPmayDatabaseHelper.getAllUser();
        refreshList();
    }

    boolean updateDuplicateRecords(String adhar, AddSurveyRequest addSurveyRequest) {
        boolean ret = false;
        List recordsList =  mPmayDatabaseHelper.getAllUser();
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            adhar = addSurveyRequest.slumAdharNo;
        } else {
            adhar = addSurveyRequest.adharNo;
        }
        Log.v("PMAY", " before Removed record by Adhar - size : "+recordsList.size() + " adhar : "+adhar);
        if(recordsList.size() > 0 && !recordsList.isEmpty()) {
            Iterator<SurveyDataModal> itr = recordsList.iterator();
            while (itr.hasNext()) {
                SurveyDataModal record = itr.next();
                Log.v("PMAY", " Removed records : old  "+ record.mAdharNo +" new "+addSurveyRequest.slumAdharNo +" "+addSurveyRequest.adharNo);
                if(record != null && record.mAdharNo != null && record.mAdharNo.trim().equals(adhar)) {
                    itr.remove();
                    Log.v("PMAY", " Removed record : "+record.mSurveyId);
                    //break;
                    ret = true;
                }
            }
        }
        Log.v("PMAY", " Removed record - size : "+recordsList.size());
        return ret;
    }

    void fetchOfflineImages(String adharNo) {
        // clear images
        AddSurveyDataManager.getInstance().slumBiometricDetails = null;
        AddSurveyDataManager.getInstance().applicantPhotoFile = null;
        AddSurveyDataManager.getInstance().presentHousePhotoFile = null;
        AddSurveyDataManager.getInstance().signaturePhotoFile = null;
        AddSurveyDataManager.getInstance().biometricDetails = null;

        AddSurveyDataManager.getInstance().applicantPhotoFile  = AddSurveyDataManager.getInstance()
                .getImageFileSaved(this, adharNo, AddSurveyDataManager.APPLICANT_PHTO);

        AddSurveyDataManager.getInstance().presentHousePhotoFile  = AddSurveyDataManager.getInstance()
                .getImageFileSaved(this, adharNo, AddSurveyDataManager.PRESENT_HOUSE_PHTO);
        AddSurveyDataManager.getInstance().signaturePhotoFile  = AddSurveyDataManager.getInstance()
                .getImageFileSaved(this, adharNo, AddSurveyDataManager.SIGNATURE_PHTO);
    }

    void deleteImagesForthisRecord() {
        if( AddSurveyDataManager.getInstance().applicantPhotoFile != null) {
            AddSurveyDataManager.getInstance().applicantPhotoFile.delete();
        }
        if( AddSurveyDataManager.getInstance().presentHousePhotoFile != null) {
            AddSurveyDataManager.getInstance().presentHousePhotoFile.delete();
        }
        if( AddSurveyDataManager.getInstance().signaturePhotoFile != null) {
            AddSurveyDataManager.getInstance().signaturePhotoFile.delete();
        }
    }

    byte [] getByteArray(File file) {
        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(bm != null) {
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            Base64.encodeToString(b, Base64.DEFAULT);
            return b;
        }
        return null;
    }

    void showDuplicateAadharDialog(final AddSurveyRequest req) {
        // check for either slum or non slum adhar
        Log.v("PMAY", "showDuplicateAadharDialog() For offline upload -  ");
        if(req.adharNo == null) {
            req.adharNo = req.slumAdharNo; // pick from Slum Adhar No
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        alertDialog.setTitle("Aadhar Exist !");
        alertDialog.setMessage("The Survey record with Aadhar Number <" + req.adharNo + "> already present, this record will be deleted before proceeding to upload other records.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                List recordsList = AddSurveyDataManager.getInstance().surveyDataModals;
                if(recordsList.size() > 0 && !recordsList.isEmpty()) {
                    Iterator<SurveyDataModal> itr = recordsList.iterator();
                    while (itr.hasNext()) {
                        SurveyDataModal surveyDataModal = itr.next();
                        if (surveyDataModal == null) {
                            continue;
                        }
                        if(surveyDataModal.mAdharNo.equals(req.adharNo) && "0".equals(surveyDataModal.mSurveyId)) {
                            // duplicate
                            Log.v("PMAY", "showDuplicateAadharDialog() For offline Remove duplicate  -  "+ req.adharNo);
                            mPmayDatabaseHelper.deleteOfflineSurveyByAadhar(req);
                            itr.remove();
                            Log.v("PMAY", "showDuplicateAadharDialog() For offline upload -  removed : "+surveyDataModal.mAdharNo);
                        }
                    }
                }
                //Start uploading remaining
                try{
                    // refresh list once
                    refreshList();
                    Thread.sleep(300);
                    uploadOfflineRecords();

                } catch (Exception e){
                    Log.v("PMAY", "showDuplicateAadharDialog() For offline Exception  -  "+ e.getMessage());
                }
            }
        });
        alertDialog.show();
    }

    void showLogoutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        //alertDialog.setIndeterminate(true);
        alertDialog.setTitle("Confirmation");
        alertDialog.setCancelable(true);
        alertDialog.setMessage(R.string.logout_message);
        /*if(isSuccess) {
            alertDialog.setTitle(R.string.confirm_title);
            alertDialog.setMessage(R.string.confirm_message);
        }*/
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onLogoutclick();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //onSignupSuccess(isSuccess);
            }
        });

        alertDialog.show();
    }
    ProgressDialog progressDialog;

    void onLogoutclick() {
        progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logout in progress ..");
        progressDialog.show();

        final Call<ResponseBody> call = apiInterface.logoutRequest();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(progressDialog != null) {
                    progressDialog.dismiss();
                }
                try {
                    if(response != null && response.isSuccessful()) {
                        if(response.body() != null ) {
                            String str = response.body().string().toString();
                            Log.v("PMAY", " Logout Response received   :  " +str.contains("successfully"));
                            if(str.contains("successfully")) {
                                SharedPreferences.Editor editor = defaultPref.edit();
                                editor.putBoolean("IsRememberMe", false);
                                editor.putString("USER_NAME", "");
                                editor.putString("PASSWORD", "");
                                editor.commit();
                                Log.v("PMAY", " Logout Response received   Finishing activity  " );
                                Toast.makeText(getApplicationContext(), "Successfully logged out  ", Toast.LENGTH_LONG).show();
                                finish();
                            } else  {
                                Toast.makeText(getApplicationContext(), "Failed to logout  ", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to logout  ", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Failed to logout  ", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void newSurvey(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("pos", 0);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //super.onPrepareOptionsMenu(menu);
        AddSurveyDataManager.getInstance().getRecordsCount(false);
        if(!defaultPref.getBoolean("logging", false)) {
            menu.findItem(R.id.action_menu_logging).setTitle("Enable Logs");
        } else {
            menu.findItem(R.id.action_menu_logging).setTitle("Disable Logs");
        }

        if(AddSurveyDataManager.getInstance().offlineRecords >0) {
            menu.findItem(R.id.action_upload_offline).setEnabled(true);
            return true;
        } else {
            menu.findItem(R.id.action_upload_offline).setEnabled(false);
            return  true;
        }
       //
    }

    boolean isWiFiDATAConnected() {
        Log.v("PMAY", "Menu  : isWiFiDATAConnected enter ");
        final ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        TelephonyManager telMgr = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);
        Log.v("PMAY", "Menu  : isWiFiDATAConnected enter , telMgr "+ telMgr);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Log.v("PMAY", " isWiFiDATAConnected()  "+telMgr.getNetworkType()  );
        if ((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected() && telMgr.getNetworkType() != TelephonyManager.NETWORK_TYPE_EDGE)) {
            return  true;
        }
        return false;
    }

    void showAboutMenuDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        alertDialog.setTitle("About");
        alertDialog.setCancelable(false);
        alertDialog.setMessage(" Application Version : "+BuildConfig.VERSION_NAME);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_new_survey) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("pos",0);
            startActivity(intent);
            return  true;
        } else if (item.getItemId() == R.id.action_logout) {
            showLogoutDialog();
            return true;
        } else if (item.getItemId() == R.id.action_about_menu) {
            showAboutMenuDialog();
            return true;
        }
          else if (item.getItemId() == R.id.action_sync) {
            if(!isWiFiDATAConnected()) {
                Toast.makeText(this, "Please check your internet connections.", Toast.LENGTH_LONG).show();
                return  true;
            }
            if(searchView != null) {
                searchView.setQuery("", false);
            }
            getAllSurveyDataFromServer();
            return true;
        } else if (item.getItemId() == R.id.action_upload_offline) {
            Log.v("PMAY", "Menu  : action_upload_offline enter ");
            if(!isWiFiDATAConnected()) {
                Toast.makeText(this, "Please check your internet connections before trying to upload offline Survey data.", Toast.LENGTH_LONG).show();
                return  true;
            }
            Log.v("PMAY", "Menu  : action_upload_offline enter  - show dialog");
            try {
                mProgressDialog = new ProgressDialog(this,
                        R.style.AppTheme_Dark_Dialog);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Uploading Offline Records ...");
                mProgressDialog.show();
                if (updatedList != null) {
                    updatedList.clear();
                }
                uploadOfflineRecords();
            } catch (Exception e) {
                Log.v("PMAY", "Menu  : action_upload_offline Exception : "+e.getMessage());
                Log.v("PMAY", "Menu  : action_upload_offline Exception : mProgressDialog "+mProgressDialog);
            }
            return true;
        } else if(item.getItemId() == R.id.action_menu_logging) {
            SharedPreferences.Editor editor = defaultPref.edit();
            if (!defaultPref.getBoolean("logging", false)) {
                editor.putBoolean("logging", true);
                editor.commit();
            } else {
                editor.putBoolean("logging", false);
                editor.commit();
            }
            Log.v("PMAY", "Menu  : action_menu_logging");
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}
