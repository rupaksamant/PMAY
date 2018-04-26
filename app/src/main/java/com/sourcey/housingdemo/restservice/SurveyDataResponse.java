package com.sourcey.housingdemo.restservice;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Biswajit on 31-03-2018.
 */

public class SurveyDataResponse {

    @SerializedName("userSurveyId")
    public String userSurveyId;

    @SerializedName("wardId")
    public String wardId;

    @SerializedName("slumNonSlum")
    public String slumNonSlum; // S or N

    @SerializedName("eligibilityForScheme")
    public String eligibilityForScheme; // Y or N

    @SerializedName("familyHead")
    public String familyHead;

    @SerializedName("fatherOrHusbandName")
    public String fatherOrHusbandName;

    @SerializedName("aadharCardNumber")
    public String aadharCardNumber;

    @SerializedName("reasonforAAdharNotAvailable")
    public String reasonforAAdharNotAvailable;

    @SerializedName("genderId")
    public String genderId; // 1 or 2 or 3

    @SerializedName("dob")
    public String dob;

    @SerializedName("reasonForNonEligibility")
    public String reasonForNonEligibility;

    @SerializedName("submittedData")
    public String submittedData; // Y or N

    @SerializedName("ulbNameId")
    public String ulbNameId; // 1, 2 , 3 etc

    @SerializedName("validationPendingNonSlum")
    public String validationPendingNonSlum;

    @SerializedName("slumNonSlumStatus")
    public String slumNonSlumStatus; // Slum OR Non-Slum

    @SerializedName("hfaCategoryId")
    public String hfaCategoryId;

    @SerializedName("photoAttachmentInFrontOfHouse")
    public String photoAttachmentInFrontOfHouse;

}
