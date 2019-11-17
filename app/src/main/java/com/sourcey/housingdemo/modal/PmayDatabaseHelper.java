package com.sourcey.housingdemo.modal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
import android.widget.Toast;
import com.sourcey.housingdemo.Log;

import com.sourcey.housingdemo.AddSurveyDataManager;
import com.sourcey.housingdemo.restservice.AddSurveyRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Biswajit on 12-03-2018.
 */

public class PmayDatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    // old DB oversion is 1 and new version is 2 still
    //old DB oversion is 2 and new version is 3 still
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "PmaySurveyRecords.db";

    // User table name
    private static final String TABLE_USER = "survey";

    // User Table Columns names
    private static final String COLUMN_APPLICANT_NAME = "applicant_name";
    private static final String COLUMN_ADHAR_NO = "adhar_no";
    private static final String COLUMN_SURVEY_ID = "surveyId";
    private static final String COLUMN_FATHER_NAME = "father_name";
    private static final String COLUMN_ACC_NO = "acc_no";
    private static final String COLUMN_SLUM = "slum";

    private static final String COLUMN_IS_SUBMITTED = "is_submitted";

    // columns

    private static final String COLUMN_adharReason = "adharReason";
    private static final String COLUMN_maritalStatus = "maritalStatus";
    private static final String COLUMN_eligibleStatus = "eligibleStatus";
    private static final String COLUMN_nonEligibleReason = "nonEligibleReason";
    private static final String COLUMN_genderId = "genderId";
    private static final String COLUMN_idType = "idType";
    private static final String COLUMN_idNo = "idNo";
    private static final String COLUMN_dob = "dob";
    private static final String COLUMN_ulbNameId = "ulbNameId";
    private static final String COLUMN_religion = "religion";
    private static final String COLUMN_IS_HOUSE_PIC_UPLOADED = "isHousePicUploaded";
    private static final String COLUMN_IS_FINGER_PRINT_UPLOADED = "isFingerPrintUploaded";
    private static final String COLUMN_religionIfOther = "religionIfOther";
    private static final String COLUMN_caste = "caste";
    private static final String COLUMN_presentTown = "presentTown";
    private static final String COLUMN_presentHouseNo = "presentHouseNo";
    private static final String COLUMN_presentStreetName = "presentStreetName";
    private static final String COLUMN_presentCity = "presentCity";
    private static final String COLUMN_presentMobileNo = "presentMobileNo";
    private static final String COLUMN_isSameAsPresentAdd = "isSameAsPresentAdd";
    private static final String COLUMN_permanentTown = "permanentTown";
    private static final String COLUMN_permanentHouseNo = "permanentHouseNo";
    private static final String COLUMN_permanentStreetName = "permanentStreetName";
    private static final String COLUMN_permanentCity = "permanentCity";
    private static final String COLUMN_permanentMobileNo = "permanentMobileNo";
    private static final String COLUMN_houseRoofType = "houseRoofType";
    private static final String COLUMN_ownsRadio = "ownsRadio";
    private static final String COLUMN_landinSqm = "landinSqm";
    private static final String COLUMN_landLocation = "landLocation";
    private static final String COLUMN_ownershipHouse = "ownershipHouse";
    private static final String COLUMN_houseWallType = "houseWallType";
    private static final String COLUMN_dwellinUnitRoom = "dwellinUnitRoom";
    private static final String COLUMN_sizeExistingDwelling = "sizeExistingDwelling";
    private static final String COLUMN_houseRequirementRadio = "houseRequirementRadio";
    private static final String COLUMN_requirement = "requirement";
    private static final String COLUMN_pattadarName = "pattadarName";
    private static final String COLUMN_dagNo = "dagNo";
    private static final String COLUMN_pattaNo ="pattaNo";
    private static final String COLUMN_landAreaPatta = "landAreaPatta";
    private static final String COLUMN_landLength = "landLength";
    private static final String COLUMN_landBreadth = "landBreadth";
    private static final String COLUMN_employmentStatus = "employmentStatus";
    private static final String COLUMN_employmentStatusName = "employmentStatusName";
    private static final String COLUMN_averageIncome = "averageIncome";
    private static final String COLUMN_incomeProof = "incomeProof";
    private static final String COLUMN_bplRadio = "bplRadio";
    private static final String COLUMN_bplNo = "bplNo";
    private static final String COLUMN_rationRadio = "rationRadio";
    private static final String COLUMN_rationCardNo = "rationCardNo";
    private static final String COLUMN_preferredAssistanceHfa = "preferredAssistanceHfa";
    private static final String COLUMN_wardDetails = "wardDetails";

    private static final String COLUMN_bankName = "bankName";
    private static final String COLUMN_bankBranchName = "bankBranchName";
    private static final String COLUMN_bankIfscCode = "bankIfscCode";
    private static final String COLUMN_familyMemberName = "familyMemberName";
    private static final String COLUMN_familyMemberRelation = "familyMemberRelation";
    private static final String COLUMN_familyMemberGender = "familyMemberGender";
    private static final String COLUMN_familyMemberAge = "familyMemberAge";
    private static final String COLUMN_familyMemberIdCardNo = "familyMemberIdCardNo";

    private static final String DATABASE_ALTER_TABLE_1 = "ALTER TABLE "
            + TABLE_USER + " ADD COLUMN " + COLUMN_IS_HOUSE_PIC_UPLOADED + " INTEGER";

    private static final String DATABASE_ALTER_TABLE_2 = "ALTER TABLE "
            + TABLE_USER + " ADD COLUMN " + COLUMN_IS_FINGER_PRINT_UPLOADED + " INTEGER";

    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_APPLICANT_NAME + " TEXT,"
            + COLUMN_ADHAR_NO + " TEXT,"
            + COLUMN_SURVEY_ID + " TEXT,"
            + COLUMN_ACC_NO + " TEXT,"
            + COLUMN_SLUM + " TEXT,"
            + COLUMN_IS_SUBMITTED + " TEXT,"
            + COLUMN_FATHER_NAME + " TEXT,"
            +  COLUMN_adharReason  + " TEXT,"
            +  COLUMN_maritalStatus + " TEXT,"
            +  COLUMN_eligibleStatus + " TEXT,"
            +  COLUMN_nonEligibleReason + " TEXT,"
            +  COLUMN_genderId + " TEXT,"
            +  COLUMN_idType + " TEXT,"
            +  COLUMN_idNo + " TEXT,"
            +  COLUMN_dob + " TEXT,"
            +  COLUMN_IS_HOUSE_PIC_UPLOADED + " INTEGER, "
            +  COLUMN_IS_FINGER_PRINT_UPLOADED + " INTEGER, "
            +  COLUMN_ulbNameId + " COLUMN_ulbNameId,"
            +  COLUMN_religion + " TEXT,"
            +  COLUMN_religionIfOther + " TEXT,"
            +  COLUMN_caste + " TEXT,"
            +  COLUMN_presentTown + " TEXT,"
            +  COLUMN_presentHouseNo + " TEXT,"
            +  COLUMN_presentStreetName + " TEXT,"
            +  COLUMN_presentCity + " TEXT,"
            +  COLUMN_presentMobileNo + " TEXT,"
            +  COLUMN_isSameAsPresentAdd + " TEXT,"
            +  COLUMN_permanentTown + " TEXT,"
            +  COLUMN_permanentHouseNo + " TEXT,"
            +  COLUMN_permanentStreetName + " TEXT,"
            +  COLUMN_permanentCity + " TEXT,"
            +  COLUMN_permanentMobileNo + " TEXT,"
            +  COLUMN_houseRoofType + " TEXT,"
            +  COLUMN_ownsRadio + " TEXT,"
            +  COLUMN_landinSqm + " TEXT,"
            +  COLUMN_landLocation + " TEXT,"
            +  COLUMN_ownershipHouse + " TEXT,"
            +  COLUMN_houseWallType + " TEXT,"
            +  COLUMN_dwellinUnitRoom + " TEXT,"
            +  COLUMN_sizeExistingDwelling + " TEXT,"
            +  COLUMN_houseRequirementRadio + " TEXT,"
            +  COLUMN_requirement + " TEXT,"
            +  COLUMN_pattadarName + " TEXT,"
            +  COLUMN_dagNo + " TEXT,"
            +  COLUMN_pattaNo + " TEXT,"
            +  COLUMN_landAreaPatta + " TEXT,"
            +  COLUMN_landLength + " TEXT,"
            +  COLUMN_landBreadth + " TEXT,"
            +  COLUMN_employmentStatus + " TEXT,"
            +  COLUMN_employmentStatusName + " TEXT,"
            +  COLUMN_averageIncome + " TEXT,"
            +  COLUMN_incomeProof + " TEXT,"
            +  COLUMN_bplRadio + " TEXT,"
            +  COLUMN_bplNo + " TEXT,"
            +  COLUMN_rationRadio + " TEXT,"
            +  COLUMN_rationCardNo + " TEXT,"
            +  COLUMN_preferredAssistanceHfa + " TEXT,"
            +  COLUMN_wardDetails + " TEXT,"

            +  COLUMN_bankName + " TEXT,"
            +  COLUMN_bankBranchName + " TEXT,"
            +  COLUMN_bankIfscCode + " TEXT,"
            +  COLUMN_familyMemberName + " TEXT,"
            +  COLUMN_familyMemberRelation + " TEXT,"
            +  COLUMN_familyMemberGender + " TEXT,"
            +  COLUMN_familyMemberAge + " TEXT,"
            +  COLUMN_familyMemberIdCardNo + " TEXT"
            + ")";

    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    /**
     * Constructor
     *
     * @param context
     */
    public PmayDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("PMAY- DB", "  : onCreate :  ");
        db.execSQL(CREATE_USER_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.d("PMAY", " onUpgrade : oldVersion :  "+oldVersion +" , newVersion : "+newVersion);
         Log.v("PMAY- DB", " onUpgrade : oldVersion :  "+oldVersion +" , newVersion : "+newVersion);
        if (oldVersion <= 2) {
            try {
                Log.v("PMAY- DB", " onUpgrade : DATABASE_ALTER_TABLE_1 - enter  ");
                db.execSQL(DATABASE_ALTER_TABLE_1);
                Log.v("PMAY- DB", " onUpgrade : DATABASE_ALTER_TABLE_1 - Exit  ");
            } catch (Exception e) {
                Log.v("PMAY- DB", " onUpgrade : Exception  -  1 "+e.getMessage());
            }
        }
        if (oldVersion <= 3) {
            try {
                Log.v("PMAY- DB", " onUpgrade : DATABASE_ALTER_TABLE_2 - enter  ");
                db.execSQL(DATABASE_ALTER_TABLE_2);
                Log.v("PMAY- DB", " onUpgrade : DATABASE_ALTER_TABLE_2 - Exit  ");
            } catch (Exception e) {
                Log.v("PMAY- DB", " onUpgrade : Exception  - 2  "+e.getMessage());
            }
        }
        //Drop User Table if exist
        //db.execSQL(DROP_USER_TABLE);

        // Create tables again
        //onCreate(db);
    }

    SQLiteDatabase writableDb;

    /**
     * This method is to create user record
     *
     * @param user
     */
    public void addUser(AddSurveyRequest user) {
        Log.v("PMAY", " addUser() DB - Enter " );
        if(writableDb == null) {
            writableDb = this.getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        if("S".equals(user.chckSlumRadio)) {
            values.put(COLUMN_ADHAR_NO, user.slumAdharNo);
            values.put(COLUMN_FATHER_NAME, user.slumFatherHusbandName);
            values.put(COLUMN_APPLICANT_NAME, user.slumFamilyHeadName);
            values.put(COLUMN_SLUM, user.chckSlumRadio);
            values.put(COLUMN_IS_SUBMITTED, user.isSubmittedFlag);
            values.put(COLUMN_genderId, user.genderValue);
            values.put(COLUMN_religion,user.slumReligion);
            values.put(COLUMN_caste, user.slumCaste);

            values.put(COLUMN_presentTown, user.slumPresentTown);
            values.put(COLUMN_presentHouseNo, user.slumPresentHouseNo);
            values.put(COLUMN_presentStreetName, user.slumPresentStreetName);
            values.put(COLUMN_presentCity, user.slumPresentCity);
            values.put(COLUMN_presentMobileNo, user.presentMobileNo);
            values.put(COLUMN_isSameAsPresentAdd, user.slumIsSameAsPresentAdd);
            values.put(COLUMN_permanentTown, user.slumPermanentTown);
            values.put(COLUMN_permanentHouseNo, user.slumPermanentHouseNo);
            values.put(COLUMN_permanentStreetName, user.slumPermanentStreetName);
            values.put(COLUMN_permanentCity, user.slumPermanentCity);
            values.put(COLUMN_permanentMobileNo, user.slumPermanentMobileNo);

        } else {
            values.put(COLUMN_ADHAR_NO, user.adharNo);
            values.put(COLUMN_FATHER_NAME, user.fatherHusbandName);
            values.put(COLUMN_APPLICANT_NAME, user.familyHeadName);
            values.put(COLUMN_SLUM, user.slumRadio);
            values.put(COLUMN_IS_SUBMITTED, user.isSubmitted);
            values.put(COLUMN_genderId, user.genderId);
            values.put(COLUMN_religion,user.religion);
            values.put(COLUMN_caste, user.caste);

            values.put(COLUMN_presentTown, user.presentTown);
            values.put(COLUMN_presentHouseNo, user.presentHouseNo);
            values.put(COLUMN_presentStreetName, user.presentStreetName);
            values.put(COLUMN_presentCity, user.presentCity);
            values.put(COLUMN_presentMobileNo, user.presentMobileNo);
            values.put(COLUMN_isSameAsPresentAdd, user.isSameAsPresentAdd);
            values.put(COLUMN_permanentTown, user.permanentTown);
            values.put(COLUMN_permanentHouseNo, user.permanentHouseNo);
            values.put(COLUMN_permanentStreetName, user.permanentStreetName);
            values.put(COLUMN_permanentCity, user.permanentCity);
            values.put(COLUMN_permanentMobileNo, user.permanentMobileNo);

            values.put(COLUMN_ownsRadio, user.ownsRadio);
            values.put(COLUMN_landinSqm, user.landinSqm);
            values.put(COLUMN_landLocation, user.landLocation);
            if(user.isHousePicUploaded) {
                values.put(COLUMN_IS_HOUSE_PIC_UPLOADED, 1);
            } else {
                values.put(COLUMN_IS_HOUSE_PIC_UPLOADED, 0);
            }
        }
        values.put(COLUMN_SURVEY_ID, user.surveyId);
        values.put(COLUMN_ACC_NO, user.bankAccNo);
        values.put(COLUMN_adharReason, user.adharReason);
        values.put(COLUMN_maritalStatus, user.maritalStatus);
        values.put(COLUMN_eligibleStatus, user.eligibleStatus);
        values.put(COLUMN_nonEligibleReason, user.nonEligibleReason);
        Log.v("PMAY", "Record added  user.nonEligibleReason : "+user.nonEligibleReason);
        values.put(COLUMN_idType, user.idType);
        values.put(COLUMN_idNo, user.idNo);
        values.put(COLUMN_dob, user.dob);

        values.put(COLUMN_religionIfOther, user.religionIfOther);
        values.put(COLUMN_houseRoofType, user.houseRoofType);

        values.put(COLUMN_ownershipHouse, user.ownershipHouse);
        values.put(COLUMN_houseWallType, user.houseWallType);
        values.put(COLUMN_dwellinUnitRoom, user.dwellinUnitRoom);
        values.put(COLUMN_sizeExistingDwelling, user.sizeExistingDwelling);
        values.put(COLUMN_houseRequirementRadio, user.houseRequirementRadio);
        values.put(COLUMN_requirement, user.requirement);
        values.put(COLUMN_pattadarName, user.pattadarName);
        values.put(COLUMN_dagNo, user.dagNo);
        values.put(COLUMN_pattaNo, user.pattaNo);
        values.put(COLUMN_landAreaPatta, user.landAreaPatta);
        values.put(COLUMN_landLength, user.landLength);
        values.put(COLUMN_landBreadth, user.landBreadth);
        values.put(COLUMN_employmentStatus, user.employmentStatus);
        values.put(COLUMN_employmentStatusName, user.employmentStatusName);
        values.put(COLUMN_averageIncome, user.averageIncome);
        values.put(COLUMN_incomeProof, user.incomeProof);
        values.put(COLUMN_bplRadio, user.bplRadio);
        values.put(COLUMN_bplNo, user.bplNo);
        values.put(COLUMN_rationRadio, user.rationRadio);
        values.put(COLUMN_rationCardNo, user.rationCardNo);
        // geo coordinates
        if(user.geoLatitude != null && !"0".equals(user.geoLatitude)) {
            values.put(COLUMN_bplNo, user.geoLatitude);
        }
        if(user.geoLongitude != null && !"0".equals(user.geoLongitude)) {
            values.put(COLUMN_rationCardNo, user.geoLongitude);
        }
        Log.v("PMAY", " Bpl no  "+user.bplNo +" , "+user.isSubmitted +" flag : "+user.isSubmittedFlag);
        values.put(COLUMN_preferredAssistanceHfa, user.preferredAssistanceHfa);
        values.put(COLUMN_wardDetails, user.wardDetails);
        values.put(COLUMN_ulbNameId, user.ulbNameId);

        values.put(COLUMN_bankName, user.bankName);
        values.put(COLUMN_bankBranchName, user.bankBranchName);
        values.put(COLUMN_bankIfscCode, user.bankIfscCode);
        values.put(COLUMN_familyMemberName, user.familyMemberName);
        values.put(COLUMN_familyMemberRelation, user.familyMemberRelation);
        values.put(COLUMN_familyMemberGender, user.familyMemberGender);
        values.put(COLUMN_familyMemberAge, user.familyMemberAge);
        values.put(COLUMN_familyMemberIdCardNo, user.familyMemberIdCardNo);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                // Inserting Row
        long id = writableDb.insert(TABLE_USER, null, values);
        Log.v("PMAY", "AddRecord : id "+id);
        //db.close();
    }

    public void closeDb() {
        if(writableDb != null) {
            writableDb.close();
            writableDb = null;
        }
    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    public List<SurveyDataModal> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_APPLICANT_NAME,
                COLUMN_SURVEY_ID,
                COLUMN_ADHAR_NO,
                COLUMN_FATHER_NAME,
                COLUMN_ACC_NO,
                COLUMN_SLUM,
                COLUMN_IS_SUBMITTED
        };
        // sorting orders
        String sortOrder =
                COLUMN_APPLICANT_NAME + " ASC";
        List<SurveyDataModal> userList = new ArrayList<SurveyDataModal>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SurveyDataModal user = new SurveyDataModal();
                user.setmName(cursor.getString(cursor.getColumnIndex(COLUMN_APPLICANT_NAME)));
                user.setmPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_ADHAR_NO)));
                user.mSurveyId = (cursor.getString(cursor.getColumnIndex(COLUMN_SURVEY_ID)));
                user.mFatherName = (cursor.getString(cursor.getColumnIndex(COLUMN_FATHER_NAME)));
                user.mBankAccNo = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_NO));
                user.mSlum = cursor.getString(cursor.getColumnIndex(COLUMN_SLUM));
                user.isSubmitted = cursor.getString(cursor.getColumnIndex(COLUMN_IS_SUBMITTED));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.v("PMAY", " list size of users "+userList.size());

        // return user list
        return userList;
    }

    public AddSurveyRequest getSurveyDataById(String survey_id, String adhar) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_APPLICANT_NAME,
                COLUMN_SURVEY_ID,
                COLUMN_ADHAR_NO,
                COLUMN_FATHER_NAME,
                COLUMN_ACC_NO,
                COLUMN_SLUM,
                COLUMN_IS_SUBMITTED,
                COLUMN_adharReason,
                COLUMN_maritalStatus,
                COLUMN_eligibleStatus,
                COLUMN_nonEligibleReason,
                COLUMN_genderId,
                COLUMN_idType,
                COLUMN_idNo,
                COLUMN_dob,
                COLUMN_IS_HOUSE_PIC_UPLOADED,
                COLUMN_IS_FINGER_PRINT_UPLOADED,
                COLUMN_ulbNameId,
                COLUMN_religion,
                COLUMN_religionIfOther,
                COLUMN_caste,
                COLUMN_presentTown,
                COLUMN_presentHouseNo,
                COLUMN_presentStreetName,
                COLUMN_presentCity,
                COLUMN_presentMobileNo,
                COLUMN_isSameAsPresentAdd,
                COLUMN_permanentTown,
                COLUMN_permanentHouseNo,
                COLUMN_permanentStreetName,
                COLUMN_permanentCity,
                COLUMN_permanentMobileNo,
                COLUMN_houseRoofType,
                COLUMN_ownsRadio,
                COLUMN_landinSqm,
                COLUMN_landLocation,
                COLUMN_ownershipHouse,
                COLUMN_houseWallType,
                COLUMN_dwellinUnitRoom,
                COLUMN_sizeExistingDwelling,
                COLUMN_houseRequirementRadio,
                COLUMN_requirement,
                COLUMN_pattadarName,
                COLUMN_dagNo,
                COLUMN_pattaNo,
                COLUMN_landAreaPatta,
                COLUMN_landLength,
                COLUMN_landBreadth,
                COLUMN_employmentStatus,
                COLUMN_employmentStatusName,
                COLUMN_averageIncome,
                COLUMN_incomeProof,
                COLUMN_bplRadio,
                COLUMN_bplNo,
                COLUMN_rationRadio,
                COLUMN_rationCardNo,
                COLUMN_preferredAssistanceHfa,
                COLUMN_wardDetails,
                COLUMN_bankName,
                COLUMN_bankBranchName,
                COLUMN_bankIfscCode,
                COLUMN_familyMemberName,
                COLUMN_familyMemberRelation,
                COLUMN_familyMemberGender,
                COLUMN_familyMemberAge,
                COLUMN_familyMemberIdCardNo
        };
        // sorting orders

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = null;
        if(survey_id != null && survey_id.equals("0") && adhar != null && !adhar.isEmpty()) {
            cursor = db.query(TABLE_USER, //Table to query
                    columns,    //columns to return
                    COLUMN_ADHAR_NO + " = ?",
                    new String[]{adhar},        //columns for the WHERE clause
                    //The values for the WHERE clause
                    null,       //group the rows
                    null,       //filter by row groups
                    null); //The sort order
        } else {
            cursor = db.query(TABLE_USER, //Table to query
                    columns,    //columns to return
                    COLUMN_SURVEY_ID + " = ?",
                    new String[]{survey_id},        //columns for the WHERE clause
                    //The values for the WHERE clause
                    null,       //group the rows
                    null,       //filter by row groups
                    null); //The sort order
        }

        AddSurveyRequest user = AddSurveyDataManager.getInstance().mAddSurveyRequest;

        // Traversing through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                user.familyHeadName = cursor.getString(cursor.getColumnIndex(COLUMN_APPLICANT_NAME));
                user.adharNo = cursor.getString(cursor.getColumnIndex(COLUMN_ADHAR_NO));
                user.surveyId = (cursor.getString(cursor.getColumnIndex(COLUMN_SURVEY_ID)));
                user.fatherHusbandName = (cursor.getString(cursor.getColumnIndex(COLUMN_FATHER_NAME)));
                user.bankAccNo = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_NO));
                user.slumRadio = cursor.getString(cursor.getColumnIndex(COLUMN_SLUM));
                user.isSubmitted = cursor.getString(cursor.getColumnIndex(COLUMN_IS_SUBMITTED));

                user.wardDetails = cursor.getString(cursor.getColumnIndex(COLUMN_wardDetails));
                user.idType = cursor.getString(cursor.getColumnIndex(COLUMN_idType));
                user.genderId = cursor.getString(cursor.getColumnIndex(COLUMN_genderId));
                user.dob = cursor.getString(cursor.getColumnIndex(COLUMN_dob));
                int state = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_HOUSE_PIC_UPLOADED));
                user.isHousePicUploaded = state == 1;
                int bioState = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FINGER_PRINT_UPLOADED));
                user.isFingerPrintUploaded = bioState == 1;
                user.ulbNameId = cursor.getString(cursor.getColumnIndex(COLUMN_ulbNameId));
                user.maritalStatus = cursor.getString(cursor.getColumnIndex(COLUMN_maritalStatus));
                user.religion = cursor.getString(cursor.getColumnIndex(COLUMN_religion));
                user.caste = cursor.getString(cursor.getColumnIndex(COLUMN_caste));
                user.presentStreetName = cursor.getString(cursor.getColumnIndex(COLUMN_presentStreetName));
                user.presentCity = cursor.getString(cursor.getColumnIndex(COLUMN_presentCity));
                user.presentHouseNo = cursor.getString(cursor.getColumnIndex(COLUMN_presentHouseNo));
                user.presentMobileNo = cursor.getString(cursor.getColumnIndex(COLUMN_presentMobileNo));

                user.familyMemberName = cursor.getString(cursor.getColumnIndex(COLUMN_familyMemberName));
                user.familyMemberAge = cursor.getString(cursor.getColumnIndex(COLUMN_familyMemberAge));
                user.familyMemberGender = cursor.getString(cursor.getColumnIndex(COLUMN_familyMemberGender));
                user.familyMemberIdCardNo = cursor.getString(cursor.getColumnIndex(COLUMN_familyMemberIdCardNo));
                user.familyMemberRelation = cursor.getString(cursor.getColumnIndex(COLUMN_familyMemberRelation));

                user.landLocation = cursor.getString(cursor.getColumnIndex(COLUMN_landLocation));
                user.ownsRadio = cursor.getString(cursor.getColumnIndex(COLUMN_ownsRadio));
                user.landinSqm = cursor.getString(cursor.getColumnIndex(COLUMN_landinSqm));

                user.eligibleStatus = cursor.getString(cursor.getColumnIndex(COLUMN_eligibleStatus));
                user.nonEligibleReason = cursor.getString(cursor.getColumnIndex(COLUMN_nonEligibleReason));
                user.preferredAssistanceHfa = cursor.getString(cursor.getColumnIndex(COLUMN_preferredAssistanceHfa));
                //lat , long
                user.bplNo = cursor.getString(cursor.getColumnIndex(COLUMN_bplNo));
                user.rationCardNo = cursor.getString(cursor.getColumnIndex(COLUMN_rationCardNo));

                // Adding user record to list
                //userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.v("PMAY", " getUserBySurveyId "+user.adharNo);

        // return user list
        return user;
    }


    /**
     * This method to update user record
     *
     * @param user
     */
    public void updateUser(AddSurveyRequest user, boolean isByAdhar) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if("S".equals(user.chckSlumRadio)) {
            values.put(COLUMN_ADHAR_NO, user.slumAdharNo);
            values.put(COLUMN_FATHER_NAME, user.slumFatherHusbandName);
            values.put(COLUMN_APPLICANT_NAME, user.slumFamilyHeadName);
            values.put(COLUMN_SLUM, user.chckSlumRadio);
            values.put(COLUMN_IS_SUBMITTED, user.isSubmittedFlag);
            values.put(COLUMN_genderId, user.genderValue);
            values.put(COLUMN_religion,user.slumReligion);
            values.put(COLUMN_caste, user.slumCaste);

            values.put(COLUMN_presentTown, user.slumPresentTown);
            values.put(COLUMN_presentHouseNo, user.slumPresentHouseNo);
            values.put(COLUMN_presentStreetName, user.slumPresentStreetName);
            values.put(COLUMN_presentCity, user.slumPresentCity);
            values.put(COLUMN_presentMobileNo, user.presentMobileNo);
            values.put(COLUMN_isSameAsPresentAdd, user.slumIsSameAsPresentAdd);
            values.put(COLUMN_permanentTown, user.slumPermanentTown);
            values.put(COLUMN_permanentHouseNo, user.slumPermanentHouseNo);
            values.put(COLUMN_permanentStreetName, user.slumPermanentStreetName);
            values.put(COLUMN_permanentCity, user.slumPermanentCity);
            values.put(COLUMN_permanentMobileNo, user.slumPermanentMobileNo);

        } else {
            values.put(COLUMN_ADHAR_NO, user.adharNo);
            values.put(COLUMN_FATHER_NAME, user.fatherHusbandName);
            values.put(COLUMN_APPLICANT_NAME, user.familyHeadName);
            values.put(COLUMN_SLUM, user.slumRadio);
            values.put(COLUMN_IS_SUBMITTED, user.isSubmitted);
            values.put(COLUMN_genderId, user.genderId);
            values.put(COLUMN_religion,user.religion);
            values.put(COLUMN_caste, user.caste);

            values.put(COLUMN_presentTown, user.presentTown);
            values.put(COLUMN_presentHouseNo, user.presentHouseNo);
            values.put(COLUMN_presentStreetName, user.presentStreetName);
            values.put(COLUMN_presentCity, user.presentCity);
            values.put(COLUMN_presentMobileNo, user.presentMobileNo);
            values.put(COLUMN_isSameAsPresentAdd, user.isSameAsPresentAdd);
            values.put(COLUMN_permanentTown, user.permanentTown);
            values.put(COLUMN_permanentHouseNo, user.permanentHouseNo);
            values.put(COLUMN_permanentStreetName, user.permanentStreetName);
            values.put(COLUMN_permanentCity, user.permanentCity);
            values.put(COLUMN_permanentMobileNo, user.permanentMobileNo);

            values.put(COLUMN_ownsRadio, user.ownsRadio);
            values.put(COLUMN_landinSqm, user.landinSqm);
            values.put(COLUMN_landLocation, user.landLocation);
            if(user.isHousePicUploaded) {
                values.put(COLUMN_IS_HOUSE_PIC_UPLOADED, 1);
            } else {
                values.put(COLUMN_IS_HOUSE_PIC_UPLOADED, 0);
            }
            if(user.isFingerPrintUploaded) {
                values.put(COLUMN_IS_FINGER_PRINT_UPLOADED, 1);
            } else {
                values.put(COLUMN_IS_FINGER_PRINT_UPLOADED, 0);
            }
        }
        values.put(COLUMN_SURVEY_ID, user.surveyId);
        values.put(COLUMN_ACC_NO, user.bankAccNo);
        values.put(COLUMN_adharReason, user.adharReason);
        values.put(COLUMN_maritalStatus, user.maritalStatus);
        values.put(COLUMN_eligibleStatus, user.eligibleStatus);
        values.put(COLUMN_nonEligibleReason, user.nonEligibleReason);
        values.put(COLUMN_idType, user.idType);
        values.put(COLUMN_idNo, user.idNo);
        values.put(COLUMN_dob, user.dob);
        values.put(COLUMN_ulbNameId, user.ulbNameId);
        Log.v("PMAY", "Update User "+ user.bplNo +" , "+ user.isSubmitted +" flag : "+user.isSubmittedFlag);
        values.put(COLUMN_religionIfOther, user.religionIfOther);
        values.put(COLUMN_houseRoofType, user.houseRoofType);

        values.put(COLUMN_ownershipHouse, user.ownershipHouse);
        values.put(COLUMN_houseWallType, user.houseWallType);
        values.put(COLUMN_dwellinUnitRoom, user.dwellinUnitRoom);
        values.put(COLUMN_sizeExistingDwelling, user.sizeExistingDwelling);
        values.put(COLUMN_houseRequirementRadio, user.houseRequirementRadio);
        values.put(COLUMN_requirement, user.requirement);
        values.put(COLUMN_pattadarName, user.pattadarName);
        values.put(COLUMN_dagNo, user.dagNo);
        values.put(COLUMN_pattaNo, user.pattaNo);
        values.put(COLUMN_landAreaPatta, user.landAreaPatta);
        values.put(COLUMN_landLength, user.landLength);
        values.put(COLUMN_landBreadth, user.landBreadth);
        values.put(COLUMN_employmentStatus, user.employmentStatus);
        values.put(COLUMN_employmentStatusName, user.employmentStatusName);
        values.put(COLUMN_averageIncome, user.averageIncome);
        values.put(COLUMN_incomeProof, user.incomeProof);
        values.put(COLUMN_bplRadio, user.bplRadio);
        values.put(COLUMN_bplNo, user.bplNo);
        values.put(COLUMN_rationRadio, user.rationRadio);
        values.put(COLUMN_rationCardNo, user.rationCardNo);

        // geo coordinates
        if(user.geoLatitude != null && !"0".equals(user.geoLatitude)) {
            values.put(COLUMN_bplNo, user.geoLatitude);
        }
        if(user.geoLongitude != null && !"0".equals(user.geoLongitude)) {
            values.put(COLUMN_rationCardNo, user.geoLongitude);
        }
        values.put(COLUMN_preferredAssistanceHfa, user.preferredAssistanceHfa);
        values.put(COLUMN_wardDetails, user.wardDetails);

        values.put(COLUMN_bankName, user.bankName);
        values.put(COLUMN_bankBranchName, user.bankBranchName);
        values.put(COLUMN_bankIfscCode, user.bankIfscCode);
        values.put(COLUMN_familyMemberName, user.familyMemberName);
        values.put(COLUMN_familyMemberRelation, user.familyMemberRelation);
        values.put(COLUMN_familyMemberGender, user.familyMemberGender);
        values.put(COLUMN_familyMemberAge, user.familyMemberAge);
        values.put(COLUMN_familyMemberIdCardNo, user.familyMemberIdCardNo);

        int rows = -1;
        // updating row
        if(isByAdhar) {
            if(user.adharNo != null) {
                rows = db.update(TABLE_USER, values, COLUMN_ADHAR_NO + " = ?",
                        new String[]{user.adharNo});
            } else  if(user.slumAdharNo != null) {
                rows = db.update(TABLE_USER, values, COLUMN_ADHAR_NO + " = ?",
                        new String[]{user.slumAdharNo});
            }
        } else {
            rows = db.update(TABLE_USER, values, COLUMN_SURVEY_ID + " = ?",
                    new String[]{user.surveyId});
        }

        Log.v("PMAY", "Update records : "+rows +"  isByAdhar "+isByAdhar + " slum adar "+user.slumAdharNo +" nonslum adar : "+user.adharNo);
        db.close();
    }

    /**
     * This method is to delete user record
     *
     * @param user
     */
    public void deleteOfflineSurveyByAadhar(AddSurveyRequest user) {
        Log.v("PMAY", " deleteOfflineSurveyByAadhar() DB - Enter " );
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_ADHAR_NO + " = ?",
                new String[]{user.adharNo});
        db.close();
        Log.v("PMAY", "deleteOfflineSurveyByAadhar() from DB -  delelted : "+user.adharNo);
    }

    public void deleteAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        int delelted = db.delete(TABLE_USER, COLUMN_SURVEY_ID + " > ?",
                new String[]{"0"});
        Log.v("PMAY", "deleteAllRecords() from DB -  delelted : "+delelted);
        db.close();
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_APPLICANT_NAME
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_SURVEY_ID + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
    public boolean checkUser(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_APPLICANT_NAME
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_SURVEY_ID + " = ?" + " AND " + COLUMN_FATHER_NAME + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

}
