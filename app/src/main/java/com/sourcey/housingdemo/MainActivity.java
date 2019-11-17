package com.sourcey.housingdemo;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pm.yojana.housingdemo.R;
import com.sourcey.housingdemo.modal.PmayDatabaseHelper;
import com.sourcey.housingdemo.modal.SurveyDataModal;
import com.sourcey.housingdemo.restservice.APIClient;
import com.sourcey.housingdemo.restservice.APIInterface;
import com.sourcey.housingdemo.restservice.AddSurveyRequest;
import com.sourcey.housingdemo.restservice.AddSurveyResponse;
import com.sourcey.housingdemo.utils.CommonUtils;
import com.sourcey.housingdemo.utils.Constants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.util.Log;


public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST = 10;
    public static final int  GALLERY_REQUEST = 11;

    AppCompatCheckBox eligible_chk_box;
    RelativeLayout ll_eligible;
    RelativeLayout personal_info;
    RelativeLayout address_details;
    AppCompatButton submitBtn;
    AppCompatButton cancelbtn, btn_continue;
    // Head of the family
    EditText input_name, husband_name, phone;

    EditText person_name, dob;

    EditText reason_non_eligible, adhar_num, no_aadhar_reason;

    AppCompatCheckBox adharchkBox;
    AppCompatButton button_scan_aadhar;

    PermissionUtil permissionUtil;
    AddSurveyRequest addSurveyRequest;
    AddSurveyDataManager addSurveyDataManager;
    APIInterface apiInterface;
    RadioGroup radioGrp;

    EditText permanentAdd, city_parmanent, phone2, presentAddr, city_present, phone_present;
    AppCompatCheckBox isSameAdd, other_land_chkbox;
    TextInputLayout addrs_layout, city_layout, loc_layout, land_size_layout, slum_name_layout;

    EditText other_house, land_size;
    AppCompatButton other_house_proof, thumb_impressiion, btnApplicantPhoto, btnScanAadhar, btnScanFingerprint;

    EditText family_name, relation, age, id_no, ward_detail_info, slum_Name;
    AppCompatTextView biometricTitle;

    AppCompatSpinner casteSpinner, maritalSpinner, religionSpinner, genderSpinner, idProofSpinner,
            yearsOfStay, family_gender, ulb_dropdown;
    SharedPreferences defaultPref;
    Toolbar toolbar;

    PmayDatabaseHelper mPmayDatabaseHelper;

    int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);*/
        permissionUtil = new PermissionUtil();
        eligible_chk_box = (AppCompatCheckBox) findViewById(R.id.eligibility);
        radioGrp = (RadioGroup) findViewById(R.id.radio_grp);

        defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        AddSurveyDataManager.getInstance().ctx = this;
        mPmayDatabaseHelper = new PmayDatabaseHelper(this);
        //mPmayDatabaseHelper.getWritableDatabase();

        ll_eligible = (RelativeLayout) findViewById(R.id.rel_ll);
        submitBtn = (AppCompatButton)findViewById(R.id.submit);
        cancelbtn = (AppCompatButton)findViewById(R.id.cancel);
        btn_continue = (AppCompatButton)findViewById(R.id.sub_continue);

        biometricTitle = (AppCompatTextView)findViewById(R.id.biometirc_info_title);

        thumb_impressiion = (AppCompatButton)findViewById(R.id.thumb_impressiion);
       // personal_info = (RelativeLayout) findViewById(R.id.personal_info);
       // address_details = (RelativeLayout) findViewById(R.id.address_details);
        input_name = (EditText)findViewById(R.id.input_name);
        husband_name = (EditText)findViewById(R.id.husband_name);

        slum_Name = (EditText)findViewById(R.id.slum_name_edit);
        slum_name_layout = (TextInputLayout) findViewById(R.id.slum_name_layout);


        casteSpinner = (AppCompatSpinner)findViewById(R.id.caste_dropDown);
        religionSpinner = (AppCompatSpinner)findViewById(R.id.religion_dropDown);
        idProofSpinner = (AppCompatSpinner)findViewById(R.id.idproof_dropdown);
        ulb_dropdown = (AppCompatSpinner)findViewById(R.id.ulb_dropdown);
        maritalSpinner = (AppCompatSpinner)findViewById(R.id.marital_state);
        genderSpinner = (AppCompatSpinner)findViewById(R.id.gender_dropDown);
        yearsOfStay =  (AppCompatSpinner)findViewById(R.id.yearsOfStay);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pos = getIntent().getIntExtra("pos", -1);
        int listItempos = getIntent().getIntExtra("ListItempos", -1);
        addSurveyRequest = new AddSurveyRequest();
        //AddSurveyDataManager.getInstance().mAddSurveyRequest = mAddSurveyEditRecord;
        btnApplicantPhoto = (AppCompatButton)findViewById(R.id.photo);
        btnScanAadhar = (AppCompatButton)findViewById(R.id.button_scan_aadhar);
        btnScanFingerprint = (AppCompatButton)findViewById(R.id.thumb_impressiion);

        String userId = AddSurveyDataManager.getInstance().mAddSurveyRequest.userId;
        AddSurveyDataManager.getInstance().mAddSurveyRequest = addSurveyRequest; // reset all values
        if(userId != null) {
            AddSurveyDataManager.getInstance().mAddSurveyRequest.userId = userId;
        } else {
            AddSurveyDataManager.getInstance().mAddSurveyRequest.userId = defaultPref.getString("USER_ID", null);
        }
        AddSurveyDataManager.getInstance().slumBiometricDetails = null;
        AddSurveyDataManager.getInstance().applicantPhotoFile = null;
        AddSurveyDataManager.getInstance().presentHousePhotoFile = null;
        AddSurveyDataManager.getInstance().signaturePhotoFile = null;
        AddSurveyDataManager.getInstance().biometricDetails = null;
        AddSurveyDataManager.getInstance().eligibleSaved = false;
        AddSurveyDataManager.getInstance().missionSpinnerSaved =0;
        AddSurveyDataManager.getInstance().eligibleReasonSaved = null;
        AddSurveyDataManager.getInstance().mAttachments.clear();
        AddSurveyDataManager.mFieldscount = 0;

        AddSurveyDataManager.getInstance().mBitmapApplicantOffline = null;
        AddSurveyDataManager.getInstance().mBitmapHousePicOffline = null;
        AddSurveyDataManager.getInstance().mBitmapFingerPrintOffline = null;

        if(listItempos != -1) {
            getSelectedSurveyDataItem(listItempos);
            if(mAddSurveyEditRecord != null && mAddSurveyEditRecord.surveyId != null && ! "0".equals(mAddSurveyEditRecord.surveyId)) {
                getSupportActionBar().setTitle("Edit Survey -  "+mAddSurveyEditRecord.surveyId);
            } else {
                getSupportActionBar().setTitle("Edit Survey");
            }

            if(pos == 0) {
                radioGrp.check(R.id.btn_slum);
                ((RadioButton)radioGrp.getChildAt(1)).setEnabled(false);
                AddSurveyDataManager.getInstance().mAddSurveyRequest.bplRadio = "N";
                AddSurveyDataManager.getInstance().mAddSurveyRequest.rationRadio = "N";
            } else {
                radioGrp.check(R.id.btn_non_slum);
                ((RadioButton)radioGrp.getChildAt(0)).setEnabled(false);
            }
            radioGrp.setSaveEnabled(true);
            radioGrp.setEnabled(false);

        } else {
            addSurveyRequest.surveyId = "0"; // Add new
            addSurveyRequest.isNewRecord = true;
            if(mAddSurveyEditRecord != null) {
                mAddSurveyEditRecord.isNewRecord = true;
            }
            if(pos == 0) {
                getSupportActionBar().setTitle("New Survey");
                radioGrp.check(R.id.btn_slum);
                AddSurveyDataManager.getInstance().mAddSurveyRequest.chckSlumRadio = "S";
            }
            else  {
                getSupportActionBar().setTitle("New Survey");
                radioGrp.check(R.id.btn_non_slum);
                AddSurveyDataManager.getInstance().mAddSurveyRequest.slumRadio = "N";
            }
        }

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        addSurveyDataManager = AddSurveyDataManager.getInstance();
        initSpinnerItemsListener();
        //addSurveyRequest = new AddSurveyRequest();
        apiInterface = APIClient.getClient().create(APIInterface.class);

        dob = (EditText)findViewById(R.id.dob_1);
        //dob.requestFocusFromTouch();
        dob.setClickable(true);

        phone = (EditText)findViewById(R.id.phone);
        reason_non_eligible = (EditText)findViewById(R.id.reason_non_eligible);
        adharchkBox = (AppCompatCheckBox)findViewById(R.id.adhar);
        no_aadhar_reason = (EditText)findViewById(R.id.no_aadhar_reason);

        button_scan_aadhar = (AppCompatButton)findViewById(R.id.button_scan_aadhar);
        adhar_num = (EditText)findViewById(R.id.adhar_num);
        adhar_num.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});

        //Address
        isSameAdd = (AppCompatCheckBox) findViewById(R.id.same_address_chkbox);

        permanentAdd = (EditText) findViewById(R.id.permanent_address);
        city_parmanent = (EditText) findViewById(R.id.city_parmanent);
        phone2 = (EditText) findViewById(R.id.phone2);

        addrs_layout = (TextInputLayout) findViewById(R.id.addrs_layout);
        city_layout = (TextInputLayout) findViewById(R.id.city_layout);

        presentAddr = (EditText) findViewById(R.id.present_address);
        city_present = (EditText) findViewById(R.id.city_present);
        phone_present = (EditText) findViewById(R.id.phone_present);

        ward_detail_info = (EditText) findViewById(R.id.ward_detail_info);

        // family
        family_name = (EditText) findViewById(R.id.family_name);
        relation = (EditText) findViewById(R.id.relation);
        age = (EditText) findViewById(R.id.age);
        id_no = (EditText) findViewById(R.id.id_no);
        family_gender = (AppCompatSpinner)findViewById(R.id.family_gender);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.pmay.FINISH_ACTIVITY");
        registerReceiver(receiver, filter);

        Log.v("PMAY", " MainActivity: onCreate() called" );

        land_size_layout = (TextInputLayout) findViewById(R.id.land_size_layout);
        other_house = (EditText) findViewById(R.id.other_house);
        land_size = (EditText) findViewById(R.id.land_size);
        other_house_proof = (AppCompatButton) findViewById(R.id.other_house_proof);
        loc_layout = (TextInputLayout) findViewById(R.id.loc_layout);

        other_land_chkbox = (AppCompatCheckBox) findViewById(R.id.other_land_chkbox);
        other_land_chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    other_house.setVisibility(View.VISIBLE);
                    loc_layout.setVisibility(View.VISIBLE);
                    land_size.setVisibility(View.VISIBLE);
                    land_size_layout.setVisibility(View.VISIBLE);
                    other_house_proof.setVisibility(View.VISIBLE);
                } else {
                    other_house.setVisibility(View.GONE);
                    loc_layout.setVisibility(View.GONE);
                    land_size.setVisibility(View.GONE);
                    land_size_layout.setVisibility(View.GONE);
                    other_house_proof.setVisibility(View.GONE);
                }
            }
        });

        isSameAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    permanentAdd.setText(presentAddr.getText().toString());
                    permanentAdd.setEnabled(false);
                    permanentAdd.setVisibility(View.GONE);
                    city_parmanent.setVisibility(View.GONE);
                    city_layout.setVisibility(View.GONE);
                    addrs_layout.setVisibility(View.GONE);
                    phone2.setVisibility(View.GONE);
                } else {
                    permanentAdd.setText("");
                    permanentAdd.setEnabled(true);
                    city_layout.setVisibility(View.VISIBLE);
                    addrs_layout.setVisibility(View.VISIBLE);
                    permanentAdd.setVisibility(View.VISIBLE);
                    city_parmanent.setVisibility(View.VISIBLE);
                    phone2.setVisibility(View.VISIBLE);
                }
            }
        });

        adharchkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    button_scan_aadhar.setVisibility(View.VISIBLE);
                    adhar_num.setVisibility(View.VISIBLE);
                    no_aadhar_reason.setVisibility(View.GONE);
                } else {
                    button_scan_aadhar.setVisibility(View.GONE);
                    adhar_num.setVisibility(View.GONE);
                    no_aadhar_reason.setVisibility(View.VISIBLE);
                }
            }
        });

        if(pos == 0) { //slum
            submitBtn.setVisibility(View.VISIBLE); // save
            btn_continue.setVisibility(View.GONE);
            cancelbtn.setVisibility(View.VISIBLE); // submit
            thumb_impressiion.setVisibility(View.VISIBLE);
            biometricTitle.setVisibility(View.VISIBLE);
            slum_name_layout.setVisibility(View.VISIBLE);
            slum_Name.setVisibility(View.VISIBLE);
            eligible_chk_box.setVisibility(View.VISIBLE);
            reason_non_eligible.setVisibility(View.VISIBLE);
        } else {
            btn_continue.setVisibility(View.VISIBLE);
            cancelbtn.setVisibility(View.GONE);
            submitBtn.setVisibility(View.VISIBLE);
            thumb_impressiion.setVisibility(View.GONE);
            biometricTitle.setVisibility(View.GONE);
            eligible_chk_box.setVisibility(View.GONE);
            reason_non_eligible.setVisibility(View.GONE);
            slum_name_layout.setVisibility(View.GONE);
            slum_Name.setVisibility(View.GONE);
        }
        radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.v("PMAY", "id " +checkedId );
                if(checkedId == R.id.btn_slum) {
                    submitBtn.setVisibility(View.VISIBLE); // save
                    btn_continue.setVisibility(View.GONE);
                    cancelbtn.setVisibility(View.VISIBLE); // submit
                    thumb_impressiion.setVisibility(View.VISIBLE);
                    biometricTitle.setVisibility(View.VISIBLE);
                    slum_name_layout.setVisibility(View.VISIBLE);
                    slum_Name.setVisibility(View.VISIBLE);
                    eligible_chk_box.setVisibility(View.VISIBLE);
                    reason_non_eligible.setVisibility(View.VISIBLE);
                    AddSurveyDataManager.getInstance().mAddSurveyRequest.chckSlumRadio = "S";
                } else {
                    btn_continue.setVisibility(View.VISIBLE);
                    cancelbtn.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.VISIBLE);
                    thumb_impressiion.setVisibility(View.GONE);
                    biometricTitle.setVisibility(View.GONE);
                    eligible_chk_box.setVisibility(View.GONE);
                    reason_non_eligible.setVisibility(View.GONE);
                    slum_name_layout.setVisibility(View.GONE);
                    slum_Name.setVisibility(View.GONE);
                    AddSurveyDataManager.getInstance().mAddSurveyRequest.slumRadio = "N";
                }
            }
        });

        eligible_chk_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    reason_non_eligible.setVisibility(View.GONE);


                    /*btn_continue.setVisibility(View.VISIBLE);
                    address_details.setVisibility(View.GONE);*/
                } else {
                    reason_non_eligible.setVisibility(View.VISIBLE);
                    /*personal_info.setVisibility(View.GONE);
                    address_details.setVisibility(View.GONE);*/
                }
            }
        });
        showRecordToEdit();
        setDatePickerDialog();
        //checkGPSOption(); //BISW27Mar18: Needs to ask user to enable GPS all the time
    }
    DatePickerDialog datePickerDialog;
    void setDatePickerDialog() {
        TextInputLayout date_layout;
        date_layout = (TextInputLayout)findViewById(R.id.date_layout);
        date_layout.setClickable(true);
        if(dob.getText().length() ==0) {
            dob.setText("  ");
        }
        dob.setFocusable(false);
        dob.setFocusableInTouchMode(false);
        dob.setShowSoftInputOnFocus(false);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.ThemeOverlay_AppCompat_Dialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                dob.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
         finish();
        }
    };

    void showRecordToEdit() {

        if(mAddSurveyEditRecord != null && !mAddSurveyEditRecord.isNewRecord) {
            Log.v("PMAY", "Edit record "+ !mAddSurveyEditRecord.isNewRecord +" " +" "+mAddSurveyEditRecord.isSubmitted);
            if("O".equals(addSurveyRequest.isSubmitted) && addSurveyRequest.surveyId.equals("0")) {
                //offline record
                AddSurveyDataManager.getInstance().applicantPhotoFile  = AddSurveyDataManager.getInstance()
                        .getImageFileSaved(this, addSurveyRequest.adharNo, AddSurveyDataManager.APPLICANT_PHTO);

                AddSurveyDataManager.getInstance().presentHousePhotoFile  = AddSurveyDataManager.getInstance()
                        .getImageFileSaved(this, addSurveyRequest.adharNo, AddSurveyDataManager.PRESENT_HOUSE_PHTO);
                AddSurveyDataManager.getInstance().signaturePhotoFile  = AddSurveyDataManager.getInstance()
                        .getImageFileSaved(this, addSurveyRequest.adharNo, AddSurveyDataManager.SIGNATURE_PHTO);

                if(addSurveyRequest.bplNo != null) {
                    addSurveyRequest.geoLatitude = addSurveyRequest.bplNo;
                }
                if(addSurveyRequest.rationCardNo != null) {
                    addSurveyRequest.geoLongitude = addSurveyRequest.rationCardNo;
                }
            } else {
                if(addSurveyRequest.bplNo != null) {
                    addSurveyRequest.geoLatitude = addSurveyRequest.bplNo;
                   // addSurveyRequest.bplNo = null;
                }
                if(addSurveyRequest.rationCardNo != null) {
                    addSurveyRequest.geoLongitude = addSurveyRequest.rationCardNo;
                    //addSurveyRequest.rationCardNo = null;
                }
            }

            if ( mAddSurveyEditRecord.preferredAssistanceHfa != null) { //BISW28Mar18:
                    addSurveyDataManager.missionSpinnerSaved = Integer.parseInt(mAddSurveyEditRecord.preferredAssistanceHfa);
            }

           if(mAddSurveyEditRecord.nonEligibleReason != null) {
            reason_non_eligible.setText(mAddSurveyEditRecord.nonEligibleReason);
            eligible_chk_box.setChecked(false);
           }
            if(mAddSurveyEditRecord.wardDetails != null) {
               ward_detail_info.setText(mAddSurveyEditRecord.wardDetails);
            }

            if(mAddSurveyEditRecord.familyHeadName != null) {
                input_name.setText(mAddSurveyEditRecord.familyHeadName);
            }
            if(mAddSurveyEditRecord.fatherHusbandName != null) {
               husband_name.setText(mAddSurveyEditRecord.fatherHusbandName);
            }
            if(mAddSurveyEditRecord. adharNo!= null) {
               adhar_num.setText(mAddSurveyEditRecord. adharNo);
            }
            if(mAddSurveyEditRecord.idType != null) {
              idProofSpinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.idType));
            }
            if(mAddSurveyEditRecord.ulbNameId != null) {
                ulb_dropdown.setSelection(Integer.parseInt(mAddSurveyEditRecord.ulbNameId));
            }

            if(mAddSurveyEditRecord.genderId != null) {
                genderSpinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.genderId));
            }
            if(mAddSurveyEditRecord. dob!= null) {
                dob.setText(mAddSurveyEditRecord.dob);
            }

            if(mAddSurveyEditRecord.maritalStatus != null) {
                maritalSpinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.maritalStatus));
            }
            if(mAddSurveyEditRecord.religion != null) {
                religionSpinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.religion));
            }
            if(mAddSurveyEditRecord.caste != null) {
                casteSpinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.caste));
            }

            if(mAddSurveyEditRecord. presentMobileNo!= null) {
                phone_present.setText(mAddSurveyEditRecord.presentMobileNo);
            }
            if(mAddSurveyEditRecord.presentHouseNo!= null && mAddSurveyEditRecord.presentStreetName != null) {
                presentAddr.setText(mAddSurveyEditRecord. presentHouseNo);
            }

            if(mAddSurveyEditRecord. familyMemberName!= null) {
                family_name.setText(mAddSurveyEditRecord. familyMemberName);
            }

            if(mAddSurveyEditRecord. familyMemberAge!= null) {
                age.setText(mAddSurveyEditRecord. familyMemberAge);
            }
            if(mAddSurveyEditRecord. familyMemberIdCardNo!= null) {
                id_no.setText(mAddSurveyEditRecord. familyMemberIdCardNo);
            }
            if(mAddSurveyEditRecord.familyMemberGender != null) {
                family_gender.setSelection(Integer.parseInt(mAddSurveyEditRecord.familyMemberGender));
            }
            if(mAddSurveyEditRecord.familyMemberRelation != null) {
                //rel.setSelection(Integer.parseInt(mAddSurveyEditRecord.familyMemberRelation));
            }

            Log.v(" PMAY ", " eligible edit "+mAddSurveyEditRecord.eligibleStatus + " , "+mAddSurveyEditRecord.chckSlumRadio);

            if(radioGrp.getCheckedRadioButtonId() == R.id.btn_slum) {
                if(mAddSurveyEditRecord.slumPresentStreetName != null) {
                    slum_Name.setText(mAddSurveyEditRecord.slumPresentStreetName);
                }
                if("Y".equals(mAddSurveyEditRecord.eligibleStatus)) {
                    eligible_chk_box.setChecked(true);
                    Log.v(" PMAY ", " eligible edit "+eligible_chk_box.isChecked());
                    //slum_Name.setText(mAddSurveyEditRecord.slumPresentStreetName);
                } else {
                    eligible_chk_box.setChecked(false);
                    if(mAddSurveyEditRecord.nonEligibleReason !=null )
                    no_aadhar_reason.setText((mAddSurveyEditRecord.nonEligibleReason));
                }

            }
            if("Y".equals(mAddSurveyEditRecord.ownsRadio)) {
                other_land_chkbox.setChecked(true);
                if(mAddSurveyEditRecord.landLocation != null) {
                  other_house.setText(mAddSurveyEditRecord.landLocation);
                }
                if(mAddSurveyEditRecord.landinSqm != null) {
                    land_size.setText(mAddSurveyEditRecord.landinSqm);
                }
            } else {
                other_land_chkbox.setChecked(false);
            }

        }
    }

    AddSurveyRequest mAddSurveyEditRecord;

    void getSelectedSurveyDataItem(int listItempos) {
        if(AddSurveyDataManager.getInstance().surveyDataModals.size() > listItempos) {
            //cheeck if it is from search list
            SurveyDataModal  data = null;
            int dispListSize = AddSurveyDataManager.getInstance().SurveyDataListToDisplay.size();
            if (dispListSize >0 && dispListSize < AddSurveyDataManager.getInstance().surveyDataModals.size()
                    && listItempos < dispListSize) {
                data = AddSurveyDataManager.getInstance().SurveyDataListToDisplay.get(listItempos);
            } else {
                data = AddSurveyDataManager.getInstance().surveyDataModals.get(listItempos);
            }

            if(data != null && data.mSurveyId != null) {
               PmayDatabaseHelper helper =  new PmayDatabaseHelper(this);
                Log.v("PMAY", "getSurveyDataById -  "+data.mAdharNo);
               mAddSurveyEditRecord = helper.getSurveyDataById(data.mSurveyId, data.mAdharNo);
                Log.v("PMAY", "getSurveyDataById - success  ");
               addSurveyRequest.isNewRecord = false;
               mAddSurveyEditRecord.isNewRecord = false;
              if("Y".equals(mAddSurveyEditRecord.isSubmitted)) {
                  cancelbtn.setEnabled(false);
                  cancelbtn.setTextColor(getResources().getColor(R.color.iron));
                  submitBtn.setEnabled(false);
                  submitBtn.setTextColor(getResources().getColor(R.color.iron));

                  btnApplicantPhoto.setEnabled(false);
                  btnApplicantPhoto.setTextColor(getResources().getColor(R.color.iron));
                  btnScanAadhar.setEnabled(false);
                  btnScanAadhar.setTextColor(getResources().getColor(R.color.iron));
                  btnScanFingerprint.setEnabled(false);
                  btnScanFingerprint.setTextColor(getResources().getColor(R.color.iron));

              } else {
                  cancelbtn.setEnabled(true);
                  submitBtn.setEnabled(true);
                  submitBtn.setTextColor(getResources().getColor(R.color.white));
                  cancelbtn.setTextColor(getResources().getColor(R.color.white));
                  btnApplicantPhoto.setEnabled(true);//BISW24Mar18:
                  btnScanAadhar.setEnabled(true);//BISW24Mar18:
                  btnScanFingerprint.setEnabled(true);//BISW24Mar18:
                  btnApplicantPhoto.setTextColor(getResources().getColor(R.color.white));//BISW24Mar18:
                  btnScanAadhar.setTextColor(getResources().getColor(R.color.white));//BISW24Mar18:
                  btnScanFingerprint.setTextColor(getResources().getColor(R.color.white));//BISW24Mar18:
              }
                Log.v("PMAY", " list item selected : " + data.mSurveyId + " submitted "+mAddSurveyEditRecord.isSubmitted);
            }
        }

    }

    boolean isSpinnerIemValid(int pos) {
        if(pos == 0) {
            //Toast.makeText(this, "Please select valid option", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }

    void initSpinnerItemsListener() {

        String[] id_proof_arrays =  getResources().getStringArray(R.array.id_proof_arrays);
        String[] caste_arrays =  getResources().getStringArray(R.array.caste_arrays);
        String[] religion_arrays =  getResources().getStringArray(R.array.religion_arrays);
        String[] marital_arrays =  getResources().getStringArray(R.array.marital_arrays);
        String[] gender_arrays =  getResources().getStringArray(R.array.gender_arrays);

        casteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerIemValid(position)) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerIemValid(position)) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        idProofSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerIemValid(position)) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        religionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerIemValid(position)) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        maritalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerIemValid(position)) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    String mCurrentPhotoPath;

    private File createFile() throws IOException {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile("attachment_house", ".jpg", storageDir);
        mCurrentPhotoPath = image.getPath();
        return image;

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("PMAY", " MainActivity: onStart() called");
    }

    void launchCamera(String filename) {
        Toast.makeText(this, "Applicant Photo in front of present house ", Toast.LENGTH_SHORT).show();
        File photoFile = null;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = createFile();
        } catch (Exception e) {
            Log.v("PMAY", " exception in createFiel() " );
            e.printStackTrace();
        }
        if(photoFile == null)
            return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        } else {
            File imagePath = new File(getApplicationContext().getFilesDir(), "images");
            File newFile = new File(imagePath, filename);
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.sourcey.materialloginexample.provider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            Log.v("PMAY", " set URI in camera intent " +photoUri.getPath());
        }
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    void openGallery(int requestcode){
        Log.v("PMAY", "Open gallery");
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Attachment");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        startActivityForResult(pickIntent, requestcode);
    }

    public void otherHouseAttach(View v) {
        Toast.makeText(getBaseContext(), "Attachment for Other land or house  ", Toast.LENGTH_SHORT).show();
        permissionUtil.showAttachmentChooserDialog(this, attachListener, AddSurveyDataManager.LAND_RECORD_PHTO);
    }
    public AttachmentSelectionListener attachListener = new AttachmentSelectionListener() {
        @Override
        public void onCameraItemClick(int result, String attachmentName) {
            int requestCode = 0;
            if(attachmentName.equals(AddSurveyDataManager.PRESENT_HOUSE_PHTO)) {
                requestCode = PermissionUtil.CAMERA_REQUEST_PRESENT_HOUSE;
            } else if (attachmentName.equals(AddSurveyDataManager.LAND_RECORD_PHTO)) {
                requestCode = PermissionUtil.CAMERA_REQUEST_OTHER_LAND;
            } else if (attachmentName.equals(AddSurveyDataManager.LAND1_PHTO)) {
                requestCode = PermissionUtil.CAMERA_REQUEST_LAND1;
            } else if (attachmentName.equals(AddSurveyDataManager.LAND2_PHTO)) {
                requestCode = PermissionUtil.CAMERA_REQUEST_LAND2;
            } else if (attachmentName.equals(AddSurveyDataManager.INCOME_PHTO)) {
                requestCode = PermissionUtil.CAMERA_REQUEST_INCOME;
            } else if (attachmentName.equals(AddSurveyDataManager.BPL_PHTO)) {
                requestCode = PermissionUtil.CAMERA_REQUEST_BPL;
            }  else if (attachmentName.equals(AddSurveyDataManager.RATIONCD_PHTO)) {
                requestCode = PermissionUtil.CAMERA_REQUEST_RATION;
            }  else if (attachmentName.equals(AddSurveyDataManager.ID_PHTO)) {
                requestCode = PermissionUtil.CAMERA_REQUEST_IDPROOF;
            }
            if(result == PermissionUtil.OPEN_CAMERA) {
            launchCameraIntent(requestCode);
            } else if(result == PermissionUtil.CAM_PERMISSION) {
                ActivityCompat.requestPermissions(MainActivity.this, permissionUtil.getCameraPermissions(), requestCode);
            }
        }


        @Override
        public void onGalleryItemClick(int result, String attachmentName) {
            int requestCode = 0;
            if(attachmentName.equals(AddSurveyDataManager.PRESENT_HOUSE_PHTO)) {
                requestCode = PermissionUtil.GALLERY_REQUEST_PRESENT_HOUSE;
            } else if (attachmentName.equals(AddSurveyDataManager.LAND_RECORD_PHTO)) {
                requestCode = PermissionUtil.GALLERY_REQUEST_OTHER_LAND;
            } else if (attachmentName.equals(AddSurveyDataManager.LAND1_PHTO)) {
                requestCode = PermissionUtil.GALLERY_REQUEST_LAND1;
            } else if (attachmentName.equals(AddSurveyDataManager.LAND2_PHTO)) {
                requestCode = PermissionUtil.GALLERY_REQUEST_LAND2;
            } else if (attachmentName.equals(AddSurveyDataManager.INCOME_PHTO)) {
                requestCode = PermissionUtil.GALLERY_REQUEST_INCOME;
            } else if (attachmentName.equals(AddSurveyDataManager.BPL_PHTO)) {
                requestCode = PermissionUtil.GALLERY_REQUEST_BPL;
            }  else if (attachmentName.equals(AddSurveyDataManager.RATIONCD_PHTO)) {
                requestCode = PermissionUtil.GALLERY_REQUEST_RATION;
            }  else if (attachmentName.equals(AddSurveyDataManager.ID_PHTO)) {
                requestCode = PermissionUtil.GALLERY_REQUEST_IDPROOF;
            }
            if(result == PermissionUtil.OPEN_GALLERY) {
                openGallery(requestCode);
            } else if(result == PermissionUtil.GAL_PERMISSION) {
                ActivityCompat.requestPermissions(MainActivity.this, permissionUtil.getGalleryPermissions(), requestCode);
            }

        }
    };

    public void resetAllFields() {
        reason_non_eligible.setText("");
        //eligible_chk_box.setChecked(false);
        input_name.setText("");
        phone.setText("");
        husband_name.setText("");
        adhar_num.setText("");
        phone.setText("");
        dob.setText("");
        presentAddr.setText("");
        city_present.setText("");
        phone.setText("");
        ward_detail_info.setText("");
        permanentAdd.setText("");
        city_parmanent.setText("");
        phone2.setText("");
        family_name.setText("");
        age.setText("");
        id_no.setText("");
        other_house.setText("");
        land_size.setText("");
        adharchkBox.setChecked(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getBaseContext(), "Login Success ", Toast.LENGTH_LONG).show();
        checkGPSOption(); //BISW27Mar18
    }

    public void addMore(View v) {
        if (getStringFromEditText(family_name).length() >1 && family_gender.getSelectedItemPosition() > 0
                && getStringFromEditText(age).length() >0 && getStringFromEditText(id_no).length() > 1) {
            Toast.makeText(this, "Please enter additional family member details", Toast.LENGTH_SHORT).show();
            addSurveyRequest = AddSurveyDataManager.getInstance().mAddSurveyRequest;
            if(getStringFromEditText(family_name).length() >1) {
                addSurveyRequest.familyMemberName = getStringFromEditText(family_name);
                family_name.setText("");
            }
            if(getStringFromEditText(age).length() >1) {
                addSurveyRequest.familyMemberIdCardNo = getStringFromEditText(id_no);
                age.setText("");
            }
            if(getStringFromEditText(id_no).length() >1) {
                addSurveyRequest.familyMemberAge = getStringFromEditText(id_no);
                id_no.setText("");
            }

        } else {
            Toast.makeText(this, "Please enter all fields before adding more", Toast.LENGTH_SHORT).show();
        }
    }

    String getStringFromEditText(EditText text) {
        if(text != null ){
            return text.getText().toString().trim();
        }
        return "";
    }

    AppCompatSpinner relationSpinner;

    boolean validateRequestFileds(boolean isSubmit ) {
        int count=0;
        boolean isValid = false;

        String[] id_proof_arrays =  getResources().getStringArray(R.array.id_proof_arrays);
        String[] caste_arrays =  getResources().getStringArray(R.array.caste_arrays);
        String[] religion_arrays =  getResources().getStringArray(R.array.religion_arrays);
        String[] marital_arrays =  getResources().getStringArray(R.array.marital_arrays);
        String[] gender_arrays =  getResources().getStringArray(R.array.gender_arrays);

        String[] ulb_arrays =  getResources().getStringArray(R.array.ulb_names_array);

        String[] yrs_of_stay =  getResources().getStringArray(R.array.years_stay_arrays);

        String hodFamily = getStringFromEditText(input_name);
        String husbandName = getStringFromEditText(husband_name);
        String adharNo = getStringFromEditText(adhar_num);
        String contactNo = getStringFromEditText(phone);
        String dOb1 = getStringFromEditText(dob);
        String NonEligibleReason = getStringFromEditText(reason_non_eligible);

        String preAdd = getStringFromEditText(presentAddr);
        String precity = getStringFromEditText(city_present);
        String precontact = getStringFromEditText(phone_present);

        String permAdd = getStringFromEditText(permanentAdd);
        String permcity = getStringFromEditText(city_parmanent);
        String town = getStringFromEditText(phone2);

        String fName = getStringFromEditText(family_name);
        relationSpinner = (AppCompatSpinner) findViewById(R.id.family_relation);
        String fAge = getStringFromEditText(age);
        String fId = getStringFromEditText(id_no);

        //Log.v("PMAY", " list item selected : "+mAddSurveyEditRecord.preferredAssistanceHfa);
        addSurveyRequest = AddSurveyDataManager.getInstance().mAddSurveyRequest;
        if(mAddSurveyEditRecord != null && !mAddSurveyEditRecord.isNewRecord) {
            Log.v("PMAY", " list item selected : "+mAddSurveyEditRecord.preferredAssistanceHfa +" , edit "+mAddSurveyEditRecord.preferredAssistanceHfa);
            addSurveyRequest =   mAddSurveyEditRecord;
            addSurveyRequest.isNewRecord = false;
        }
        if(radioGrp.getCheckedRadioButtonId() == R.id.btn_slum) {
            addSurveyRequest.chckSlumRadio = "S";
            addSurveyRequest.slumRadio = null;
        } else {
            addSurveyRequest.chckSlumRadio = null;
            addSurveyRequest.slumRadio = "N";
        }

        if("N".equals(addSurveyRequest.slumRadio)) {
            addSurveyRequest.bplRadio = "N";
            addSurveyRequest.rationRadio = "N";
        }

        if("S".equals(addSurveyRequest.chckSlumRadio)) {            // NA

            addSurveyRequest.bplRadio = "N";
            addSurveyRequest.rationRadio = "N";
        }

        //addSurveyRequest.formNo = "12";
       // addSurveyRequest.userId = "5";

        // Family details
        if (ulb_dropdown.getSelectedItemPosition() >0) {
            addSurveyRequest.ulbNameId = Integer.toString(ulb_dropdown.getSelectedItemPosition());
            count++;
        }
        if (family_gender.getSelectedItemPosition() >0) {
            addSurveyRequest.familyMemberGender = Integer.toString(family_gender.getSelectedItemPosition());
            count++;
        }/* else if (family_gender.getSelectedItemPosition() == 2) {
            addSurveyRequest.familyMemberGender = "F";
            count++;
        } else if (family_gender.getSelectedItemPosition() == 3) {
            addSurveyRequest.familyMemberGender = "T";
            count++;
        }*/
        if(fName.length() >1) {
            addSurveyRequest.familyMemberName = fName;
            count++;
        }
        if(relationSpinner.getSelectedItemPosition() > 0) {
            addSurveyRequest.familyMemberRelation = Integer.toString(relationSpinner.getSelectedItemPosition());
            count++;
        }
        if(fAge.length() >1) {
            addSurveyRequest.familyMemberAge = fAge;
            count++;
        }
        if(fId.length() >1) {
            addSurveyRequest.familyMemberIdCardNo = fId;
            count++;
        }

        if(getStringFromEditText(ward_detail_info).length() > 0) {
            addSurveyRequest.wardDetails = getStringFromEditText(ward_detail_info);
            count++;
        }
        if(addSurveyRequest.eligibleStatus == null) {

        }
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            isValid = true;

            if(eligible_chk_box.isChecked()){
                addSurveyRequest.eligibleStatus = "Y";
                addSurveyRequest.nonEligibleReason = null;
                count++;
            } else {
                if(NonEligibleReason.length() >0 ) {
                    addSurveyRequest.nonEligibleReason = NonEligibleReason;
                    count++;
                }
                addSurveyRequest.eligibleStatus = "N";
                count++;
            }

            if(casteSpinner.getSelectedItemPosition() > 0) {
                addSurveyRequest.slumCaste = Integer.toString(casteSpinner.getSelectedItemPosition());
                count++;
            }
            if(idProofSpinner.getSelectedItemPosition() > 0) {
                addSurveyRequest.idType = Integer.toString(idProofSpinner.getSelectedItemPosition());
                count++;
            }
            if(religionSpinner.getSelectedItemPosition() > 0) {
                addSurveyRequest.slumReligion = Integer.toString(religionSpinner.getSelectedItemPosition());
                count++;
            }
            if(maritalSpinner.getSelectedItemPosition() > 0) {
                addSurveyRequest.maritalStatus = Integer.toString(maritalSpinner.getSelectedItemPosition());
                count++;
            }
            if (genderSpinner.getSelectedItemPosition() >0) {
                addSurveyRequest.genderValue = Integer.toString(genderSpinner.getSelectedItemPosition());
                count++;
            }/* else if (genderSpinner.getSelectedItemPosition() == 2) {
            addSurveyRequest.genderId = "F";
            count++;
        } else if (genderSpinner.getSelectedItemPosition() == 3) {
            addSurveyRequest.genderId = "T";
            count++;
        }*/

            if(hodFamily.length() >0) {
                addSurveyRequest.slumFamilyHeadName = hodFamily;
                count++;
            }
            if(husbandName.length() >0) {
                addSurveyRequest.slumFatherHusbandName = husbandName;
                count++;
            }
            if(adharNo.length() >0) {
                addSurveyRequest.slumAdharNo = adharNo;
                addSurveyRequest.adharNo = null;
                addSurveyRequest.idType = "0";
                count++;
            }
            if(contactNo.length() >0) {
                addSurveyRequest.contactNo = contactNo;
                count++;
            }
            if( dOb1.trim().length() >= 2) {
              /*  if(addSurveyRequest.isNewRecord || (addSurveyRequest.dob == null)) {
                    String dd = "", mm = "", yy = "";
                    dd = dOb1.substring(0,3);
                    mm = dOb1.substring(3,6);
                    yy = dOb1.substring(6);
                    addSurveyRequest.dob = mm+dd+yy;
                } else {
                    addSurveyRequest.dob = dOb1;
                }*/
                addSurveyRequest.dob = dOb1;
                count++;
            }
            if(yearsOfStay.getSelectedItemPosition() != 0) {
                //yrs of stay
            }
// For slum
            if (ulb_dropdown.getSelectedItemPosition() >0) {
                addSurveyRequest.slumUlbNameId = Integer.toString(ulb_dropdown.getSelectedItemPosition());
                count++;
            }
            if(getStringFromEditText(ward_detail_info).length() > 0) {
                addSurveyRequest.slumWardDetails = getStringFromEditText(ward_detail_info);
                count++;
            }

            if(preAdd.length() > 1) {
                addSurveyRequest.slumPresentHouseNo = preAdd;
                count++;
                if(addSurveyRequest.slumPresentHouseNo != null) {
                    if(addSurveyRequest.slumPresentHouseNo.length() >= 100) {
                        addSurveyRequest.slumPresentHouseNo = addSurveyRequest.slumPresentHouseNo.substring(0,99);
                    }
                }
            }
            if(getStringFromEditText(slum_Name).length() > 0) {
                addSurveyRequest.slumPresentStreetName = getStringFromEditText(slum_Name);
                Random num = new Random();
                int city = num.nextInt(35);
                addSurveyRequest.slumPresentCity = "3";
                if(city > 0) {
                    addSurveyRequest.slumPresentCity = Integer.toString(city);
                }
                count++;
            }
            if(precontact.length() >= 8) {
                addSurveyRequest.presentMobileNo = precontact;
                count++;
            }
            if(isSameAdd.isChecked()) {
                addSurveyRequest.slumIsSameAsPresentAdd = "Y";
                addSurveyRequest.slumPermanentHouseNo = addSurveyRequest.slumPresentHouseNo;
                addSurveyRequest.slumPermanentStreetName = addSurveyRequest.slumPresentStreetName;
                if(addSurveyRequest.slumPresentCity != null ) {
                    addSurveyRequest.permanentCity = addSurveyRequest.slumPresentCity;
                    addSurveyRequest.permanentTown = addSurveyRequest.slumPresentCity;
                }
                addSurveyRequest.permanentMobileNo = addSurveyRequest.presentMobileNo;
            } else {
                // String permcontact = getStringFromEditText();
                addSurveyRequest.slumIsSameAsPresentAdd = "N";
                if(permAdd.length() >1) {
                    addSurveyRequest.slumPermanentHouseNo = permAdd;
                    count++;
                    if(addSurveyRequest.slumPermanentHouseNo != null) {
                        if(addSurveyRequest.slumPermanentHouseNo.length() >= 100) {
                            addSurveyRequest.slumPermanentHouseNo = addSurveyRequest.slumPermanentHouseNo.substring(0,99);
                        }
                    }

                }
                if(getStringFromEditText(slum_Name).length() > 1) {
                    addSurveyRequest.slumPermanentStreetName = getStringFromEditText(slum_Name);
                    Random num = new Random();
                    int city = num.nextInt(35);
                    addSurveyRequest.slumPermanentCity = "3";
                    if(city > 0) {
                        addSurveyRequest.slumPermanentCity = Integer.toString(city);
                    }
                    count++;
                }
                if(town.length()>1) {
                    Random num = new Random();
                    int city = num.nextInt(40);
                    addSurveyRequest.slumPermanentTown = "3";
                    if(city > 0) {
                        addSurveyRequest.slumPermanentTown = Integer.toString(city);
                    }
                    count++;
                }
                if(precontact.length() >= 8) {
                    addSurveyRequest.permanentMobileNo = precontact;
                    count++;
                }
            }
            if(other_land_chkbox.isChecked()) {
                addSurveyRequest.slumOwnsRadio = "Y";
                if(getStringFromEditText(other_house).length() > 1) {
                    addSurveyRequest.slumLandAddress = getStringFromEditText(other_house);
                    count++;
                }
                if(getStringFromEditText(land_size).length() > 1) {
                    addSurveyRequest.slumLandinSqm = getStringFromEditText(land_size);
                    count++;
                }
            } else {
                addSurveyRequest.slumOwnsRadio = "N";
            }
            if(!adharchkBox.isChecked()) {
                if(no_aadhar_reason.getText().toString().trim().length() > 0) {
                    count++;
                    addSurveyRequest.adharReason = no_aadhar_reason.getText().toString().trim();
                }
            } else {
                addSurveyRequest.adharReason = null;
            }
            isValid = false;
            AddSurveyDataManager.mFieldscount = count;
            Log.v("PMAY", " mFieldscount Non Slum "+AddSurveyDataManager.mFieldscount);

            if(isSubmit) {
                if(addSurveyRequest.isNewRecord)
                    addSurveyRequest.isSubmittedFlag = "Y";
                if( count >= 4) {
                    isValid = true;
                }
            } else {
                if(addSurveyRequest.isNewRecord)
                    addSurveyRequest.isSubmittedFlag = "N";
                if( count >= 2) {
                    isValid = true;
                }
            }

        } else {
            // Non slum
            if(casteSpinner.getSelectedItemPosition() > 0) {
                addSurveyRequest.caste = Integer.toString(casteSpinner.getSelectedItemPosition());
                count++;
            }
            if(idProofSpinner.getSelectedItemPosition() > 0) {
                addSurveyRequest.idType = Integer.toString(idProofSpinner.getSelectedItemPosition());
                count++;
            }
            if(religionSpinner.getSelectedItemPosition() > 0) {
                addSurveyRequest.religion = Integer.toString(religionSpinner.getSelectedItemPosition());
                count++;
            }
            if(maritalSpinner.getSelectedItemPosition() > 0) {
                addSurveyRequest.maritalStatus = Integer.toString(maritalSpinner.getSelectedItemPosition());
                count++;
            }
            if (genderSpinner.getSelectedItemPosition() >0) {
                addSurveyRequest.genderId = Integer.toString(genderSpinner.getSelectedItemPosition());
                count++;
            }/* else if (genderSpinner.getSelectedItemPosition() == 2) {
            addSurveyRequest.genderId = "F";
            count++;
        } else if (genderSpinner.getSelectedItemPosition() == 3) {
            addSurveyRequest.genderId = "T";
            count++;
        }*/

            if(hodFamily.length() >0) {
                addSurveyRequest.familyHeadName = hodFamily;
                count++;
            }
            if(husbandName.length() >0) {
                addSurveyRequest.fatherHusbandName = husbandName;
                count++;
            }
            if(adharNo.length() >0) {
                addSurveyRequest.adharNo = adharNo;
                addSurveyRequest.idType = "0";
                count++;
            }
            if(contactNo.length() >0) {
                addSurveyRequest.contactNo = contactNo;
                count++;
            }
            if( dOb1.trim().length() >= 2) {
               /* if(addSurveyRequest.isNewRecord || (addSurveyRequest.dob == null)) {
                    String dd = "", mm = "", yy = "";
                    dd = dOb1.substring(0,3);
                    mm = dOb1.substring(3,6);
                    yy = dOb1.substring(6);
                    addSurveyRequest.dob = mm+dd+yy;
                } else {
                    addSurveyRequest.dob = dOb1;
                }*/
                addSurveyRequest.dob = dOb1;
                count++;
            }
            if(yearsOfStay.getSelectedItemPosition() != 0) {
                //yrs of stay
            }


            if(preAdd.length() > 1) {
                addSurveyRequest.presentHouseNo = preAdd;
                count++;
                if(addSurveyRequest.presentHouseNo != null) {
                    if(addSurveyRequest.presentHouseNo.length() >= 100) {
                        addSurveyRequest.presentHouseNo = addSurveyRequest.presentHouseNo.substring(0,99);
                    }
                }
            }
            if(precity.length() > 0) {
                addSurveyRequest.presentStreetName = precity;
                Random num = new Random();
                int city = num.nextInt(35);
                addSurveyRequest.presentCity = "3";
                if(city > 0) {
                    addSurveyRequest.presentCity = Integer.toString(city);
                }
                count++;
            }
            if(precontact.length() >= 8) {
                addSurveyRequest.presentMobileNo = precontact;
                count++;
            }
            if(isSameAdd.isChecked()) {
                addSurveyRequest.isSameAsPresentAdd = "Y";
                addSurveyRequest.permanentHouseNo = addSurveyRequest.presentHouseNo;
                addSurveyRequest.permanentStreetName = addSurveyRequest.presentStreetName;
                if(addSurveyRequest.presentCity != null ) {
                    addSurveyRequest.permanentCity = addSurveyRequest.presentCity;
                    addSurveyRequest.permanentTown = addSurveyRequest.presentCity;
                }
                addSurveyRequest.permanentMobileNo = addSurveyRequest.presentMobileNo;
            } else {
                // String permcontact = getStringFromEditText();
                addSurveyRequest.isSameAsPresentAdd = "N";
                if(permAdd.length() >1) {
                    addSurveyRequest.permanentHouseNo = permAdd;
                    count++;
                    if(addSurveyRequest.permanentHouseNo != null) {
                        if(addSurveyRequest.permanentHouseNo.length() >=100) {
                            addSurveyRequest.permanentHouseNo = addSurveyRequest.permanentHouseNo.substring(0,99);
                        }
                    }
                }
                if(permcity.length() > 1) {
                    addSurveyRequest.permanentStreetName = permcity;
                    Random num = new Random();
                    int city = num.nextInt(35);
                    addSurveyRequest.permanentCity = "3";
                    if(city > 0) {
                        addSurveyRequest.permanentCity = Integer.toString(city);
                    }
                    count++;
                }
                if(town.length()>1) {
                    Random num = new Random();
                    int city = num.nextInt(40);
                    addSurveyRequest.permanentTown = "3";
                    if(city > 0) {
                        addSurveyRequest.permanentTown = Integer.toString(city);
                    }
                    count++;
                }
                if(precontact.length() >= 8) {
                    addSurveyRequest.permanentMobileNo = precontact;
                    count++;
                }
            }
            if(other_land_chkbox.isChecked()) {
                if(getStringFromEditText(other_house).length() > 1) {
                    addSurveyRequest.landLocation = getStringFromEditText(other_house);
                    count++;
                }
                if(getStringFromEditText(land_size).length() > 1) {
                    addSurveyRequest.landinSqm = getStringFromEditText(land_size);
                    count++;
                }
            }
            if(!adharchkBox.isChecked()) {
                if(no_aadhar_reason.getText().toString().trim().length() > 0) {
                    count++;
                    addSurveyRequest.adharReason = no_aadhar_reason.getText().toString().trim();
                }
            } else {
                addSurveyRequest.adharReason = null;
            }
            isValid = false;
            AddSurveyDataManager.mFieldscount = count;
            Log.v("PMAY", " mFieldscount Non Slum "+AddSurveyDataManager.mFieldscount);

            if(isSubmit) {
                if(addSurveyRequest.isNewRecord)
                addSurveyRequest.isSubmitted = "Y";
                if( count >= 4) {
                    isValid = true;
                }
            } else {
                if(addSurveyRequest.isNewRecord)
                addSurveyRequest.isSubmitted = "N";
                if( count >= 2) {
                    isValid = true;
                }
            }
        }

        return  isValid;
    }

    ProgressDialog mProgressDialog;

    void showSaveDialogForOfflineRecords(boolean isOffline) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        alertDialog.setTitle("Please check your internet connections");
        String adhar_no = "";
        if(addSurveyRequest != null && addSurveyRequest.slumAdharNo != null) {
            adhar_no = addSurveyRequest.slumAdharNo;
        } else if(addSurveyRequest != null && addSurveyRequest.adharNo != null) {
            adhar_no = addSurveyRequest.adharNo;
        }

        if(isSubmittedRecord(adhar_no)) { //BISW27Mar18:
            //duplicate
            alertDialog.setTitle("Aadhar Exist !");
            alertDialog.setMessage("The Survey record with Aadhar Number <" + adhar_no + "> already present, please use a different Aadhar number.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                   // return;
                }
            });
            alertDialog.show();
            return;
        }

        String msg = getResources().getString(R.string.save_message_offline);
        if(CommonUtils.is2GNetwork(this)) {
            msg = getResources().getString(R.string.save_message_low_network);
        }
        if(!adhar_no.isEmpty()) {
            msg += " with Aadhar Number <" + adhar_no + ">.";
//            if(isOffline) {
//                alertDialog.setMessage(getResources().getString(R.string.save_message_no_network)+" with Aadhar Number <" + adhar_no + ">" + " due to low bandwidth.");
//            }
        } else {
//            alertDialog.setMessage(getResources().getString(R.string.save_message_offline));
//            if(isOffline) {
//                alertDialog.setMessage(getResources().getString(R.string.save_message_no_network));
//            }
        }
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                addOfflineRecordtoDb();
                finish();
            }
        });

        /*alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                   finish();
            }
        });*/ //BISW27Mar18: Need not to show the cancel button
        alertDialog.show();
    }

    void displayUploadResponseDialog(boolean isSubmitted, final boolean isSuccess, String surveyID, String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        alertDialog.setTitle("Success");
        //alertDialog.setIndeterminate(true);
        String lDisplayMsg = "";
        if(isSubmitted) {
            if(isSuccess) {
                 //lDisplayMsg = "Survey data has been submitted successfully with Survey ID = " + surveyID.toString();
                lDisplayMsg = getResources().getString(R.string.submit_message_success) + " with survey ID = "+surveyID.toString();
                alertDialog.setMessage(lDisplayMsg);
                //alertDialog.setMessage(R.string.submit_message_success);
            }
            else {
                alertDialog.setMessage(TextUtils.isEmpty(message) ? getResources().getString(R.string.submit_message_fail) : message) ;
                alertDialog.setTitle("Error");            }
        } else {
            if(isSuccess) {
                lDisplayMsg = getResources().getString(R.string.save_message_success) +" with survey ID = "+  surveyID.toString();
                alertDialog.setMessage(lDisplayMsg);
                //alertDialog.setMessage(R.string.save_message_success);
            }
            else {
                alertDialog.setMessage(TextUtils.isEmpty(message) ? getResources().getString(R.string.save_message_fail) : message) ;
                alertDialog.setTitle("Error");
            }
        }
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if(isSuccess) {
                  finish();
              }
            }
        });
        alertDialog.show();
    }

    void submitConfirmDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage(R.string.submit_slum_message);
        //alertDialog.setIndeterminate(true);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                addSurveyRequest.validationPendingStatus = "N";
                createUploadRequest(false);
                if("S".equals(addSurveyRequest.chckSlumRadio)) {
                    addSurveyRequest.isSubmittedFlag = "Y";
                } else {
                    addSurveyRequest.isSubmitted = "Y";
                }
                sendAddSurveyReq(true);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*addSurveyRequest.validationPendingStatus = "Y";
                if("S".equals(addSurveyRequest.chckSlumRadio)) {
                    addSurveyRequest.isSubmittedFlag = "N";
                } else {
                    addSurveyRequest.isSubmitted = "Y";
                }
                createUploadRequest(true);
                sendAddSurveyReq(true);*/
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });

        //alertDialog.show();

  /*      alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

            }
        });*/
        alertDialog.show();
    }


    boolean isWiFiDATAConnected() {
        /*final ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telMgr = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        //Log.v("PMAY", " isWiFiDATAConnected()  "+mobile.getType());
        if ((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected() && telMgr.getNetworkType() != TelephonyManager.NETWORK_TYPE_EDGE)) {
           return  true;
        }*/
        if(CommonUtils.isNetworkAvailable(this) && !CommonUtils.is2GNetwork(this)) {
            return true;
        }
        return false;
    }
    public void onSaveClicked(View v) {
       // Toast.makeText(getBaseContext(), "Your data is saved.. ", Toast.LENGTH_SHORT).show();
        //Log.v("PMAY", " save click "+dob.getText().toString().length() +" , "+dob.getText().toString());
        boolean isValid = validateRequestFileds(false);
        if(!isValid) {
            Toast.makeText(getApplicationContext(), "Please add necessary information before Saving survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (ulb_dropdown.getSelectedItemPosition() == 0) { //BISW25Mar18:02 Don't allow to save/ edit in case ULB name is blank
            Toast.makeText(getApplicationContext(), "Please enter ULB name before saving Survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if (ward_detail_info.getText().toString().length() == 0) { //BISW27Mar18:02 Don't allow to save/ edit in case Ward number is blank
            Toast.makeText(getApplicationContext(), "Please enter Ward number before saving Survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if(addSurveyDataManager.applicantPhotoFile == null && addSurveyRequest.isNewRecord) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Applicant photo  is required for survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if (input_name.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Name of the Head of the Family(Beneficiary Name) is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        }  else if (genderSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Beneficiary Gender is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.adharNo == null && addSurveyRequest.slumAdharNo == null) {
            Toast.makeText(getApplicationContext(), "Please enter Aadhar or other ID Number before saving survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (adhar_num.getText().toString().length() < 12){
            Toast.makeText(getApplicationContext(), "Aadhar number is less than 12, please re-enter.", Toast.LENGTH_SHORT).show();
            return;
        } else if (dob.getText().toString().length() <= 2){
            Toast.makeText(getApplicationContext(), "Please enter Date of birth before saving survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if((!"S".equals(addSurveyRequest.chckSlumRadio))) { //BISW27Mar18:
            if (addSurveyDataManager.missionSpinnerSaved == 0) { //BISW27Mar18: Need to check if misson component is selected or not
                // not requireed to check mission component
//                Toast.makeText(getBaseContext(), "Please enter Preferred component of mission before submitting Survey data.", Toast.LENGTH_SHORT).show();
//                return;
            } /*else if (addSurveyDataManager.presentHousePhotoFile == null && addSurveyRequest.isNewRecord) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(getBaseContext(), "Present house photo is required for survey data.", Toast.LENGTH_SHORT).show();
                return;
            }*/
        } else if(reason_non_eligible != null && reason_non_eligible.getVisibility() == View.VISIBLE
                && reason_non_eligible.getText().length() < 8) {
            Toast.makeText(getApplicationContext(), "Please enter valid reason for non-eligibility before saving survey data.", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Saving ...");
        mProgressDialog.show();

        AddSurveyDataManager.mFieldscount = 0;
        /*Map<String, AddSurveyRequest> map = new HashMap<String, AddSurveyRequest>();
        map.put("surveyData", addSurveyRequest);
       *//* RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), "surveyData");*//*
       Call<ResponseBody> callUpload =  apiInterface.addSurveyDataTest(addSurveyRequest);
        //Call<ResponseBody> callUpload =  apiInterface.addSurveyDataTrial(description, map);
        callUpload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.v(" PMAY ", " onFailure "+t.getMessage());
            t.printStackTrace();
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v(" PMAY ", " OnResponse " +response );
                if(response != null) {
                    Log.v(" PMAY ", " OnResponse " +response.message() +response.errorBody());
                }
            }
        });*/
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            addSurveyRequest.isSubmittedFlag = "N";
        } else {
            addSurveyRequest.isSubmitted = "N";
        }
       createUploadRequest(false);
        sendAddSurveyReq(false);
    }

    void createUploadRequest(final boolean isSubmitted) {
       //Non slum
        if(addSurveyDataManager == null)
            addSurveyDataManager = AddSurveyDataManager.getInstance();

        addSurveyDataManager.isEdited = true;

        if("N".equals(addSurveyRequest.slumRadio)) {
            if((mAddSurveyEditRecord != null) && (mAddSurveyEditRecord.preferredAssistanceHfa != null)) {
                addSurveyDataManager.missionSpinnerSaved = Integer.parseInt(mAddSurveyEditRecord.preferredAssistanceHfa);
            }

            if(addSurveyDataManager.missionSpinnerSaved >= 0) {
                addSurveyRequest.preferredAssistanceHfa = Integer.toString(addSurveyDataManager.missionSpinnerSaved);
            }

            if(addSurveyDataManager.eligibleSaved) {
                addSurveyRequest.eligibleStatus = "Y";
            }
            if(addSurveyDataManager.eligibleReasonSaved != null) {
                addSurveyRequest.nonEligibleReason = addSurveyDataManager.eligibleReasonSaved;
            }

        }
        /*if(spinn >= 0) {
            addSurveyRequest.preferredAssistanceHfa = Integer.toString(addSurveyDataManager.missionSpinnerSaved);
        }*/
        addSurveyDataManager.mAttachments.clear();
        List<MultipartBody.Part> attachments = addSurveyDataManager.mAttachments;

        //RequestBody description = RequestBody.create(MultipartBody.FORM, addSurveyDataManager.slumBiometricDetails);

        if( addSurveyDataManager.applicantPhotoFile != null) {
            addSurveyDataManager.setGeoTaggingAttributes(addSurveyDataManager.applicantPhotoFile.getAbsolutePath());
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.applicantPhotoFile, AddSurveyDataManager.APPLICANT_PHTO);
            attachments.add(applicantImage);
        }
        if(addSurveyDataManager.IdPhotoFile != null) {
            MultipartBody.Part idproof = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.IdPhotoFile, AddSurveyDataManager.ID_PHTO);
            attachments.add(idproof);
        }
        if( addSurveyDataManager.landRecordPhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.landRecordPhotoFile, AddSurveyDataManager.LAND_RECORD_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.signaturePhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.signaturePhotoFile, AddSurveyDataManager.SIGNATURE_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.presentHousePhotoFile != null) {
            addSurveyDataManager.setGeoTaggingAttributes(addSurveyDataManager.presentHousePhotoFile.getAbsolutePath());
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.presentHousePhotoFile, AddSurveyDataManager.PRESENT_HOUSE_PHTO);
            attachments.add(applicantImage);
        }
        Log.v("PMAY", " Size of the attachments "+attachments.size());

        //Call<ResponseBody> callUpload =  apiInterface.uploadSurveyData(description, applicantImage, addSurveyRequest);
        //addSurveyRequest.surveyId = "448825";

    }

    public void sendAddSurveyReq(final boolean isSubmitted) {
        //Remove
        if(!AddSurveyDataManager.IS_BIOMETRIC_RESTRICTED) {
            if (addSurveyDataManager.biometricDetails == null && addSurveyDataManager.slumBiometricDetails == null) {
                addSurveyDataManager.biometricDetails = new byte[1];
                addSurveyDataManager.slumBiometricDetails = new byte[1];
            }
        }
        if(!isWiFiDATAConnected()) {
            //Offline
            boolean iserror = false;
            if(!addSurveyRequest.isNewRecord && "Y".equals(addSurveyRequest.isSubmitted)) {
                //Toast.makeText(getBaseContext(), "Survey data can not be edited in offline mode", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, //BISW27Mar18: Needs to show a dialog rather toast
                        R.style.AppTheme_Dark_Dialog);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Survey data can not be edited");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        return;
                    }
                });
                alertDialog.show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            } else if (ulb_dropdown.getSelectedItemPosition() == 0) { //BISW25Mar18:02 Don't allow to save/ edit in case ULB name is blank
                Toast.makeText(getApplicationContext(), "Please enter ULB name before saving Survey data.", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            } else if (input_name.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(), "Name of the Head of the Family(Beneficiary Name) is required for survey data", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            } /*else if (husband_name.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(), "Father's/Husband name is required for survey data", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            }*/ else if (genderSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(getApplicationContext(), "Beneficiary Gender is required for survey data", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            } else if (addSurveyRequest.adharNo == null && addSurveyRequest.slumAdharNo == null) {
                Toast.makeText(getApplicationContext(), "Please enter Aadhar or other ID Number before saving survey data", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            } else if (adhar_num.getText().toString().length() < 12){
                Toast.makeText(getApplicationContext(), "Aadhar number is less than 12, please re-enter.", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            }  else if (dob.getText().toString().length() <= 2){
                Toast.makeText(getApplicationContext(), "Please enter Date of birth before submitting survey data.", Toast.LENGTH_SHORT).show();
                return;
            }  else
            if(addSurveyRequest != null && "S".equals(addSurveyRequest.chckSlumRadio)) {
                if (addSurveyDataManager.slumBiometricDetails == null && addSurveyRequest.isNewRecord) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    if(!Constants.ENABLE_CHEAT_MODE) {
                        Toast.makeText(getBaseContext(), "Biometric Thumb impression is required for saving or submitting survey data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } else if(addSurveyDataManager.biometricDetails == null && addSurveyRequest.isNewRecord) {
                //addSurveyDataManager.biometricDetails = new byte[1];
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if(!Constants.ENABLE_CHEAT_MODE) {
                    Toast.makeText(getBaseContext(), "Biometric Thumb impression is required for saving or submitting survey data", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if(reason_non_eligible != null && reason_non_eligible.getVisibility() == View.VISIBLE
                    && reason_non_eligible.getText().length() < 8) {
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Please enter valid reason for non-eligibility before saving survey data.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!isSubmitted) {
                if(addSurveyRequest.surveyId != null /*&& addSurveyRequest.surveyId.equals("0")*/) {
                    //record.isSubmitted = "O";
                    //addSurveyRequest.surveyId = surveyId;
                    addSurveyRequest.isSubmitted = "O";
                    addSurveyRequest.isSubmittedFlag = "O";
                }
            } else {
                //Toast.makeText(this, "Please check your internet connections", Toast.LENGTH_LONG).show();
                //offline submit
                addSurveyRequest.isSubmitted = "OSB";
                addSurveyRequest.isSubmittedFlag = "OSB";
//                addSurveyRequest.validationPendingStatus = "N";
            }
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            showSaveDialogForOfflineRecords(true);
            return;
        }
        Call<AddSurveyResponse> callUpload;

        //check Geo Location for saved records
        if(addSurveyRequest != null && !addSurveyRequest.isNewRecord) {
            // dont send if they are 0
           if("0".equals(addSurveyRequest.geoLatitude) || addSurveyRequest.geoLatitude == null) {
               addSurveyRequest.geoLatitude = addSurveyRequest.bplNo;
               addSurveyRequest.bplNo = null;
           }
            if("0".equals(addSurveyRequest.geoLongitude) || addSurveyRequest.geoLongitude == null) {
                addSurveyRequest.geoLongitude = addSurveyRequest.rationCardNo;
                addSurveyRequest.rationCardNo = null;
            }
            if(addSurveyRequest.geoLatitude != null && !"0".equalsIgnoreCase(addSurveyRequest.geoLatitude)) {
                addSurveyRequest.bplNo = null;
            }
            if(addSurveyRequest.geoLongitude != null && !"0".equalsIgnoreCase(addSurveyRequest.geoLongitude)) {
                addSurveyRequest.rationCardNo = null;
            }
        } else {
            // new record
        }
        if(addSurveyRequest != null && "S".equals(addSurveyRequest.chckSlumRadio)) {
            if(!Constants.ENABLE_CHEAT_MODE && addSurveyDataManager.slumBiometricDetails == null && addSurveyRequest.isNewRecord) {
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if(!Constants.ENABLE_CHEAT_MODE) {
                    Toast.makeText(getBaseContext(), "Biometric Thumb impression is required for saving or submitting survey data", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(addSurveyDataManager.slumBiometricDetails != null) {
                RequestBody description = RequestBody.create(MultipartBody.FORM, addSurveyDataManager.slumBiometricDetails);
                callUpload =  apiInterface.uploadMultiFilesSurveyDataSlum(description, AddSurveyDataManager.getInstance().mAttachments, addSurveyRequest);
            } else {
                callUpload =  apiInterface.uploadMultiFilesSurveyDataSlum(AddSurveyDataManager.getInstance().mAttachments, addSurveyRequest);
            }
        } else {
             if(addSurveyDataManager.biometricDetails == null && addSurveyRequest.isNewRecord) {
                 //addSurveyDataManager.biometricDetails = new byte[1];
                 if(mProgressDialog != null) {
                     mProgressDialog.dismiss();
                 }
                 if(!Constants.ENABLE_CHEAT_MODE && "S".equalsIgnoreCase(addSurveyRequest.chckSlumRadio)) {
                     Toast.makeText(getBaseContext(), "Biometric Thumb impression is required for saving or submitting survey data", Toast.LENGTH_SHORT).show();
                     return;
                 }
             }
            if(addSurveyDataManager.biometricDetails != null) {
                RequestBody description = RequestBody.create(MultipartBody.FORM, addSurveyDataManager.biometricDetails);
                callUpload =  apiInterface.uploadMultiFilesSurveyData(description, AddSurveyDataManager.getInstance().mAttachments, addSurveyRequest);
            } else {
                callUpload =  apiInterface.uploadMultiFilesSurveyData(AddSurveyDataManager.getInstance().mAttachments, addSurveyRequest);
            }
        }
        callUpload.enqueue(new Callback<AddSurveyResponse>() {

            @Override
            public void onResponse(Call<AddSurveyResponse> call, Response<AddSurveyResponse> response) {
                //Log.d("PMAY","UPLOAD response : "+response.body().success);
                if( mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if(response != null && response.body() != null) {
                    if(response.body().success) {
                        if(addSurveyRequest.isNewRecord) {
                            addSurveyDataToDB(response.body().surveyId);
                        } else {
                            // modify
                            updatedSurveyDataToDB(response.body().surveyId);
                        }
                        resetAllFields();
                    }

                    displayUploadResponseDialog(isSubmitted, response.body().success, response.body().surveyId, response.body().message);
                } else {
                    //displayUploadResponseDialog(isSubmitted, false);
                    try {
                        /*String str = ;
                        str = str.trim();*/
                        if(response!= null && response.errorBody() != null) {
                            Log.v("PMAY", " onFailureResp - 3rd screen " + "Duplicate".contains(response.errorBody().string().toString()));
                            if (response.errorBody() != null && ("Duplicate".contains(response.errorBody().string().toString()))) {
                                displayUploadResponseDialog(false, false, "", response.message());
                                return;
                            }
                        }

                    }catch (Exception e) {

                    }
                    String msg = "";
                    if(response != null && response.body() != null) {
                        msg = response.body().message;
                    }
                    if(!isSubmitted) {
                        //showSaveDialogForOfflineRecords(false);
                        displayUploadResponseDialog(false, false, "", msg);
                        return;
                    } else {
                        displayUploadResponseDialog(false, false, "", msg);
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<AddSurveyResponse> call, Throwable t) {
                if( mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if(!isSubmitted) {
                    //showSaveDialogForOfflineRecords(false);
                    Toast.makeText(getApplicationContext(), "Unable to save. Please try again", Toast.LENGTH_LONG).show();
//                    return;
                } else {
                    if(SocketTimeoutException.class == t.getClass()) {
                        Toast.makeText(getApplicationContext(), "Request timeout. Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to submit. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    void updatedSurveyDataToDB(String surveyId) {

        SurveyDataModal record = new SurveyDataModal();

        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            // slum
            if(addSurveyRequest.slumFamilyHeadName != null && !addSurveyRequest.slumFamilyHeadName.isEmpty()) {
                record.mName = addSurveyRequest.slumFamilyHeadName;
            }
            if (addSurveyRequest.slumAdharNo != null && !addSurveyRequest.slumAdharNo.isEmpty()) {
                record.mAdharNo = addSurveyRequest.slumAdharNo;
            }
             Log.v("PMAY", " updated record : "+ record.mAdharNo +" "+addSurveyRequest.slumAdharNo + " , "+addSurveyRequest.adharNo);
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

        if(updateDuplicateRecords(addSurveyRequest.adharNo)) {
            mPmayDatabaseHelper.updateUser(addSurveyRequest, true);
        } else {
            mPmayDatabaseHelper.addUser(addSurveyRequest);
        }
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            record.isSubmitted = addSurveyRequest.isSubmittedFlag;
        } else {
            record.isSubmitted = addSurveyRequest.isSubmitted;
        }
        Log.v("PMAY", "Record  surveyId: "+addSurveyRequest.surveyId  +", "+addSurveyRequest.slumAdharNo +" , "+addSurveyRequest.adharNo);
        AddSurveyDataManager.getInstance().surveyDataModals.add(record);

    }

    void updatedEditedRecordList(String surveyId) {
       List recordsList =  AddSurveyDataManager.getInstance().surveyDataModals;
        Log.v("PMAY", " before Removed record - size : "+recordsList.size());
       if(recordsList.size() > 0 && !recordsList.isEmpty()) {
           Iterator<SurveyDataModal> itr = recordsList.iterator();
           while (itr.hasNext()) {
               SurveyDataModal surveyDataModal = itr.next();
               if(surveyDataModal != null && surveyDataModal.mSurveyId != null && surveyDataModal.mSurveyId.equals(surveyId)) {
                   itr.remove();
                   Log.v("PMAY", " Removed record : "+surveyDataModal.mSurveyId);
                   //break;
               }
           }
       }
        Log.v("PMAY", " Removed record - size : "+AddSurveyDataManager.getInstance().surveyDataModals.size());
    }

    boolean updateDuplicateRecords(String adhar) {
        boolean ret = false;
        List recordsList =  AddSurveyDataManager.getInstance().surveyDataModals;
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            adhar = addSurveyRequest.slumAdharNo;
        } else {
            adhar = addSurveyRequest.adharNo;
        }
        Log.v("PMAY", " before Removed record by Adhar - size : "+recordsList.size() + " adhar : "+adhar);
        if(recordsList.size() > 0 && !recordsList.isEmpty()) {
            Iterator<SurveyDataModal> itr = recordsList.iterator();
            while (itr.hasNext()) {
                SurveyDataModal surveyDataModal = itr.next();
                Log.v("PMAY", " Removed records : old  "+ surveyDataModal.mAdharNo +" new "+addSurveyRequest.slumAdharNo +" "+addSurveyRequest.adharNo);
                if(surveyDataModal != null && surveyDataModal.mAdharNo != null && surveyDataModal.mAdharNo.trim().equals(adhar)) {
                    itr.remove();
                    Log.v("PMAY", " Removed record : "+surveyDataModal.mSurveyId);
                    //break;
                    ret = true;
                }
            }
        }
        Log.v("PMAY", " Removed record - size : "+AddSurveyDataManager.getInstance().surveyDataModals.size());
        return ret;
    }


	boolean isUpdated = false;

    boolean isSubmittedRecord(String adhar) {
        boolean ret = false;
       // isUpdated = false;
        List recordsList =  AddSurveyDataManager.getInstance().surveyDataModals;
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            adhar = addSurveyRequest.slumAdharNo;
        } else {
            adhar = addSurveyRequest.adharNo;
        }
        //Log.v("PMAY", " before Removed record by Adhar - size : "+recordsList.size() + " adhar : "+adhar);
        if(recordsList.size() > 0 && !recordsList.isEmpty()) {
            Iterator<SurveyDataModal> itr = recordsList.iterator();
            while (itr.hasNext()) {
                SurveyDataModal surveyDataModal = itr.next();
                //Log.v("PMAY", " Removed records : old  "+ surveyDataModal.mAdharNo +" new "+addSurveyRequest.slumAdharNo +" "+addSurveyRequest.adharNo);
                if(surveyDataModal != null && surveyDataModal.mAdharNo != null && surveyDataModal.mAdharNo.trim().equals(adhar)) {
                    //Adhar matched check if it is offline record
                    Log.v("PMAY", " isSubmittedRecord() record Id : "+surveyDataModal.mAdharNo +" , "+surveyDataModal.isSubmitted);
                    if(!"0".equals(surveyDataModal.mSurveyId) && "Y".equals(surveyDataModal.isSubmitted)) {
                        ret = true;
                    }
                }
            }
        }
        Log.v("PMAY", " Removed record - size : "+AddSurveyDataManager.getInstance().surveyDataModals.size());
        return ret;
    }
	
	boolean updateDuplicateRecordsInOffline(String adhar) {
        boolean ret = false;
		isUpdated = false;
        List recordsList =  AddSurveyDataManager.getInstance().surveyDataModals;
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            adhar = addSurveyRequest.slumAdharNo;
        } else {
            adhar = addSurveyRequest.adharNo;
        }
        Log.v("PMAY", " before Removed record by Adhar - size : "+recordsList.size() + " adhar : "+adhar);
        if(recordsList.size() > 0 && !recordsList.isEmpty()) {
            Iterator<SurveyDataModal> itr = recordsList.iterator();
            while (itr.hasNext()) {
                SurveyDataModal surveyDataModal = itr.next();
                //Log.v("PMAY", " Removed records : old  "+ surveyDataModal.mAdharNo +" new "+addSurveyRequest.slumAdharNo +" "+addSurveyRequest.adharNo);
                if(surveyDataModal != null && surveyDataModal.mAdharNo != null && surveyDataModal.mAdharNo.trim().equals(adhar)) {
					//Adhar matched check if it is offline record
                    Log.v("PMAY", " Update offline record Id : "+surveyDataModal.mAdharNo +" , "+surveyDataModal.isSubmitted);
					if("0".equals(surveyDataModal.mSurveyId)) {
						itr.remove();
                        Log.v("PMAY", " Removed record : "+surveyDataModal.mSurveyId);
						mPmayDatabaseHelper.updateUser(addSurveyRequest, true);
                        Log.v("PMAY", "updateDuplicateRecordsInOffline- Updated for surveyId 0 " );
						isUpdated = true;
                        //break;
                        ret = false;
					} else {
                        if(!"Y".equals(surveyDataModal.isSubmitted)) {
                           //Existing saved record
                            addSurveyRequest.surveyId = surveyDataModal.mSurveyId; // store the surveyId
                           // addSurveyRequest.isSubmitted = "O";
                            //addSurveyRequest.isSubmittedFlag = "O"; // Non submitted saved records in offline
                            itr.remove();
                            ret = false;
                            mPmayDatabaseHelper.updateUser(addSurveyRequest, true);
                            Log.v("PMAY", "updateDuplicateRecordsInOffline- Updated for surveyId Non 0 " );
                            isUpdated = true;
                        } else if("Y".equals(surveyDataModal.isSubmitted)) {
                            ret = true;
                            isUpdated = true;
                        }
					}                  
                }
            }
        }
        Log.v("PMAY", " Removed record - size : "+AddSurveyDataManager.getInstance().surveyDataModals.size());
        return ret;
    }

    void addOfflineRecordtoDb() {
        // offline
        SurveyDataModal record = new SurveyDataModal();
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            // slum
            if(addSurveyRequest.slumFamilyHeadName != null && !addSurveyRequest.slumFamilyHeadName.isEmpty()) {
                record.mName = addSurveyRequest.slumFamilyHeadName;
            }
            if (addSurveyRequest.slumAdharNo != null && !addSurveyRequest.slumAdharNo.isEmpty()) {
                record.mAdharNo = addSurveyRequest.slumAdharNo;
                Log.v("PMAY", " Update account adhar "+record.mAdharNo);
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
        record.mSurveyId = addSurveyRequest.surveyId;

        //Save lat and long values in to bpl and ration card numbers for offline
        AddSurveyDataManager.getInstance().checkLastKnowLocation();
        if(!"0".equals(addSurveyRequest.geoLatitude))
            addSurveyRequest.bplNo = addSurveyRequest.geoLatitude;
        if(!"0".equals(addSurveyRequest.geoLongitude))
            addSurveyRequest.rationCardNo = addSurveyRequest.geoLongitude;

        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            record.mAdharNo = addSurveyRequest.slumAdharNo;
            record.isSubmitted = addSurveyRequest.isSubmittedFlag;
        } else {
            record.mAdharNo = addSurveyRequest.adharNo;
            record.isSubmitted = addSurveyRequest.isSubmitted;
        }
        if(AddSurveyDataManager.getInstance().mBitmapApplicantOffline != null) {
            AddSurveyDataManager.getInstance().storeOfflineImage(this, AddSurveyDataManager.getInstance().mBitmapApplicantOffline, record.mAdharNo, AddSurveyDataManager.APPLICANT_PHTO);
        }
        if(AddSurveyDataManager.getInstance().mBitmapHousePicOffline != null) {
            AddSurveyDataManager.getInstance().storeOfflineImage(this, AddSurveyDataManager.getInstance().mBitmapHousePicOffline, record.mAdharNo, AddSurveyDataManager.PRESENT_HOUSE_PHTO);
        }
        if(AddSurveyDataManager.getInstance().mBitmapFingerPrintOffline != null) {
            AddSurveyDataManager.getInstance().storeOfflineImage(this, AddSurveyDataManager.getInstance().mBitmapFingerPrintOffline, record.mAdharNo, AddSurveyDataManager.SIGNATURE_PHTO);
        }

        //Log.v("PMAY", "offline Record to be added "+ record.mAdharNo);
        if("OSB".equals(addSurveyRequest.isSubmitted) || "OSB".equals(addSurveyRequest.isSubmittedFlag))
            record.isSubmitted = "OSB";
        else
            record.isSubmitted = "O";

        if(addSurveyRequest.bankAccNo != null)
            record.mBankAccNo = addSurveyRequest.bankAccNo;

        if(updateDuplicateRecordsInOffline(record.mAdharNo)) {
            //duplicate
			//displayUploadResponseDialog(false, false, "");
			return;
        } else {
			if(!isUpdated) {
				mPmayDatabaseHelper.addUser(addSurveyRequest);
                Log.v("PMAY", "updateDuplicateRecordsInOffline- Added Newly "+ record.mAdharNo);
			} else {
                record.mSurveyId = addSurveyRequest.surveyId; // for saved offline records before
            }
        }
        AddSurveyDataManager.getInstance().surveyDataModals.add(record);
    }

    void addSurveyDataToDB(String surveyId) {
        SurveyDataModal record = new SurveyDataModal();
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

        if(addSurveyRequest.bankAccNo != null)
            record.mBankAccNo = addSurveyRequest.bankAccNo;

        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            record.isSubmitted = addSurveyRequest.isSubmittedFlag;
        } else {
            record.isSubmitted = addSurveyRequest.isSubmitted;
        }
        addSurveyRequest.surveyId = surveyId;
        mPmayDatabaseHelper.addUser(addSurveyRequest);
        AddSurveyDataManager.getInstance().surveyDataModals.add(record);
    }

    public void onIDProof(View v) {
        Toast.makeText(getBaseContext(), "Attachment for ID Proof  ", Toast.LENGTH_SHORT).show();
        permissionUtil.showAttachmentChooserDialog(this, attachListener, AddSurveyDataManager.ID_PHTO );
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void takePicture(View v) {
        if (permissionUtil.checkMarshMellowPermission()) {
            AddSurveyDataManager.getInstance().requestLocationUpdates(this);
            if (permissionUtil.verifyPermissions(this, permissionUtil.getCameraPermissions())) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                Toast.makeText(getBaseContext(), "Taking picture ", Toast.LENGTH_SHORT).show();                //launchCamera();
            } else
                ActivityCompat.requestPermissions(this, permissionUtil.getCameraPermissions(), CAMERA_REQUEST);
        }

    }

    void checkGPSPermissions() {
        if (permissionUtil.checkMarshMellowPermission()) {
            if (permissionUtil.verifyPermissions(this, permissionUtil.gpsPermissions)) {
                AddSurveyDataManager.getInstance().requestLocationUpdates(this);
            } else
                ActivityCompat.requestPermissions(this, permissionUtil.gpsPermissions, 56);

        }
    }

    void launchCameraIntent(int request) {
        if (permissionUtil.checkMarshMellowPermission()) {
            if (permissionUtil.verifyPermissions(this, permissionUtil.getCameraPermissions())) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, request);
                Toast.makeText(getBaseContext(), "Taking picture ", Toast.LENGTH_SHORT).show();                //launchCamera();
            } else
                ActivityCompat.requestPermissions(this, permissionUtil.getCameraPermissions(), request);
        }

    }

    public  void scanAadhar(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        List<String> list = new ArrayList<String>();
        //list.add(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Aadhar card ..");
        //integrator.createScanIntent();
        integrator.initiateScan();

    }

    public  void scanFingerprint(View v) {
        Intent intent = new Intent(getApplicationContext(), FingerPrintScannerActivity.class);
        startActivity(intent);
    }

    /**
     * process xml string received from aadhaar card QR code
     * @param scanData
     */
    protected void processScannedData(String scanData) {
        XmlPullParserFactory pullParserFactory;

        try {
            // init the parserfactory
            Log.d("PMAY","scanData  received : "+scanData);
            pullParserFactory = XmlPullParserFactory.newInstance();
            // get the parser
            XmlPullParser parser = pullParserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(scanData));
            String uid = "", name ="", gender = "", dob = "", yearOfBirth = "", careOf = "",
                    villageTehsil= "", postOffice= "", district= "", state= "", postCode= "", house_no= "", street= "", landMark= "";
            // parse the XML
            int eventType = parser.getEventType();
            Log.d("PMAY","scanData  : "+eventType);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("PMAY","scanData  : "+eventType);
                if(eventType == XmlPullParser.START_DOCUMENT) {

                } else if(eventType == XmlPullParser.START_TAG && AdharDataConstants.AADHAAR_DATA_TAG.equals(parser.getName())) {

                   uid = parser.getAttributeValue(null,AdharDataConstants.AADHAR_UID_ATTR);
                    //name
                   name = parser.getAttributeValue(null,AdharDataConstants.AADHAR_NAME_ATTR);
                    //gender
                    gender = parser.getAttributeValue(null,AdharDataConstants.AADHAR_GENDER_ATTR);
                    // year of birth
                    dob = parser.getAttributeValue(null,AdharDataConstants.AADHAR_DOB_ATTR);
                    yearOfBirth = parser.getAttributeValue(null,AdharDataConstants.AADHAR_YOB_ATTR);
                    // care of
                    careOf = parser.getAttributeValue(null,AdharDataConstants.AADHAR_CO_ATTR);
                    //Address
                    house_no = parser.getAttributeValue(null,AdharDataConstants.AADHAR_ADD_HOUSE_NO);
                    street = parser.getAttributeValue(null,AdharDataConstants.AADHAR_ADD_STREET);

                    // village Tehsil
                    villageTehsil = parser.getAttributeValue(null,AdharDataConstants.AADHAR_VTC_ATTR);
                    landMark = parser.getAttributeValue(null,AdharDataConstants.AADHAR_ADD_LANDMARK);
                    // Post Office
                    postOffice = parser.getAttributeValue(null,AdharDataConstants.AADHAR_PO_ATTR);
                    // district
                    district = parser.getAttributeValue(null,AdharDataConstants.AADHAR_DIST_ATTR);
                    // state
                    state = parser.getAttributeValue(null,AdharDataConstants.AADHAR_STATE_ATTR);
                    // Post Code
                    postCode = parser.getAttributeValue(null,AdharDataConstants.AADHAR_PC_ATTR);

                } else if(eventType == XmlPullParser.END_TAG) {

                } else if(eventType == XmlPullParser.TEXT) {
                    Log.d("PMAY","Text : "+parser.getText());
                }
                // update eventType
                eventType = parser.next();
            }
            if(dob == null) {
                dob = yearOfBirth;
            }
           displayAadharDetails(name, uid, dob, careOf, gender, house_no, street, villageTehsil,
                   postOffice, district, state, postCode, landMark);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void displayAadharDetails(String name, String uid, String dob, String careOf, String gender
    , String house, String street, String villageTehsil, String postOffice, String district, String state, String postCode, String lm) {
        Log.d("PMAY","Text : "+house +" , "+gender +" , "+careOf +", "+street);
        addSurveyRequest = AddSurveyDataManager.getInstance().mAddSurveyRequest;
        String add = "";
        if(house != null) {
            addSurveyRequest.presentHouseNo = house.trim();
            add = house;
        }
        if(street != null) {
            add = add+", "+street;
            //addSurveyRequest.presentHouseNo = add;
        }
        if(house == null && street == null && lm != null) {
            addSurveyRequest.presentHouseNo = lm;
            add = add + lm;
        }
        presentAddr.setText(add);
        add = "";
        if(street != null) {
            add = add+""+street;
            //addSurveyRequest.presentHouseNo = add;
        }
        if(villageTehsil != null) {
            //addSurveyRequest.presentStreetName = villageTehsil;
            add = add+", "+villageTehsil.trim();
        }
        if(postOffice != null) {

            add = add+" "+postOffice.trim();
        }
        if(district != null) {
            add = add+", "+district.trim();

        }
        if(state != null) {
            add = add+", "+state.trim();
        }
        if(postCode != null) {
            add = add+", "+postCode.trim();
        }
        addSurveyRequest.presentStreetName = add;
        city_present.setText(add);

        adhar_num.setText(uid);
        if(adhar_num.getText().length() > 0) {
            idProofSpinner.setSelection(1);
        }
        if(dob != null && dob.trim().length() >=8 ) {
            this.dob.setText(dob);
        }
        this.input_name.setText(name);
        this.husband_name.setText(careOf);
        if("M".equals(gender)) {
            genderSpinner.setSelection(1);
        } else {
            genderSpinner.setSelection(2);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("Housing", " onActivityResult requestCode: "+requestCode +" result "+resultCode +" "+data);
        if(addSurveyDataManager == null) {
            addSurveyDataManager = AddSurveyDataManager.getInstance();
        }
        super.onActivityResult(requestCode, resultCode, data);
        // Applicant photo
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                AddSurveyDataManager.getInstance().mBitmapApplicantOffline = thumbnail;
                Log.v("PMAY", " current phot path : "+thumbnail);
                addSurveyDataManager.applicantPhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.APPLICANT_PHTO, addSurveyDataManager.applicantPhotoFile);
                addSurveyDataManager.setGeoTaggingAttributes(addSurveyDataManager.applicantPhotoFile.getAbsolutePath());
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.GALLERY_REQUEST_IDPROOF) { //IDproof
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(getBaseContext(), "Successfully picked an attachment", Toast.LENGTH_SHORT).show();
                addSurveyDataManager.IdPhotoFile = addSurveyDataManager.getImageFilefromGallery(data.getData(), getApplicationContext());
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to pick an attachment", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.CAMERA_REQUEST_IDPROOF) { //IDproof
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Log.v("PMAY", " current phot path : "+thumbnail);
                addSurveyDataManager.IdPhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.ID_PHTO, addSurveyDataManager.IdPhotoFile);
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again ", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PermissionUtil.GALLERY_REQUEST_OTHER_LAND) { //IDproof
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(getBaseContext(), "Successfully picked an attachment", Toast.LENGTH_SHORT).show();
                addSurveyDataManager.landRecordPhotoFile = addSurveyDataManager.getImageFilefromGallery(data.getData(), getApplicationContext());
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to pick an attachment", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.CAMERA_REQUEST_OTHER_LAND) { //IDproof
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Log.v("PMAY", " current phot path : "+thumbnail);
                addSurveyDataManager.landRecordPhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.LAND_RECORD_PHTO, addSurveyDataManager.landRecordPhotoFile);
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again", Toast.LENGTH_LONG).show();
            }
        } else {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                //we have a result
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                Log.v("PMAY", " scanContent  : "+scanContent );
                Log.v("PMAY", " scanFormat  : "+scanFormat);
                Log.v("PMAY", " scan raw  : "+scanningResult.getRawBytes());
                Log.v("PMAY", " error  : "+scanningResult.getErrorCorrectionLevel());

                if(scanFormat != null && !scanFormat.trim().equals("QR_CODE")) {
                    Toast.makeText(getBaseContext(), "Unable to scan your Aadhar. Please try again", Toast.LENGTH_SHORT).show();
                }
                if(data != null) {
                    Log.v("PMAY", " scanFormat extras  : "+data.getExtras());
                }

                // process received data
                if(scanContent != null && !scanContent.isEmpty()){
                    processScannedData(scanContent);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Scan Cancelled", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void checkGPSOption(){ //BISW27Mar18:
        // Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!Constants.ENABLE_CHEAT_MODE && (lm != null && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            // Build the alert dialog
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                    R.style.AppTheme_Dark_Dialog);
            alertDialog.setTitle("Location Services or Device Mode for GPS Not Active");
            alertDialog.setMessage("Enable Location Services from the settings. Please make sure you are selecting the Device only option under Mode.");

            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            alertDialog.show();
        } else {
            checkGPSPermissions();
        }
    }

    public void onContinueClicked(View v) {
        validateRequestFileds(false);
        createUploadRequest(false);
        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
        startActivity(intent);
    }

    public void onSubmitClicked(View v) {
        /*if(!isWiFiDATAConnected()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                    R.style.AppTheme_Dark_Dialog);
            alertDialog.setTitle("Offline");
            alertDialog.setMessage(R.string.offline_save_option);

            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    return;
                }
            });

            alertDialog.show();
            //Toast.makeText(getApplicationContext(), "Internet connection is mandatory for submit data. Please make sure you save the data using Save button.", Toast.LENGTH_SHORT).show();
            return;
        }*/

        boolean isValid = validateRequestFileds(true);
        //Remove
        if(!AddSurveyDataManager.IS_BIOMETRIC_RESTRICTED) {
            if (addSurveyDataManager.biometricDetails == null || addSurveyDataManager.slumBiometricDetails == null) {
                addSurveyDataManager.biometricDetails = new byte[1];
                addSurveyDataManager.slumBiometricDetails = new byte[1];
            }
        }
        if(!isValid) {
            Toast.makeText(getApplicationContext(), "Please add necessary information before submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        }  else if (ulb_dropdown.getSelectedItemPosition() == 0) { //BISW25Mar18:02 Don't allow to save/ edit in case ULB name is blank
            Toast.makeText(getApplicationContext(), "Please enter ULB name before submitting Survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if (ward_detail_info.getText().toString().length() == 0) { //BISW27Mar18:02 Don't allow to save/ edit in case Ward number is blank
            Toast.makeText(getApplicationContext(), "Please enter Ward number before saving Survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if(addSurveyDataManager.applicantPhotoFile == null && addSurveyRequest.isNewRecord) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Applicant photo  is required for submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (input_name.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Name of the Head of the Family(Beneficiary Name) is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        }  else if (husband_name.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Father's/Husband name is required for survey data", Toast.LENGTH_SHORT).show();
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            return;
        } else if (genderSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Beneficiary Gender is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.adharNo == null && addSurveyRequest.slumAdharNo == null) {
            Toast.makeText(getApplicationContext(), "Please enter Aadhar or other ID Number before submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (adhar_num.getText().toString().length() < 12){
            Toast.makeText(getApplicationContext(), "Aadhar number is less than 12, please re-enter.", Toast.LENGTH_SHORT).show();
            return;
        } else if (dob.getText().toString().length() <= 2){
            Toast.makeText(getApplicationContext(), "Please enter Date of birth before submitting survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if((!"S".equals(addSurveyRequest.chckSlumRadio))) { //BISW27Mar18:
            if (addSurveyDataManager.missionSpinnerSaved == 0) { //BISW27Mar18: Need to check if misson component is selected or not
                Toast.makeText(getBaseContext(), "Please enter Preferred component of mission before submitting Survey data.", Toast.LENGTH_SHORT).show();
                return;
            } else if (addSurveyDataManager.presentHousePhotoFile == null && addSurveyRequest.isNewRecord) {
                 if (mProgressDialog != null) {
                     mProgressDialog.dismiss();
                 }
                 Toast.makeText(getBaseContext(), "Present house photo is required for survey data.", Toast.LENGTH_SHORT).show();
                 return;
             }
        } else if(addSurveyDataManager.slumBiometricDetails == null && addSurveyRequest.isNewRecord) {
            //addSurveyDataManager.slumBiometricDetails = new byte[1];
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if(!Constants.ENABLE_CHEAT_MODE) {
                Toast.makeText(getBaseContext(), "Biometric Thumb impression is required for saving or submitting survey data", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if(reason_non_eligible != null && reason_non_eligible.getVisibility() == View.VISIBLE
                && reason_non_eligible.getText().length() < 8) {
            Toast.makeText(getApplicationContext(), "Please enter valid reason for non-eligibility before saving survey data.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isWiFiDATAConnected()) {
            mProgressDialog = new ProgressDialog(this,
                    R.style.AppTheme_Dark_Dialog);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Submitting ...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            submitConfirmDialog();
        } else {
            // offline submit
            createUploadRequest(true);
            sendAddSurveyReq(true);
        }

    }

    ProgressDialog progressDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        AddSurveyDataManager.getInstance().stopLocationUpdates();
        Log.v("PMAY", " MainActivity: onDestroy called - may be logged out ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home) {
            finish();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

}
