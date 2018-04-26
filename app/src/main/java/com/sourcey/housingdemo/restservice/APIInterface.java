package com.sourcey.housingdemo.restservice;

import com.sourcey.housingdemo.UserDetails;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Biswajit on 25-02-2018.
 */

public interface APIInterface {
  //@Body /*Map<String, AddSurveyRequest> body*/
    @FormUrlEncoded
    @POST("/login")
    Call<Credential> login(@Field("userId") String userId, @Field("password") String password);

    @Multipart
    @POST("/addSurvey")
    Call<ResponseBody> uploadSurveyData(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part applicantPhoto,
            @Part("surveyData") AddSurveyRequest addSurveyRequest);

    @Multipart
    @POST("/addSurvey")
    Call<ResponseBody> addSurveyDataTest(/*@Part("surveyData") RequestBody description, */@Part("surveyData") AddSurveyRequest addSurveyRequest /*"surveyData") Map<String, AddSurveyRequest> body*/
                                       /*@Part("surveyData") AddSurveyRequest addSurveyRequest*/);
  @Multipart
  @POST("/addSurvey")
  Call<ResponseBody> addSurveyDataTrial(@Part("surveyData") RequestBody description, @Part /*AddSurveyRequest addSurveyRequest *//*"surveyData")*/ Map<String, AddSurveyRequest> body
                                       /*@Part("surveyData") AddSurveyRequest addSurveyRequest*/);

    @Multipart
    @POST("/addSurvey")
    Call<AddSurveyResponse> uploadMultiFilesSurveyData(
            @Part("biometricDetails") RequestBody description,
            @Part List<MultipartBody.Part> applicantAttachments,
            @Part("surveyData") AddSurveyRequest addSurveyRequest);


  @Multipart
  @POST("/addSurvey")
  Call<AddSurveyResponse> uploadMultiFilesSurveyData(
          @Part List<MultipartBody.Part> applicantAttachments,
          @Part("surveyData") AddSurveyRequest addSurveyRequest);



    // SLUM DATA

  @Multipart
  @POST("/addSlumSurvey")
  Call<AddSurveyResponse> uploadMultiFilesSurveyDataSlum(
            @Part("slumBiometricDetails") RequestBody description,
            @Part List<MultipartBody.Part> applicantAttachments,
            @Part("surveyData") AddSurveyRequest addSurveyRequest);

  @Multipart
  @POST("/addSlumSurvey")
  Call<AddSurveyResponse> uploadMultiFilesSurveyDataSlum(
          @Part List<MultipartBody.Part> applicantAttachments,
          @Part("surveyData") AddSurveyRequest addSurveyRequest);


    @POST("/login")
    Call<LoginResponse> loginRequest(@Body Credential cred);

  @GET("/logout")
  Call<ResponseBody> logoutRequest();

    @POST("/register")
    Call<RegisterUserResponse> registerRequest(@Body CreateNewAccount createNewAccount);


    @POST("/forgetpassword")
    Call<ResponseBody> RequestForgetPwd(@Body ForgotPwdRequest forgetPwd);


    @POST("/getSurveyUserReport")
    Call<List<SurveyDataResponse>> getSurveyUserReportFromServer(@Body UserDetails user);

    @POST("/validateOtp")
    Call<ResponseBody> RequestValidateOTP(@Body ForgotPwdRequest forgetPwd);


    @POST("/generatePassword")
    Call<ResponseBody> RequestGeneratePassword(@Body ForgotPwdRequest forgetPwd);

    //@POST("/generatePassword")

    //@POST("/generatePassword")




}
