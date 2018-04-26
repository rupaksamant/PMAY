package com.sourcey.housingdemo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Biswajit on 09-03-2018.
 */

public class UserDetails {
    @SerializedName("roleId")
    public String roleId;

    @SerializedName("userId")
    public String userId;

    @SerializedName("roleName")
    public String roleName;
}
