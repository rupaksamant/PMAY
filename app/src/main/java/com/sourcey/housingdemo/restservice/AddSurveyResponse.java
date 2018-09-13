package com.sourcey.housingdemo.restservice;

import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;

/**
 * Created by Biswajit on 08-03-2018.
 */

public class AddSurveyResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("surveyId")
    public String surveyId;

    @SerializedName("exception")
    public String exception;

    @SerializedName("message")
    public String message;

    @SerializedName("code")
    public String status;

}
