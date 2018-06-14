package com.sourcey.housingdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.telephony.TelephonyManager;
//import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.pm.yojana.housingdemo.R;
import com.sourcey.housingdemo.modal.PmayDatabaseHelper;
import com.sourcey.housingdemo.modal.SurveyDataModal;
import com.sourcey.housingdemo.restservice.APIClient;
import com.sourcey.housingdemo.restservice.APIInterface;
import com.sourcey.housingdemo.restservice.AddSurveyRequest;
import com.sourcey.housingdemo.restservice.AddSurveyResponse;
import com.sourcey.housingdemo.utils.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity {
    EditText permanentAdd, city_parmanent, phone2, presentAddr;
    EditText family_name, relation, age, id_no;

    EditText acc_no, bank, branch, ifsc_code, reason_non_eligible;
    AppCompatCheckBox isSameAdd, other_land_chkbox, eligible_chk_box;

    TextInputLayout addrs_layout, city_layout, loc_layout, land_size_layout;

    EditText other_house, land_size, dwelling_size, house_req_details;
    AppCompatButton other_house_proof, save_btn, submitBtn, btnPhoto, btnThumbImpression;
    PermissionUtil permissionUtil;

    AppCompatSpinner spinner_ownership, house_wall_type_spinner, house_type_spinner, house_req_spinner, emp_status,
            bank_spinner, missionSpinner;

    EditText name_pattadar, patta_no, dag_no, land_area, land_length, land_breadth;
    EditText income, bpl_num, ration_num;
    AppCompatCheckBox  bplchk, ration_cardchk, house_uploaded_chk;
    PmayDatabaseHelper mPmayDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        permissionUtil = new PermissionUtil();

        mPmayDatabaseHelper = new PmayDatabaseHelper(this);

        spinner_ownership = (AppCompatSpinner)findViewById(R.id.spinner_ownership);
        house_wall_type_spinner = (AppCompatSpinner)findViewById(R.id.house_wall_type_spinner);
        house_type_spinner = (AppCompatSpinner)findViewById(R.id.house_type_spinner);
        missionSpinner  = (AppCompatSpinner)findViewById(R.id.mission);

        house_req_spinner = (AppCompatSpinner)findViewById(R.id.house_req_spinner);
        emp_status = (AppCompatSpinner)findViewById(R.id.emp_status);

        bank_spinner = (AppCompatSpinner)findViewById(R.id.bank_spinner);
        bpl_num = (EditText) findViewById(R.id.bpl_num);

        income = (EditText) findViewById(R.id.income);
        ration_num = (EditText) findViewById(R.id.ration_num);

        house_uploaded_chk = (AppCompatCheckBox) findViewById(R.id.upload_house_status);
        bplchk = (AppCompatCheckBox) findViewById(R.id.bpl);
        final AppCompatButton bplBtn = (AppCompatButton)findViewById(R.id.button_scan_bpl);
        final AppCompatButton rationBtn= (AppCompatButton)findViewById(R.id.button_scan_ration);

        bplchk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bpl_num.setVisibility(View.VISIBLE);
                    bplBtn.setVisibility(View.VISIBLE);
                } else {
                    bpl_num.setVisibility(View.GONE);
                    bplBtn.setVisibility(View.GONE);
                }

            }
        });
         ration_cardchk = (AppCompatCheckBox) findViewById(R.id.ration_card);
        ration_cardchk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ration_num.setVisibility(View.VISIBLE);
                    rationBtn.setVisibility(View.VISIBLE);
                } else {
                    ration_num.setVisibility(View.GONE);
                    rationBtn.setVisibility(View.GONE);
                }

            }
        });

        name_pattadar = (EditText) findViewById(R.id.name_pattadar);
        patta_no = (EditText) findViewById(R.id.patta_no);
        dag_no = (EditText) findViewById(R.id.dag_no);
        land_area = (EditText) findViewById(R.id.land_area);
        land_length = (EditText) findViewById(R.id.land_length);
        land_breadth = (EditText) findViewById(R.id.land_breadth);

        dwelling_size = (EditText) findViewById(R.id.dwelling_size);
        house_req_details = (EditText) findViewById(R.id.house_req_details);

        permanentAdd = (EditText) findViewById(R.id.permanent_address);
        city_parmanent = (EditText) findViewById(R.id.city_parmanent);
        phone2 = (EditText) findViewById(R.id.phone2);

        addrs_layout = (TextInputLayout) findViewById(R.id.addrs_layout);
        city_layout = (TextInputLayout) findViewById(R.id.city_layout);

        presentAddr = (EditText) findViewById(R.id.present_address);

        loc_layout = (TextInputLayout) findViewById(R.id.loc_layout);
        land_size_layout = (TextInputLayout) findViewById(R.id.land_size_layout);
        other_house = (EditText) findViewById(R.id.other_house);
        land_size = (EditText) findViewById(R.id.land_size);
        other_house_proof = (AppCompatButton) findViewById(R.id.other_house_proof);

        family_name = (EditText) findViewById(R.id.family_name);
        relation = (EditText) findViewById(R.id.relation);
        age = (EditText) findViewById(R.id.age);
        id_no = (EditText) findViewById(R.id.id_no);

        acc_no = (EditText) findViewById(R.id.acc_no);
        bank = (EditText) findViewById(R.id.branch);
        branch = (EditText) findViewById(R.id.branch);
        ifsc_code = (EditText) findViewById(R.id.ifsc_code);

        save_btn = (AppCompatButton) findViewById(R.id.submit);
        submitBtn = (AppCompatButton) findViewById(R.id.cancel);

        eligible_chk_box = (AppCompatCheckBox) findViewById(R.id.eligibility);
        reason_non_eligible = (EditText)findViewById(R.id.reason_non_eligible);
        addSurveyDataManager = AddSurveyDataManager.getInstance();
        mPermissionUtil = new PermissionUtil();

        btnPhoto = (AppCompatButton) findViewById(R.id.photo_house);
        btnThumbImpression = (AppCompatButton) findViewById(R.id.thumb_impressiion);

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
        isSameAdd = (AppCompatCheckBox) findViewById(R.id.same_address_chkbox);

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

        eligible_chk_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    reason_non_eligible.setText("");
                    reason_non_eligible.setVisibility(View.GONE);

                } else {
                    reason_non_eligible.setVisibility(View.VISIBLE);

                }
            }
        });

        if(AddSurveyDataManager.getInstance().missionSpinnerSaved >= 0) {
            missionSpinner.setSelection(AddSurveyDataManager.getInstance().missionSpinnerSaved);
        }
        eligible_chk_box.setChecked(AddSurveyDataManager.getInstance().eligibleSaved);
        if(AddSurveyDataManager.getInstance().eligibleReasonSaved != null) {
            reason_non_eligible.setText(AddSurveyDataManager.getInstance().eligibleReasonSaved);
        }
        showRecordToEdit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(missionSpinner.getSelectedItemPosition() >= 0) {
            AddSurveyDataManager.getInstance().missionSpinnerSaved = missionSpinner.getSelectedItemPosition();
        }
        AddSurveyDataManager.getInstance().eligibleSaved = eligible_chk_box.isChecked();
        AddSurveyDataManager.getInstance().eligibleReasonSaved = reason_non_eligible.getText().toString();
    }

    AddSurveyRequest mAddSurveyEditRecord;
    void showRecordToEdit() {
        mAddSurveyEditRecord = AddSurveyDataManager.getInstance().mAddSurveyRequest;
        if(mAddSurveyEditRecord != null && !mAddSurveyEditRecord.isNewRecord) {
            Log.v("PMAY", "Edit record "+ mAddSurveyEditRecord.isNewRecord +" housepic "+mAddSurveyEditRecord.isHousePicUploaded +" "+mAddSurveyEditRecord.eligibleStatus);
            Log.v("PMAY", "Edit record ");
            if(mAddSurveyEditRecord.isHousePicUploaded) {
                house_uploaded_chk.setChecked(true);
            } else {
                house_uploaded_chk.setChecked(false);
            }
            if("Y".equals(mAddSurveyEditRecord.isSubmitted)) {
                save_btn.setEnabled(false);
                save_btn.setTextColor(getResources().getColor(R.color.iron));
                submitBtn.setEnabled(false);
                submitBtn.setTextColor(getResources().getColor(R.color.iron));

                btnPhoto.setEnabled(false);
                btnPhoto.setTextColor(getResources().getColor(R.color.iron));
                btnThumbImpression.setEnabled(false);
                btnThumbImpression.setTextColor(getResources().getColor(R.color.iron));

            } else {
                save_btn.setEnabled(true);
                submitBtn.setEnabled(true);
                submitBtn.setTextColor(getResources().getColor(R.color.white));
                save_btn.setTextColor(getResources().getColor(R.color.white));

                btnPhoto.setEnabled(true); //BISW24Mar18:
                btnThumbImpression.setEnabled(true); //BISW24Mar18:
                btnPhoto.setTextColor(getResources().getColor(R.color.white)); //BISW24Mar18:
                btnThumbImpression.setTextColor(getResources().getColor(R.color.white)); //BISW24Mar18:
            }

            if(mAddSurveyEditRecord.ownershipHouse != null) {
                spinner_ownership.setSelection(Integer.parseInt(mAddSurveyEditRecord.ownershipHouse));
            }
            if(mAddSurveyEditRecord.houseRoofType != null) {
                house_type_spinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.houseRoofType));
            }
            if(mAddSurveyEditRecord.houseWallType != null) {
                house_wall_type_spinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.houseWallType));
            }
            if(mAddSurveyEditRecord.houseRequirementRadio != null) {
                house_req_spinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.houseRequirementRadio));
            }
            if(mAddSurveyEditRecord.employmentStatus != null) {
                emp_status.setSelection(Integer.parseInt(mAddSurveyEditRecord.employmentStatus));
            }
            if(mAddSurveyEditRecord.preferredAssistanceHfa != null) {
                missionSpinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.preferredAssistanceHfa));
            }
            if(mAddSurveyEditRecord.bankName != null) {
                bank_spinner.setSelection(Integer.parseInt(mAddSurveyEditRecord.bankName));
            }

            if(mAddSurveyEditRecord.sizeExistingDwelling != null) {
                dwelling_size.setText(mAddSurveyEditRecord.sizeExistingDwelling);
            }
            if(mAddSurveyEditRecord.requirement != null) {
                house_req_details.setText(mAddSurveyEditRecord.requirement);
            }
            if(mAddSurveyEditRecord.pattadarName != null) {
                name_pattadar.setText(mAddSurveyEditRecord.pattadarName);
            }
            if(mAddSurveyEditRecord.pattaNo != null) {
                patta_no.setText(mAddSurveyEditRecord.pattaNo);
            }

            if(mAddSurveyEditRecord.dagNo != null) {
                dag_no.setText(mAddSurveyEditRecord.dagNo);
            }
            if(mAddSurveyEditRecord.landAreaPatta != null) {
                land_area.setText(mAddSurveyEditRecord.landAreaPatta);
            }
            if(mAddSurveyEditRecord.landBreadth != null) {
                land_breadth.setText(mAddSurveyEditRecord.landBreadth);
            }
            if(mAddSurveyEditRecord.landLength != null) {
                land_length.setText(mAddSurveyEditRecord.landLength);
            }
            if(mAddSurveyEditRecord.averageIncome != null) {
                income.setText(mAddSurveyEditRecord.averageIncome);
            }
            if(mAddSurveyEditRecord.bplRadio != null && mAddSurveyEditRecord.bplRadio.equals("Y")) {
                bplchk.setChecked(true);
            } else {
                bplchk.setChecked(false);
            }
            if(mAddSurveyEditRecord.bplNo != null ) {
                bpl_num.setText(mAddSurveyEditRecord.bplNo);
            }

            if(mAddSurveyEditRecord.rationRadio != null && mAddSurveyEditRecord.rationRadio.equals("Y")) {
                ration_cardchk.setChecked(true);
            } else {
                ration_cardchk.setChecked(false);
            }
            if(mAddSurveyEditRecord.rationCardNo != null ) {
                ration_num.setText(mAddSurveyEditRecord.rationCardNo);
            }

            if(mAddSurveyEditRecord.bankAccNo != null ) {
                acc_no.setText(mAddSurveyEditRecord.bankAccNo);
            }
            if(mAddSurveyEditRecord.bankBranchName != null ) {
                branch.setText(mAddSurveyEditRecord.bankBranchName);
            }
            if(mAddSurveyEditRecord.bankIfscCode != null ) {
                ifsc_code.setText(mAddSurveyEditRecord.bankIfscCode);
            }

            if(mAddSurveyEditRecord.eligibleStatus != null && mAddSurveyEditRecord.eligibleStatus.equals("Y")) {
                eligible_chk_box.setChecked(true);
            } else {
                eligible_chk_box.setChecked(false);
            }
            if(mAddSurveyEditRecord.nonEligibleReason != null ) {
                reason_non_eligible.setText(mAddSurveyEditRecord.nonEligibleReason);
            }

        }
    }

    void updatedSurveyDataToDB(String surveyId) {

        SurveyDataModal record = new SurveyDataModal();
        Intent intent = new Intent("com.pmay.FINISH_ACTIVITY");
        sendBroadcast(intent);
        if(addSurveyRequest.familyHeadName != null)
            record.mName = addSurveyRequest.familyHeadName;
        if(surveyId != null)
            record.mSurveyId = surveyId;
        if(addSurveyRequest.adharNo != null)
            record.mAdharNo = addSurveyRequest.adharNo;
        if(addSurveyRequest.fatherHusbandName != null)
            record.mFatherName = addSurveyRequest.fatherHusbandName;
        if(addSurveyRequest.bankAccNo != null)
            record.mBankAccNo = addSurveyRequest.bankAccNo;
        if(addSurveyRequest.slumRadio != null)
            record.mSlum = addSurveyRequest.slumRadio;

        record.isSubmitted = addSurveyRequest.isSubmitted;
        addSurveyRequest.surveyId = surveyId;
        Log.v("PMAY", "Record : "+record.mAdharNo  +", "+addSurveyRequest.slumAdharNo +" , "+addSurveyRequest.adharNo);
        addSurveyRequest.surveyId = surveyId;
        if(updateDuplicateRecords(addSurveyRequest.adharNo)) {
            mPmayDatabaseHelper.updateUser(addSurveyRequest, true);
        } else {
            mPmayDatabaseHelper.addUser(addSurveyRequest);
        }
        AddSurveyDataManager.getInstance().surveyDataModals.add(record);
    }

    void updatedEditedRecordList(String surveyId) {
        List recordsList =  AddSurveyDataManager.getInstance().surveyDataModals;
        Log.v("PMAY", " before Removed record - size : "+recordsList.size());
        if(recordsList.size() > 0 && !recordsList.isEmpty()) {
            Iterator<SurveyDataModal> itr = recordsList.iterator();
            while (itr.hasNext()) {
                SurveyDataModal surveyDataModal = itr.next();
                if(surveyDataModal.mSurveyId.equals(surveyId)) {
                    itr.remove();
                    Log.v("PMAY", " Removed record : "+surveyDataModal.mSurveyId);
                    //break;
                }
            }
        }
        Log.v("PMAY", " Removed record - size : "+AddSurveyDataManager.getInstance().surveyDataModals.size());
    }

    void addSurveyDataToDB(String surveyId) {
        Intent intent = new Intent("com.pmay.FINISH_ACTIVITY");
        sendBroadcast(intent);
        SurveyDataModal record = new SurveyDataModal();
        if(addSurveyRequest.familyHeadName != null)
            record.mName = addSurveyRequest.familyHeadName;
        if(surveyId != null)
            record.mSurveyId = surveyId;
        if(addSurveyRequest.adharNo != null)
            record.mAdharNo = addSurveyRequest.adharNo;
        if(addSurveyRequest.fatherHusbandName != null)
            record.mFatherName = addSurveyRequest.fatherHusbandName;
        if(addSurveyRequest.bankAccNo != null)
            record.mBankAccNo = addSurveyRequest.bankAccNo;
        if(addSurveyRequest.slumRadio != null)
            record.mSlum = addSurveyRequest.slumRadio;

        record.isSubmitted = addSurveyRequest.isSubmitted;
        addSurveyRequest.surveyId = surveyId;
        mPmayDatabaseHelper.addUser(addSurveyRequest);
        AddSurveyDataManager.getInstance().surveyDataModals.add(record);
    }

    public  void scanFingerprint(View v) {
        Intent intent = new Intent(getApplicationContext(), FingerPrintScannerActivity.class);
        startActivity(intent);
    }

    boolean isWiFiDATAConnected() {
        /*final ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telMgr = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected() && telMgr.getNetworkType() != TelephonyManager.NETWORK_TYPE_EDGE)) {
            return  true;
        }*/
        if(CommonUtils.isNetworkAvailable(this) && !CommonUtils.is2GNetwork(this)) {
            return true;
        }
        return false;
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

        boolean isValid = validateRequestFileds(false);
        if(!AddSurveyDataManager.IS_BIOMETRIC_RESTRICTED) {
            if (addSurveyDataManager.biometricDetails == null) {
                addSurveyDataManager.biometricDetails = new byte[1];
                //addSurveyDataManager.slumBiometricDetails = new byte[1];
            }
        }
        if(!isValid) {
            Toast.makeText(getApplicationContext(), "Please add necessary information before submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        }  else if (addSurveyRequest.ulbNameId == null) { //BISW25Mar18:02 Don't allow to save/ edit in case ULB name is blank
            Toast.makeText(getApplicationContext(), "Please enter ULB name before submitting Survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.familyHeadName == null || addSurveyRequest.familyHeadName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Name of the Head of the Family(Beneficiary Name) is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.fatherHusbandName == null || addSurveyRequest.fatherHusbandName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Father's/Husband name is required for survey data", Toast.LENGTH_SHORT).show();
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            return;
        } else if (addSurveyRequest.genderId == null || addSurveyRequest.genderId.equals("0")) {
            Toast.makeText(getApplicationContext(), "Beneficiary Gender is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.adharNo == null) {
            Toast.makeText(getApplicationContext(), "Please enter Aadhar Number or other ID Number before submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        }  else if (addSurveyRequest.adharNo.length() < 12){
            Toast.makeText(getApplicationContext(), "Aadhar number is less than 12, please re-enter.", Toast.LENGTH_SHORT).show();
            return;
        }  else if (addSurveyRequest.dob == null || addSurveyRequest.dob.length() <= 2){
            Toast.makeText(getApplicationContext(), "Please enter Date of birth before submitting survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if (missionSpinner.getSelectedItemPosition() == 0) { //BISW27Mar18: Need to validate the values before submitting
            Toast.makeText(getApplicationContext(), "Please enter Preferred component of mission before submitting Survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if(addSurveyDataManager.applicantPhotoFile == null && addSurveyRequest.isNewRecord) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Applicant photo  is required for submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if(addSurveyRequest.isHousePicUploaded  == false) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Present house photo is required for submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if(addSurveyDataManager.biometricDetails  == null && addSurveyRequest.isNewRecord) {
            //addSurveyDataManager.biometricDetails = new byte[1];
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Biometric Thumb impression is required for saving or submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isWiFiDATAConnected()) {
            mProgressDialog = new ProgressDialog(this,
                    R.style.AppTheme_Dark_Dialog);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Submitting ...");
            mProgressDialog.show();
            submitConfirmDialog();
        } else {
            createUploadRequest(true);
        }
    }

    void createUploadRequest(final boolean isSubmitted) {

        if(!AddSurveyDataManager.IS_BIOMETRIC_RESTRICTED) {
            if (addSurveyDataManager.biometricDetails == null) {
                addSurveyDataManager.biometricDetails = new byte[1];
            }
        }
        if(addSurveyDataManager == null)
            addSurveyDataManager = AddSurveyDataManager.getInstance();

        addSurveyDataManager.mAttachments.clear();
        List<MultipartBody.Part> attachments = addSurveyDataManager.mAttachments;

        addSurveyDataManager.isEdited = true;

       // RequestBody description = RequestBody.create(MultipartBody.FORM, "surveyData");
        if( addSurveyDataManager.presentHousePhotoFile != null) {
            addSurveyRequest.isHousePicUploaded = true;
            addSurveyDataManager.setGeoTaggingAttributes(addSurveyDataManager.presentHousePhotoFile.getAbsolutePath());
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.presentHousePhotoFile, AddSurveyDataManager.PRESENT_HOUSE_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.presentHousePhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.presentHousePhotoFile, AddSurveyDataManager.LOCATION_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.applicantPhotoFile != null) {
            addSurveyDataManager.setGeoTaggingAttributes(addSurveyDataManager.applicantPhotoFile.getAbsolutePath());
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.applicantPhotoFile, AddSurveyDataManager.APPLICANT_PHTO);
            attachments.add(applicantImage);
        }
       /* if( addSurveyDataManager.landRecordPhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.landRecordPhotoFile, AddSurveyDataManager.LAND_RECORD_PHTO);
            attachments.add(applicantImage);
        }*/
        if( addSurveyDataManager.land1PhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.land1PhotoFile, AddSurveyDataManager.LAND1_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.land2PhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.land2PhotoFile, AddSurveyDataManager.LAND2_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.bplPhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.bplPhotoFile, AddSurveyDataManager.BPL_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.rationPhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.rationPhotoFile, AddSurveyDataManager.RATIONCD_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.signaturePhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.signaturePhotoFile, AddSurveyDataManager.SIGNATURE_PHTO);
            attachments.add(applicantImage);
        }
        if( addSurveyDataManager.incomePhotoFile != null) {
            MultipartBody.Part applicantImage = addSurveyDataManager.getMultiPartFile(addSurveyDataManager.incomePhotoFile, AddSurveyDataManager.INCOME_PHTO);
            attachments.add(applicantImage);
        }
        Log.v("PMAY", " Size of the attachments "+attachments.size());

        if(!isWiFiDATAConnected()) {
            //Offline
            if(!addSurveyRequest.isNewRecord && "Y".equals(addSurveyRequest.isSubmitted)) {
                //Toast.makeText(getBaseContext(), "Survey data can not be edited in offline mode", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, //BISW27Mar18: Needs to show a dialog rather toast
                        R.style.AppTheme_Dark_Dialog);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Survey data can not be edited ");
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
            }   else if (addSurveyRequest.ulbNameId == null) { //BISW25Mar18:02 Don't allow to save/ edit in case ULB name is blank
                Toast.makeText(getApplicationContext(), "Please enter ULB name before saving Survey data.", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            } else if (addSurveyRequest.familyHeadName == null || addSurveyRequest.familyHeadName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Name of the Head of the Family(Beneficiary Name) is required for survey data", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            }   else if (addSurveyRequest.genderId == null || addSurveyRequest.genderId.equals("0")) {
                Toast.makeText(getApplicationContext(), "Beneficiary Gender is required for survey data", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            } else if (addSurveyRequest.adharNo == null && addSurveyRequest.slumAdharNo == null) {
                Toast.makeText(getApplicationContext(), "Please enter Aadhar or other ID Number before Saving survey data", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            } else if (addSurveyRequest.adharNo != null && addSurveyRequest.adharNo.length() < 12){
                Toast.makeText(getApplicationContext(), "Aadhar number is less than 12, please re-enter.", Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                return;
            }  else if (addSurveyRequest.dob == null || addSurveyRequest.dob.length() <= 2){
                Toast.makeText(getApplicationContext(), "Please enter Date of birth before saving survey data.", Toast.LENGTH_SHORT).show();
                return;
            } else if(addSurveyDataManager.applicantPhotoFile == null && addSurveyRequest.isNewRecord) {
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(getBaseContext(), "Applicant photo  is required for survey data", Toast.LENGTH_SHORT).show();
                return;
            } /*else if(isSubmitted && addSurveyDataManager.presentHousePhotoFile == null && addSurveyRequest.isNewRecord) {
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(getBaseContext(), "Present house photo is required for survey data", Toast.LENGTH_SHORT).show();
                return;
            }*/ else if(addSurveyDataManager.biometricDetails == null && addSurveyRequest.isNewRecord) {
                //addSurveyDataManager.biometricDetails = new byte[1];
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(getBaseContext(), "Biometric Thumb impression is required for saving or submitting survey data", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!isSubmitted) {
                if(addSurveyRequest.surveyId != null /*&& addSurveyRequest.surveyId.equals("0")*/) {
                    //record.isSubmitted = "O";
                    //addSurveyRequest.surveyId = surveyId;
                    addSurveyRequest.isSubmitted = "O";
                    //addSurveyRequest.isSubmittedFlag = "O";
                }

            }  else {
                //Toast.makeText(this, "Please check your internet connections", Toast.LENGTH_LONG).show();
                //offline submit
                addSurveyRequest.isSubmitted = "OSB";
                addSurveyRequest.isSubmittedFlag = "OSB";
                addSurveyRequest.validationPendingStatus = "N";
            } /*else {
                Toast.makeText(this, "Please check your internet connections", Toast.LENGTH_LONG).show();
            }*/
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            showSaveDialogForOfflineRecords(true);
            return;
        }

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
        }

        //Call<ResponseBody> callUpload =  apiInterface.uploadSurveyData(description, applicantImage, addSurveyRequest);
        //addSurveyRequest.surveyId = "448825";
        Call<AddSurveyResponse> callUpload;
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        if(addSurveyDataManager.biometricDetails  == null && addSurveyRequest.isNewRecord) {
            //addSurveyDataManager.biometricDetails = new byte[1];
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Biometric Thumb impression is required for saving or submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        }
        /*if(addSurveyRequest.isHousePicUploaded == false) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Present house photo  is required for submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        }*/
        if(isSubmitted && addSurveyDataManager.applicantPhotoFile == null && addSurveyRequest.isNewRecord) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Applicant photo  is required for submitting survey data", Toast.LENGTH_SHORT).show();
            return;
        }
        if(addSurveyDataManager.biometricDetails != null) {
            RequestBody description = RequestBody.create(MultipartBody.FORM, addSurveyDataManager.biometricDetails);
            callUpload =  apiInterface.uploadMultiFilesSurveyData(description, attachments, addSurveyRequest);
        } else {
            callUpload =  apiInterface.uploadMultiFilesSurveyData(attachments, addSurveyRequest);
        }

        callUpload.enqueue(new Callback<AddSurveyResponse>() {

            @Override
            public void onResponse(Call<AddSurveyResponse> call, Response<AddSurveyResponse> response) {
                Log.d("PMAY","UPLOAD request : "+response);
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
                        resetAll();
                    }
                    displayUploadResponseDialog(isSubmitted, response.body().success, response.body().surveyId);
                } else {
                    //displayUploadResponseDialog(isSubmitted, false);
                    Log.v("PMAY", " onFailureResp - 4th screen " +response);
                    if(!isSubmitted) {
                        //showSaveDialogForOfflineRecords(isSubmitted);
                        displayUploadResponseDialog(false, false, "");
                        return;
                    } else {
                        displayUploadResponseDialog(false, false, "");
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<AddSurveyResponse> call, Throwable t) {
                if( mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                displayUploadResponseDialog(false, false, "");
                return;
            }
        });
    }

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

    void showSaveDialogForOfflineRecords(boolean isSubmitted) {
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
                    return;
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
//            alertDialog.setMessage(getResources().getString(R.string.save_message_offline)+" with Aadhar Number <" + adhar_no + ">.");
            msg += " with Aadhar Number <" + adhar_no + ">.";
            /*if(isOffline) {
                alertDialog.setMessage(getResources().getString(R.string.save_message_no_network)+" with Aadhar Number <" + adhar_no + ">");
            }*/
        } else {
//            alertDialog.setMessage(R.string.save_message_offline);
           /* if(isOffline) {
                alertDialog.setMessage(R.string.save_message_no_network);
            }*/
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

        /* //BISW27Mar18: Need not to show cancel button alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });*/
        alertDialog.show();
    }
    boolean isUpdated = false;

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
                           // addSurveyRequest.isSubmittedFlag = "O"; // Non submitted saved records in offline
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
        Intent intent = new Intent("com.pmay.FINISH_ACTIVITY");
        sendBroadcast(intent);
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
        record.mSurveyId = addSurveyRequest.surveyId;

        //Save lat and long values in to bpl and ration card numbers for offline
        if(!"0".equals(addSurveyRequest.geoLatitude))
            addSurveyRequest.bplNo = addSurveyRequest.geoLatitude;
        if(!"0".equals(addSurveyRequest.geoLongitude))
            addSurveyRequest.rationCardNo = addSurveyRequest.geoLongitude;

        //Added 28MarBiswajit
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            record.mAdharNo = addSurveyRequest.slumAdharNo;
        } else {
            record.mAdharNo = addSurveyRequest.adharNo;
            record.isSubmitted = addSurveyRequest.isSubmitted;
        }
        if(AddSurveyDataManager.getInstance().mBitmapApplicantOffline != null) {
            AddSurveyDataManager.getInstance().storeOfflineImage(this, AddSurveyDataManager.getInstance().mBitmapApplicantOffline, record.mAdharNo, AddSurveyDataManager.APPLICANT_PHTO);
        }
        if(AddSurveyDataManager.getInstance().mBitmapHousePicOffline != null) {
            addSurveyRequest.isHousePicUploaded = true;
            AddSurveyDataManager.getInstance().storeOfflineImage(this, AddSurveyDataManager.getInstance().mBitmapHousePicOffline, record.mAdharNo, AddSurveyDataManager.PRESENT_HOUSE_PHTO);
        }
        if(AddSurveyDataManager.getInstance().mBitmapFingerPrintOffline != null) {
            AddSurveyDataManager.getInstance().storeOfflineImage(this, AddSurveyDataManager.getInstance().mBitmapFingerPrintOffline, record.mAdharNo, AddSurveyDataManager.SIGNATURE_PHTO);
        }

        if(addSurveyRequest.bankAccNo != null)
            record.mBankAccNo = addSurveyRequest.bankAccNo;

        if("OSB".equals(addSurveyRequest.isSubmitted) || "OSB".equals(addSurveyRequest.isSubmittedFlag))
            record.isSubmitted = "OSB";
        else
            record.isSubmitted = "O";


        if(updateDuplicateRecordsInOffline(record.mAdharNo)) {
            //duplicate
            //mPmayDatabaseHelper.updateUser(addSurveyRequest, true);
            return;
        } if(!isUpdated) {
            mPmayDatabaseHelper.addUser(addSurveyRequest);
        }  else {
            record.mSurveyId = addSurveyRequest.surveyId; // for saved offline records before
        }
        AddSurveyDataManager.getInstance().surveyDataModals.add(record);
    }

    boolean updateDuplicateRecords(String adhar1) {
        boolean ret = false;
        List recordsList =  AddSurveyDataManager.getInstance().surveyDataModals;
        if("S".equals(addSurveyRequest.chckSlumRadio)) {
            adhar1 = addSurveyRequest.slumAdharNo;
        } else {
            adhar1 = addSurveyRequest.adharNo;
        }
        Log.v("PMAY", " before Removed record - size : "+recordsList.size());
        if(recordsList.size() > 0 && !recordsList.isEmpty()) {
            Iterator<SurveyDataModal> itr = recordsList.iterator();
            while (itr.hasNext()) {
                SurveyDataModal surveyDataModal = itr.next();
                if(surveyDataModal.mAdharNo != null && surveyDataModal.mAdharNo.equals(adhar1)) {
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

    void displayUploadResponseDialog( boolean isSubmitted,final boolean isSuccess, String surveyID) {
        if(addSurveyRequest.isHousePicUploaded) {
            house_uploaded_chk.setChecked(true);
        } else {
            house_uploaded_chk.setChecked(false);
        }
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        alertDialog.setTitle("Success");
        //alertDialog.setIndeterminate(true);
        String lDisplayMsg = "";
        if(isSubmitted) {
            if(isSuccess) {
                //lDisplayMsg = "Survey data has been submitted successfully with Survey ID = " + surveyID.toString();
                lDisplayMsg = getResources().getString(R.string.submit_message_success) + " with survey ID = " + surveyID.toString();
                alertDialog.setMessage(lDisplayMsg);
            }
            //alertDialog.setMessage(R.string.submit_message_success);
            else {
                alertDialog.setMessage(R.string.submit_message_fail);
                alertDialog.setTitle("Error");            }
        } else {
            if(isSuccess)
            {
                lDisplayMsg = getResources().getString(R.string.save_message_success) +" with survey ID = "+  surveyID.toString();
                alertDialog.setMessage(lDisplayMsg);
                //alertDialog.setMessage(R.string.save_message_success);
            }
            else {
                alertDialog.setMessage("The Aadhar number given, already present in the records, please use a different Aadhar number for Survey.");
                alertDialog.setTitle("Error");
            }
        }
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dis
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
        alertDialog.setMessage(R.string.submit_non_slum_message);
        //alertDialog.setIndeterminate(true);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addSurveyRequest.validationPendingStatus = "Y";
                addSurveyRequest.isSubmitted = "N";
                createUploadRequest(false);
            }
        });

        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addSurveyRequest.validationPendingStatus = "N";
                addSurveyRequest.isSubmitted = "Y";
                createUploadRequest(true);
            }
        });
        alertDialog.show();
    }

    ProgressDialog mProgressDialog;

    public void onSaveClicked(View v) {
        boolean isValid = validateRequestFileds(false);
        if(!isValid) {
            Toast.makeText(getApplicationContext(), "Please add necessary information before saving survey data ", Toast.LENGTH_SHORT).show();
            return;
        }  else if (addSurveyRequest.ulbNameId == null) { //BISW25Mar18:02 Don't allow to save/ edit in case ULB name is blank
            Toast.makeText(getApplicationContext(), "Please enter ULB name before saving Survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.familyHeadName == null || addSurveyRequest.familyHeadName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Name of the Head of the Family(Beneficiary Name) is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        }else if (addSurveyRequest.genderId == null || addSurveyRequest.genderId.equals("0")) {
            Toast.makeText(getApplicationContext(), "Beneficiary Gender is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.adharNo == null && addSurveyRequest.slumAdharNo == null) {
            Toast.makeText(getApplicationContext(), "Please enter Aadhar or other ID Number before Saving survey data", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.adharNo != null && addSurveyRequest.adharNo.length() < 12){
            Toast.makeText(getApplicationContext(), "Aadhar number is less than 12, please re-enter.", Toast.LENGTH_SHORT).show();
            return;
        } else if (addSurveyRequest.dob == null || addSurveyRequest.dob.length() <= 2){
            Toast.makeText(getApplicationContext(), "Please enter Date of birth before saving survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if (missionSpinner.getSelectedItemPosition() == 0) { //BISW27Mar18: Need to validate the values before submitting
            Toast.makeText(getApplicationContext(), "Please enter Preferred component of mission before submitting Survey data.", Toast.LENGTH_SHORT).show();
            return;
        } else if(addSurveyDataManager.applicantPhotoFile == null && addSurveyRequest.isNewRecord) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Applicant photo  is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        } /*else if(addSurveyDataManager.presentHousePhotoFile == null && addSurveyRequest.isNewRecord) {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), "Present house photo is required for survey data", Toast.LENGTH_SHORT).show();
            return;
        }*/

        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Saving ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        createUploadRequest(false);
    }

    AddSurveyRequest addSurveyRequest;

    String getStringFromEditText(EditText text) {
        if(text != null ){
            return text.getText().toString().trim();
        }
        return "";
    }

    boolean validateRequestFileds(boolean isSubmit ) {
        int count = AddSurveyDataManager.mFieldscount;
        boolean isValid = false;

        String[] ownership_arrays =  getResources().getStringArray(R.array.ownership_arrays);
        String[] house_arrays_roof =  getResources().getStringArray(R.array.house_arrays_roof);
        String[] house_arrays_wall =  getResources().getStringArray(R.array.house_arrays_wall);
        String[] house_req_arrays =  getResources().getStringArray(R.array.house_req_arrays);
        String[] employment_arrays =  getResources().getStringArray(R.array.employment_arrays);

        String[] mission_arrays =  getResources().getStringArray(R.array.mission_arrays);
        String[] bank_names =  getResources().getStringArray(R.array.bank_names);

        addSurveyRequest = AddSurveyDataManager.getInstance().mAddSurveyRequest;

        //addSurveyRequest.userId = "5";
       // addSurveyRequest.surveyId = "0.0";
        if(spinner_ownership.getSelectedItemPosition() > 0) {
            addSurveyRequest.ownershipHouse = Integer.toString(spinner_ownership.getSelectedItemPosition());
            AddSurveyDataManager.mFieldscount++;
        }

        if(house_type_spinner.getSelectedItemPosition() > 0) {
            addSurveyRequest.houseRoofType = Integer.toString(house_type_spinner.getSelectedItemPosition());
            AddSurveyDataManager.mFieldscount++;
        }
        if(house_wall_type_spinner.getSelectedItemPosition() > 0) {
            addSurveyRequest.houseWallType = Integer.toString(house_wall_type_spinner.getSelectedItemPosition());
            AddSurveyDataManager.mFieldscount++;
        }
        if(house_req_spinner.getSelectedItemPosition() > 0) {
            addSurveyRequest.houseRequirementRadio = Integer.toString(house_req_spinner.getSelectedItemPosition());
            AddSurveyDataManager.mFieldscount++;
        }

        if(emp_status.getSelectedItemPosition() > 0) {
            addSurveyRequest.employmentStatus = Integer.toString(emp_status.getSelectedItemPosition());
            AddSurveyDataManager.mFieldscount++;
        }
        if(missionSpinner.getSelectedItemPosition() > 0) {
            addSurveyRequest.preferredAssistanceHfa = Integer.toString(missionSpinner.getSelectedItemPosition());
            AddSurveyDataManager.mFieldscount++;
        }

        if(bank_spinner.getSelectedItemPosition() > 0) {
            addSurveyRequest.bankName = Integer.toString(bank_spinner.getSelectedItemPosition());
            AddSurveyDataManager.mFieldscount++;
        }

        if(getStringFromEditText(dwelling_size).length() > 1) {
            addSurveyRequest.sizeExistingDwelling = getStringFromEditText(dwelling_size);
            AddSurveyDataManager.mFieldscount++;
        }

        if(getStringFromEditText(house_req_details).length() > 1) {
            addSurveyRequest.requirement = "2";
            AddSurveyDataManager.mFieldscount++;
        }

        if(getStringFromEditText(name_pattadar).length() > 1) {
            addSurveyRequest.pattadarName = getStringFromEditText(name_pattadar);
            AddSurveyDataManager.mFieldscount++;
        }
        if(getStringFromEditText(patta_no).length() > 1) {
            addSurveyRequest.pattaNo = getStringFromEditText(patta_no);
            AddSurveyDataManager.mFieldscount++;
        }
        if(getStringFromEditText(dag_no).length() > 1) {
            addSurveyRequest.dagNo = getStringFromEditText(dag_no);
            AddSurveyDataManager.mFieldscount++;
        }
        if(getStringFromEditText(land_area).length() > 1) {
            addSurveyRequest.landAreaPatta = getStringFromEditText(land_area);
            AddSurveyDataManager.mFieldscount++;
        }
        if(getStringFromEditText(land_length).length() > 1) {
            addSurveyRequest.landLength = getStringFromEditText(land_length);
            AddSurveyDataManager.mFieldscount++;
        }
        if(getStringFromEditText(land_breadth).length() > 1) {
            addSurveyRequest.landBreadth = getStringFromEditText(land_breadth);
            AddSurveyDataManager.mFieldscount++;
        }

        if(getStringFromEditText(income).length() > 1) {
            addSurveyRequest.averageIncome = getStringFromEditText(income);
            AddSurveyDataManager.mFieldscount++;
        }

        if(bplchk.isChecked()) {
            if(getStringFromEditText(bpl_num).length() > 1) {
                addSurveyRequest.bplNo = getStringFromEditText(bpl_num);
                addSurveyRequest.bplRadio = "Y";
                AddSurveyDataManager.mFieldscount++;
                AddSurveyDataManager.mFieldscount++;
            }
        } else {
            addSurveyRequest.bplRadio = "N";
        }

        if(ration_cardchk.isChecked()) {
            if(getStringFromEditText(ration_num).length() > 1) {
                addSurveyRequest.rationCardNo = getStringFromEditText(ration_num);
                addSurveyRequest.rationRadio = "Y";
                AddSurveyDataManager.mFieldscount++;
                AddSurveyDataManager.mFieldscount++;
            }
        } else {
            addSurveyRequest.rationRadio = "N";
        }


        if(getStringFromEditText(acc_no).length() > 1) {
            addSurveyRequest.bankAccNo = getStringFromEditText(acc_no);
            AddSurveyDataManager.mFieldscount++;
        } else {
            //Toast.makeText(this, " Please enter a valid account number", Toast.LENGTH_SHORT).show();
            //return false;
        }

        if(getStringFromEditText(branch).length() > 1) {
            addSurveyRequest.bankBranchName = getStringFromEditText(branch);
            AddSurveyDataManager.mFieldscount++;
        }
        if(getStringFromEditText(ifsc_code).length() > 1) {
            addSurveyRequest.bankIfscCode = getStringFromEditText(ifsc_code);
            AddSurveyDataManager.mFieldscount++;
        }  /*else {
            Toast.makeText(this, " Please enter a valid IFSC code", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        if(eligible_chk_box.isChecked()){
            addSurveyRequest.eligibleStatus = "Y";
            addSurveyRequest.nonEligibleReason = "";
            AddSurveyDataManager.mFieldscount++;
        } else {
            if(getStringFromEditText(reason_non_eligible).length() >0 ) {
                addSurveyRequest.nonEligibleReason = getStringFromEditText(reason_non_eligible);
                AddSurveyDataManager.mFieldscount++;
            }
            addSurveyRequest.eligibleStatus = "N";
            AddSurveyDataManager.mFieldscount++;
        }

        if(isSubmit) {
            addSurveyRequest.isSubmitted = "Y";
            if( AddSurveyDataManager.mFieldscount >= 5) {
                isValid = true;
            }
        } else {
            addSurveyRequest.isSubmitted = "N";
            if( AddSurveyDataManager.mFieldscount >= 3) {
                isValid = true;
            }
        }
        Log.v("PMAY", " screen 4  fields : mFieldscount "+AddSurveyDataManager.mFieldscount);
        return isValid;
    }


    public void resetAll() {
        acc_no.setText("");
       // bank.setText("");
        ifsc_code.setText("");
        branch.setText("");
        dwelling_size.setText("");
        house_req_details.setText("");
        name_pattadar.setText("");
        patta_no.setText("");
        dag_no.setText("");
        land_area.setText("");
        land_length.setText("");
        land_breadth.setText("");
        income.setText("");
        bpl_num.setText("");
        ration_num.setText("");
        reason_non_eligible.setText("");
    }

    public static final int CAMERA_REQUEST = 10;


    private File getPhotoFileUri() {
        //File file = new File(Environment.getExternalStorageDirectory(),"/PMAY/attachment11" + ".jpg");
        //return file;
        return  null;
    }

    void launchCamera() {
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
            File newFile = new File(imagePath, "attachment_house.jpg");
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.sourcey.materialloginexample.provider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            Log.v("PMAY", " set URI in camera intent " +photoUri.getPath());
        }
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }
    PermissionUtil mPermissionUtil;

    public void housePicture(View v) {
        //mPermissionUtil.showAttachmentChooserDialog(this, attachListener, AddSurveyDataManager.PRESENT_HOUSE_PHTO);
        launchCameraIntent(PermissionUtil.CAMERA_REQUEST_PRESENT_HOUSE);

    }

    public void onLand1Attachment(View v) {
        mPermissionUtil.showAttachmentChooserDialog(this, attachListener, AddSurveyDataManager.LAND1_PHTO);
    }
    public void onLand2Attachment(View v) {
        mPermissionUtil.showAttachmentChooserDialog(this, attachListener, AddSurveyDataManager.LAND2_PHTO);
    }

    public void incomeProof(View v) {
        Toast.makeText(getBaseContext(), " Attachment for Income proof", Toast.LENGTH_SHORT).show();
        mPermissionUtil.showAttachmentChooserDialog(this, attachListener, AddSurveyDataManager.INCOME_PHTO);
    }

    public void onBPLCardAttach(View v) {
        Toast.makeText(getBaseContext(), " Attachment for BPl Card", Toast.LENGTH_SHORT).show();
        mPermissionUtil.showAttachmentChooserDialog(this, attachListener, AddSurveyDataManager.BPL_PHTO);
    }

    public void onRationCardAttach(View v) {
        Toast.makeText(getBaseContext(), " Attachment for Ration card ", Toast.LENGTH_SHORT).show();
        mPermissionUtil.showAttachmentChooserDialog(this, attachListener, AddSurveyDataManager.RATIONCD_PHTO);
    }


    public void otherHouseProof(View v) {
        Toast.makeText(getBaseContext(), " Patta/House Pic/Land Record/can be attached", Toast.LENGTH_SHORT).show();
    }


    private File createFile() throws  IOException {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile("attachment_house", ".jpg", storageDir);
        return image;

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

    void launchCameraIntent(int request) {
        if (permissionUtil.checkMarshMellowPermission()) {
            AddSurveyDataManager.getInstance().requestLocationUpdates(this);
            if (permissionUtil.verifyPermissions(this, permissionUtil.getCameraPermissions())) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, request);
                Toast.makeText(getBaseContext(), "Taking picture ", Toast.LENGTH_SHORT).show();                //launchCamera();
            } else
                ActivityCompat.requestPermissions(this, permissionUtil.getCameraPermissions(), request);
        }

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
                ActivityCompat.requestPermissions(MainActivity2.this, permissionUtil.getCameraPermissions(), requestCode);
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
                ActivityCompat.requestPermissions(MainActivity2.this, permissionUtil.getGalleryPermissions(), requestCode);
            }

        }
    };

    AddSurveyDataManager addSurveyDataManager;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("Housing", " onActivityResult requestCode: " + requestCode + " result " + resultCode + " " + data);
        if (addSurveyDataManager == null) {
            addSurveyDataManager = AddSurveyDataManager.getInstance();
        }
        super.onActivityResult(requestCode, resultCode, data);
        // Applicant photo
        if (requestCode == PermissionUtil.CAMERA_REQUEST_PRESENT_HOUSE) {
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ////Added 28MarBiswajit
                AddSurveyDataManager.getInstance().mBitmapHousePicOffline = thumbnail;
                if(addSurveyRequest == null) {
                    addSurveyRequest = AddSurveyDataManager.getInstance().mAddSurveyRequest;
                }
                addSurveyRequest.isHousePicUploaded = true;
                Log.v("PMAY", " current phot path : " + thumbnail);
                addSurveyDataManager.presentHousePhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.PRESENT_HOUSE_PHTO, addSurveyDataManager.presentHousePhotoFile);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.GALLERY_REQUEST_PRESENT_HOUSE) { //IDproof
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(getBaseContext(), "Successfully picked an attachment", Toast.LENGTH_SHORT).show();
                addSurveyDataManager.presentHousePhotoFile = addSurveyDataManager.getImageFilefromGallery(data.getData(), getApplicationContext());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to pick an attachment", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.CAMERA_REQUEST_LAND1) { //LAND1
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Log.v("PMAY", " current phot path : " + thumbnail);
                addSurveyDataManager.land1PhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.LAND1_PHTO, addSurveyDataManager.land1PhotoFile);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.GALLERY_REQUEST_LAND1) { //LAND1
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(getBaseContext(), "Successfully picked an attachment", Toast.LENGTH_SHORT).show();
                addSurveyDataManager.land1PhotoFile = addSurveyDataManager.getImageFilefromGallery(data.getData(), getApplicationContext());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to pick an attachment", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.CAMERA_REQUEST_LAND2) { //LAND2
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Log.v("PMAY", " current phot path : " + thumbnail);
                addSurveyDataManager.land2PhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.LAND2_PHTO, addSurveyDataManager.land2PhotoFile);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.GALLERY_REQUEST_LAND2) { //LAND1
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(getBaseContext(), "Successfully picked an attachment", Toast.LENGTH_SHORT).show();
                addSurveyDataManager.land2PhotoFile = addSurveyDataManager.getImageFilefromGallery(data.getData(), getApplicationContext());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to pick an attachment", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.CAMERA_REQUEST_INCOME) { //LAND2
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Log.v("PMAY", " current phot path : " + thumbnail);
                addSurveyDataManager.incomePhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.INCOME_PHTO, addSurveyDataManager.incomePhotoFile);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.GALLERY_REQUEST_INCOME) { //LAND1
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(getBaseContext(), "Successfully picked an attachment", Toast.LENGTH_SHORT).show();
                addSurveyDataManager.incomePhotoFile = addSurveyDataManager.getImageFilefromGallery(data.getData(), getApplicationContext());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to pick an attachment", Toast.LENGTH_SHORT).show();
            }
        }  else if (requestCode == PermissionUtil.CAMERA_REQUEST_BPL) { //LAND2
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Log.v("PMAY", " current phot path : " + thumbnail);
                addSurveyDataManager.bplPhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.BPL_PHTO, addSurveyDataManager.bplPhotoFile);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.GALLERY_REQUEST_BPL) { //LAND1
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(getBaseContext(), "Successfully picked an attachment", Toast.LENGTH_SHORT).show();
                addSurveyDataManager.bplPhotoFile = addSurveyDataManager.getImageFilefromGallery(data.getData(), getApplicationContext());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to pick an attachment", Toast.LENGTH_SHORT).show();
            }
        }   else if (requestCode == PermissionUtil.CAMERA_REQUEST_RATION) { //LAND2
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Toast.makeText(getBaseContext(), "Successfully captured ", Toast.LENGTH_SHORT).show();
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Log.v("PMAY", " current phot path : " + thumbnail);
                addSurveyDataManager.rationPhotoFile = addSurveyDataManager.createImageFile(thumbnail, AddSurveyDataManager.RATIONCD_PHTO, addSurveyDataManager.rationPhotoFile);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to capture. Please try again ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PermissionUtil.GALLERY_REQUEST_RATION) { //LAND1
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Toast.makeText(getBaseContext(), "Successfully picked an attachment", Toast.LENGTH_SHORT).show();
                addSurveyDataManager.rationPhotoFile = addSurveyDataManager.getImageFilefromGallery(data.getData(), getApplicationContext());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Failed to pick an attachment", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
