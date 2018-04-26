package com.sourcey.housingdemo.restservice;

import com.google.gson.annotations.SerializedName;

import java.util.Random;

/**
 * Created by Biswajit on 26-02-2018.
 */

public class CreateNewAccount {
    @SerializedName("ulbName")
    public String ulbName;

    @SerializedName("ulbNo")
    public String ulbNo;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("middleName")
    public String middleName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("mobileNo")
    public String mobileNumber;

    @SerializedName("emailId")
    public String emailId;

    @SerializedName("userId")
    public int userId;

    public CreateNewAccount(String ulbName, String ulbNo, String firstName, String middleName,
                            String lastName, String mobileNumber, String emailId) {
        if(mobileNumber.trim().length() >=4) {
            this.ulbName = "ULB"+mobileNumber.substring((mobileNumber.trim().length()-4));
        }
        if(mobileNumber.trim().length() > 0) {
            this.ulbNo = mobileNumber;
        }
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.emailId = emailId;
        Random random = new Random();
        this.userId = random.nextInt(10000);
    }
}
