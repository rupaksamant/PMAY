package com.sourcey.housingdemo.modal;

/**
 * Biswajit.
 */

public class SurveyDataModal {
    public String mName;
    public String mAdharNo;
    public String mSurveyId;
    public String mFatherName;
    public String mBankAccNo;
    public String mSlum; // "S" or "N"
    public String isSubmitted; // S or N or O(offline)

    public SurveyDataModal(String name, String adharNo, String surveyId, String fName, String accNo, String isslum){
        mName = name;
        mAdharNo = adharNo;
        mSurveyId = surveyId;
        mFatherName = fName;
        mBankAccNo = accNo;
        this.mSlum = isslum;
    }

    public SurveyDataModal() {

    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPhoneNumber() {
        return mAdharNo;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mAdharNo = mPhoneNumber;
    }
}
