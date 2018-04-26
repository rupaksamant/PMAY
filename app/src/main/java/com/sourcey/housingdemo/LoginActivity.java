package com.sourcey.housingdemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.util.Log;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pm.yojana.housingdemo.R;
import com.sourcey.housingdemo.restservice.APIClient;
import com.sourcey.housingdemo.restservice.APIInterface;
import com.sourcey.housingdemo.restservice.CreateNewAccount;
import com.sourcey.housingdemo.restservice.Credential;
import com.sourcey.housingdemo.restservice.ForgotPwdRequest;
import com.sourcey.housingdemo.restservice.LoginResponse;

import butterknife.ButterKnife;
import butterknife.Bind;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    boolean validEmail = true;
    boolean validPwd = true;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password)
    TextInputEditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    @Bind(R.id.forgot_pwd) AppCompatTextView forgot_pwd;
    @Bind(R.id.remember_chk) AppCompatCheckBox remember_chk;
    @Bind(R.id.show_pwd) AppCompatCheckBox show_pwd;
    LayoutInflater li;
    APIInterface apiInterface;
    SharedPreferences defaultPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        li = LayoutInflater.from(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

       /* show_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    _passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    _passwordText.setInputType(-1);
                    _passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });*/

        defaultPref = PreferenceManager.getDefaultSharedPreferences(this);

        forgot_pwd.setClickable(true);
        forgot_pwd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onForgotPwd();
            }
        });
        apiInterface = APIClient.getClient().create(APIInterface.class);

    }
    AlertDialog oTPAlertDialog ;
    AlertDialog confirmOTPAlertDialog;
    ForgotPwdRequest forgotPwdRequest;
    ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        boolean isRememberMeChecked = defaultPref.getBoolean("IsRememberMe", false);
        if(isRememberMeChecked) {
            String uname = defaultPref.getString("USER_NAME", "");
            String pwd = defaultPref.getString("PASSWORD", "");
            if(!uname.isEmpty() && !pwd.isEmpty()) {
                Credential cred = new Credential(uname, pwd);
                sendLoginRequest(cred, true);
            }
        }

    }


    void showOTPInputDialog() {

        View promptsView = li.inflate(R.layout.dialog_layout_otp, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // alertDialogBuilder.setCancelable(false);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.mobile_otp);

        final Button btnOtp = (Button) promptsView
                .findViewById(R.id.btn_generate_otp);
        //btnOtp.setText("Submit OTP");
        //userInput.setHint("Enter OTP");
        //userInput.set
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = userInput.getText().toString().trim();
                if(otp.isEmpty()) {
                    Toast.makeText(getApplicationContext(), " OTP can not be empty  ", Toast.LENGTH_LONG).show();
                } else {
                    forgotPwdRequest = new ForgotPwdRequest(forgotPwdRequest.mobileNumber, otp);
                    final Call<ResponseBody> call_otp = apiInterface.RequestValidateOTP(forgotPwdRequest);
                    userInput.setText("");
                    call_otp.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                           try {
                               if(response != null && response.isSuccessful()) {
                                   //btnOtp.setText("Submit OTP");
                                   //userInput.setHint("Enter OTP");
                                   if(confirmOTPAlertDialog != null ) {
                                       confirmOTPAlertDialog.dismiss();
                                   }

                                   if(response.body() != null ) {

                                       String str = response.body().string().toString();
                                       Log.v("PMAY", " Response received  otp confirmation :  " +str.contains("valid Otp"));
                                       if(str.contains("valid Otp")) {
                                           Toast.makeText(getApplicationContext(), "OTP is verified. Resetting your password", Toast.LENGTH_LONG).show();
                                           generatePassword();
                                       } else if(str.contains("invalid otp")) {
                                           Log.v("PMAY", " Response received  otp sent :  " +oTPAlertDialog);
                                           showOTPInputDialog();
                                           Toast.makeText(getApplicationContext(), "Invalid OTP ", Toast.LENGTH_LONG).show();
                                       }
                                   }

                               }
                           } catch (Exception e) {

                           }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Internal Server error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        confirmOTPAlertDialog = alertDialogBuilder.create();
        // show it
        confirmOTPAlertDialog.show();


    }

    private void onForgotPwd() {

        View promptsView = li.inflate(R.layout.dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // alertDialogBuilder.setCancelable(false);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.mobile_otp);

        final Button btnOtp = (Button) promptsView
                .findViewById(R.id.btn_generate_otp);
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = userInput.getText().toString().trim();
                if(otp.isEmpty()) {
                    Toast.makeText(getApplicationContext(), " Mobile number can not be empty  ", Toast.LENGTH_LONG).show();
                } else {
                    forgotPwdRequest = new ForgotPwdRequest(otp);
                    final Call<ResponseBody> call = apiInterface.RequestForgetPwd(forgotPwdRequest);
                    userInput.setText("");
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if(oTPAlertDialog != null ) {
                                    oTPAlertDialog.dismiss();
                                }

                                if(response != null && response.isSuccessful()) {

                                    if(response.body() != null ) {

                                        String str = response.body().string().toString();
                                        Log.v("PMAY", " Response received  otp sent :  " +str.contains("Sorry!"));
                                        if(str.contains("Sorry!")) {
                                            Toast.makeText(getApplicationContext(), "Sorry! No user is exist for this number  ", Toast.LENGTH_LONG).show();
                                            onForgotPwd();
                                        } else if(str.contains("successfully")) {
                                            Log.v("PMAY", " Response received  otp sent :  " +oTPAlertDialog);
                                          showOTPInputDialog();
                                        }
                                    }
                                }

                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.v("PMAY", " Response received  Error :  " +oTPAlertDialog);
                        }
                    });
                }
            }
        });
        oTPAlertDialog = alertDialogBuilder.create();
        // show it
        oTPAlertDialog.show();

    }

    void generatePassword() {

        final Call<ResponseBody> call = apiInterface.RequestGeneratePassword(new ForgotPwdRequest(forgotPwdRequest.mobileNumber));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    if(response != null && response.isSuccessful()) {

                        if(response.body() != null ) {
                            String str = response.body().string().toString();
                            Log.v("PMAY", " Response received   :  " +str.contains("password is sen"));
                            if(str.contains("password is sen")) {
                                Toast.makeText(getApplicationContext(), "Password is sent your registered mobile number  ", Toast.LENGTH_LONG).show();
                                //generatePassword();
                            } else  {
                                Log.v("PMAY", " Response received  otp sent :  " +oTPAlertDialog);
                                Toast.makeText(getApplicationContext(), "Sorry!, Unable send pasword your mobile number.  ", Toast.LENGTH_LONG).show();
                                onForgotPwd();
                            }
                        }
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("PMAY", " Response received  Error :  " +oTPAlertDialog);
            }
        });
    }

    public void sendLoginRequest(final Credential cred, final boolean isAutoLogin) {
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if(isAutoLogin)
            progressDialog.setMessage("Auto logging in...");
        else
            progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final Call<LoginResponse> call = apiInterface.loginRequest(cred);
        _emailText.setText("");
        _passwordText.setText("");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(progressDialog != null)
                    progressDialog.dismiss();
                SharedPreferences.Editor editor = defaultPref.edit();
                if(response != null && response.isSuccessful()) {
                    //response.body()
                    Log.v("PMAY", " Login Response received :  " +response.body().success);
                    if (response.body().success) {
                        if(response.body().userdtl != null) {
                            AddSurveyDataManager.getInstance().mAddSurveyRequest.userId = response.body().userdtl.userId;
                            editor.putString("USER_ID", response.body().userdtl.userId);
                            editor.commit();
                        }
                        Toast.makeText(getApplicationContext(), " login Success  ", Toast.LENGTH_LONG).show();
                        AddSurveyDataManager.getInstance().IsSyncEnabled = true;
                        onLoginSuccess();
                        // For auto login
                        if(remember_chk.isChecked() && defaultPref != null) {
                            editor.putBoolean("IsRememberMe", remember_chk.isChecked());
                            editor.putString("USER_NAME", cred.userId);
                            editor.putString("PASSWORD", cred.password);
                            editor.commit();
                        } else {
                            if(!isAutoLogin) {
                                editor.putBoolean("IsRememberMe", remember_chk.isChecked());
                                editor.putString("USER_NAME", "");
                                editor.putString("PASSWORD", "");
                                editor.commit();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter a valid user name or password", Toast.LENGTH_LONG).show();
                        editor.putString("USER_NAME", "");
                        editor.putString("PASSWORD", "");
                        editor.commit();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), " login failed. Please try again  ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if(progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), " Application is in offline mode ", Toast.LENGTH_SHORT).show();
                //remove this
                onLoginSuccess();
            }

        });


    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(true);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        Credential cred = new Credential(email, password);
        sendLoginRequest(cred, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //new PermissionUtil().showAttachmentChooserDialog(this);
        //showOTPInputDialog();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {

        if(!validEmail && !validPwd) {
            Toast.makeText(getBaseContext(), "Enter valid user name and Password", Toast.LENGTH_LONG).show();
        } else if(validPwd && validEmail) {
            Toast.makeText(getBaseContext(), "User name or Password is incorrect", Toast.LENGTH_LONG).show();
        } else if(validPwd && !validEmail) {
            Toast.makeText(getBaseContext(), "Please enter a valid user name", Toast.LENGTH_LONG).show();
        } else if(!validPwd && validEmail) {
            Toast.makeText(getBaseContext(), "Please enter a valid password", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();
        }
        _loginButton.setEnabled(true);
    }

    public boolean validate() {

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        _emailText.setError(null);
        _passwordText.setError(null);
        if (email.isEmpty()) {
            _emailText.setError("Please enter a valid mobile number/email id");
            validEmail = false;
        } /*else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            validEmail = false;
        }*/ else {
            _emailText.setError(null);
            validEmail = true;
        }

        if (password.isEmpty()) {
            _passwordText.setError("Please enter a valid password");
            validPwd = false;
        } else if (password.length() < 4) {
            _passwordText.setError("Password should be minimum of 4 characters");
            validPwd = false;
        } else {
            _passwordText.setError(null);
            validPwd = true;
        }

        return (validPwd && validEmail);
    }
}
