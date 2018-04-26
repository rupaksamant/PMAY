package com.sourcey.housingdemo.restservice;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Biswajit on 28-02-2018.
 */

public class ForgotPwdRequest {

    @SerializedName("mobileNo")
    public String mobileNumber;

    @SerializedName("otp")
    public String otp;


    public ForgotPwdRequest(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public ForgotPwdRequest(String mobileNumber, String otp) {
        this.mobileNumber = mobileNumber;
        this.otp = otp;
    }

}
