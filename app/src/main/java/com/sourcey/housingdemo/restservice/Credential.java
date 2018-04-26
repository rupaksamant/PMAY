package com.sourcey.housingdemo.restservice;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Biswajit on 25-02-2018.
 */

public class Credential {
    @SerializedName("userId")
    public String userId;
    @SerializedName("password")
    public String password;

    public Credential(String userId, String password) {
        this.userId = userId;
        this.password = password;

    }
}
