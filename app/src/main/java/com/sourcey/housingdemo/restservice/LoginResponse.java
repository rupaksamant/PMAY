package com.sourcey.housingdemo.restservice;

import com.google.gson.annotations.SerializedName;
import com.sourcey.housingdemo.UserDetails;

/**
 * Created by Biswajit on 27-02-2018.
 */

public class LoginResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("userdtl")
    public UserDetails userdtl;
}
