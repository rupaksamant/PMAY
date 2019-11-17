package com.sourcey.housingdemo.restservice;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Biswajit on 03-03-2018.
 */

public class AddSurveyRequest {

    @SerializedName("ulbNameId")
    public String ulbNameId;

    @SerializedName("slumUlbNameId")
    public String slumUlbNameId;

    @SerializedName("slumWardDetails")
    public String slumWardDetails;

    @SerializedName("userId")
    public String userId;

    @SerializedName("geoLongitude")
    public String geoLongitude = "0";

    @SerializedName("geoLatitude")
    public String geoLatitude = "0";

    @SerializedName("slumRadio")
    public String  slumRadio;

    //@SerializedName("surveyData")
    //public String  surveyData = "surveyData";

    @SerializedName("validationPendingStatus")
    public String validationPendingStatus;

    @SerializedName("familyHeadName")
    public String familyHeadName;

    @SerializedName("fatherHusbandName")
    public String fatherHusbandName;

    @SerializedName("adharNo")
    public String  adharNo;

    @SerializedName("adharReason")
    public String adharReason;

    @SerializedName("contactNo")
    public String contactNo;

    @SerializedName("maritalStatus")
    public String maritalStatus;

    @SerializedName("eligibleStatus")
    public String eligibleStatus;

    @SerializedName("nonEligibleReason")
    public String nonEligibleReason;

    @SerializedName("genderId")
    public String genderId;

    @SerializedName("idType")
    public String idType;

    @SerializedName("idNo")
    public String idNo;

    @SerializedName("dob")
    public String dob;

    @SerializedName("religion")
    public String religion;

    @SerializedName("religionIfOther")
    public String religionIfOther;

    @SerializedName("caste")
    public String caste;

    @SerializedName("presentTown")
    public String presentTown;

    @SerializedName("presentHouseNo")
    public String presentHouseNo;

    @SerializedName("presentStreetName")
    public String presentStreetName;

    @SerializedName("presentCity")
    public String presentCity;

    @SerializedName("presentMobileNo")
    public String presentMobileNo;

    @SerializedName("isSameAsPresentAdd")
    public String isSameAsPresentAdd;

    @SerializedName("permanentTown")
    public String permanentTown;

    @SerializedName("permanentHouseNo")
    public String permanentHouseNo;

    @SerializedName("permanentStreetName")
    public String permanentStreetName;

    @SerializedName("permanentCity")
    public String permanentCity;

    @SerializedName("permanentMobileNo")
    public String permanentMobileNo;

    @SerializedName("houseRoofType")
    public String houseRoofType;

    @SerializedName("ownsRadio")
    public String ownsRadio;

    @SerializedName("landinSqm")
    public String  landinSqm;

    @SerializedName("landLocation")
    public String  landLocation;

    @SerializedName("ownershipHouse")
    public String ownershipHouse;

    @SerializedName("houseWallType")
    public String houseWallType;

    @SerializedName("dwellinUnitRoom")
    public String dwellinUnitRoom;

    @SerializedName("sizeExistingDwelling")
    public String sizeExistingDwelling;

    @SerializedName("houseRequirementRadio")
    public String houseRequirementRadio;

    @SerializedName("requirement")
    public String requirement;

    @SerializedName("pattadarName")
    public String pattadarName;

    @SerializedName("dagNo")
    public String dagNo;

    @SerializedName("pattaNo")
    public String pattaNo;

    @SerializedName("landAreaPatta")
    public String landAreaPatta;

    @SerializedName("landLength")
    public String landLength;

    @SerializedName("landBreadth")
    public String landBreadth;

    @SerializedName("employmentStatus")
    public String employmentStatus;

    @SerializedName("employmentStatusName")
    public String employmentStatusName;

    @SerializedName("averageIncome")
    public String averageIncome;

    @SerializedName("incomeProof")
    public String incomeProof;

    @SerializedName("bplRadio")
    public String bplRadio;

    @SerializedName("bplNo")
    public String bplNo;

    @SerializedName("rationRadio")
    public String rationRadio;

    @SerializedName("rationCardNo")
    public String rationCardNo;

    @SerializedName("surveyId")
    public String surveyId;

    @SerializedName("preferredAssistanceHfa")
    public String preferredAssistanceHfa;

    @SerializedName("wardDetails")
    public String wardDetails;

    @SerializedName("vehicleCategoryId")
    public String vehicleCategoryId;

    @SerializedName("vehicleRegdNo")
    public String vehicleRegdNo;

    @SerializedName("bankAccNo")
    public String bankAccNo;

    @SerializedName("bankName")
    public String bankName;

    @SerializedName("bankBranchName")
    public String bankBranchName;

    @SerializedName("bankIfscCode")
    public String bankIfscCode;

    @SerializedName("familyMemberName")
    public String familyMemberName;

    @SerializedName("familyMemberRelation")
    public String familyMemberRelation;

    @SerializedName("familyMemberGender")
    public String familyMemberGender;

    @SerializedName("familyMemberAge")
    public String familyMemberAge;

    @SerializedName("familyMemberIdCardNo")
    public String familyMemberIdCardNo;

    @SerializedName("isSubmitted")
    public String isSubmitted;

    @SerializedName("biometricDetails")
    public byte[] biometricDetails;

    @SerializedName("slumBiometricDetails")
    public byte[] slumBiometricDetails;

        // Slum fields

    @SerializedName("chckSlumRadio")
    public String chckSlumRadio;

    @SerializedName("slumFamilyHeadName")
    public String slumFamilyHeadName;

    @SerializedName("slumFatherHusbandName")
    public String slumFatherHusbandName;

    @SerializedName("slumAdharNo")
    public String slumAdharNo;

    @SerializedName("isSubmittedFlag")
    public String isSubmittedFlag;

    @SerializedName("genderValue")
    public String  genderValue ;

    @SerializedName("slumPresentTown")
    public String   slumPresentTown ;

    @SerializedName("slumPresentHouseNo")
    public String slumPresentHouseNo;

    @SerializedName("slumPresentStreetName")
    public String  slumPresentStreetName ;

    @SerializedName("slumPresentCity")
    public String slumPresentCity ;

    @SerializedName("slumIsSameAsPresentAdd")
    public String slumIsSameAsPresentAdd ;

    @SerializedName("slumPermanentTown")
    public String  slumPermanentTown ;

    @SerializedName("slumPermanentHouseNo")
    public String slumPermanentHouseNo ;

    @SerializedName("slumPermanentStreetName")
    public String  slumPermanentStreetName ;

    @SerializedName("slumPermanentCity")
    public String slumPermanentCity ;

    @SerializedName("slumPermanentMobileNo")
    public String  slumPermanentMobileNo ;

    @SerializedName("slumReligion")
    public String slumReligion ;

    @SerializedName("slumCaste")
    public String  slumCaste;

    @SerializedName("slumOwnsRadio")
    public String slumOwnsRadio;

    @SerializedName("slumLandAddress")
    public String slumLandAddress ;

    @SerializedName("slumLandinSqm")
    public String slumLandinSqm ;

   /* @SerializedName("slumBiometricDetails")
    public String slumBiometricDetails ;*/

    public  boolean isNewRecord = true;
    public  boolean isHousePicUploaded;
    public  boolean isFingerPrintUploaded;
}
