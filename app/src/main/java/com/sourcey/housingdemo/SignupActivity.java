package com.sourcey.housingdemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pm.yojana.housingdemo.R;
import com.sourcey.housingdemo.restservice.APIClient;
import com.sourcey.housingdemo.restservice.APIInterface;
import com.sourcey.housingdemo.restservice.CreateNewAccount;
import com.sourcey.housingdemo.restservice.Credential;
import com.sourcey.housingdemo.restservice.RegisterUserResponse;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_address) EditText _addressText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_mobile) EditText _mobileText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.input_middle_name) EditText input_middle_name;

    @Bind(R.id.btn_signup) Button _signupButton;

    Button btn_cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        btn_cancel = (Button)findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                /*Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);*/
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        apiInterface = APIClient.getClient().create(APIInterface.class);


    }

    void showAccountCreationRespDialog(final boolean isSuccess) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,
                R.style.AppTheme_Dark_Dialog);
        //alertDialog.setIndeterminate(true);
        alertDialog.setTitle("Error");
        alertDialog.setCancelable(false);
        alertDialog.setMessage(R.string.register_fail_message);
        if(isSuccess) {
            alertDialog.setTitle(R.string.confirm_title);
            alertDialog.setMessage(R.string.confirm_message);
        }
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onSignupSuccess(isSuccess);
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //showAccountCreationRespDialog(false);
    }

    void sendRegisterRequest(final CreateNewAccount cr) {
        final Call<RegisterUserResponse> call = apiInterface.registerRequest(cr);
        call.enqueue(new Callback<RegisterUserResponse>() {
            @Override
            public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {
                if(progressDialog != null) {
                    progressDialog.dismiss();
                }

                if(response != null && response.isSuccessful()) {
                    Log.v("PMAY", " Register Response received :  " +response.body().success);
                    showAccountCreationRespDialog(response.body().success);
                } else {
                    showAccountCreationRespDialog(false);

                }
            }

            @Override
            public void onFailure(Call<RegisterUserResponse> call, Throwable t) {
                if(progressDialog != null) {
                    progressDialog.dismiss();
                }
               Toast.makeText(getApplicationContext(), " Unexpected Error, try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    CreateNewAccount crRequest;
    APIInterface apiInterface;
    ProgressDialog progressDialog;

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);


        String firstName = _nameText.getText().toString().trim();
        String mid_name = input_middle_name.getText().toString().trim();
        String lastName = _addressText.getText().toString().trim();
        String email = _emailText.getText().toString().trim();
        String mobile = _mobileText.getText().toString().trim();
        String ulbName = _passwordText.getText().toString().trim();;
        String ulbNumber = _reEnterPasswordText.getText().toString().trim();

        // TODO: Implement your own signup logic here.

         progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering ...");
        progressDialog.show();

        crRequest = new CreateNewAccount(ulbName, ulbNumber, firstName,
                mid_name, lastName, mobile, email);
        sendRegisterRequest(crRequest);

        resetAllOnSubmit();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        // onSignupFailed();
                        //alertDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess(boolean isSuccess) {
        //Toast.makeText(getBaseContext(), "Account creation is successful ", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        if(isSuccess) {
            setResult(RESULT_OK, null);
            finish();
        }
    }

    public void onSignupFailed() {
        //Toast.makeText(getBaseContext(), "Please provide valid data", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public void resetAllOnSubmit() {
        _nameText.setText("");
        _addressText.setText("");
        input_middle_name.setText("");
        _emailText.setText("");
        _mobileText.setText("");
        _passwordText.setText("");
        _reEnterPasswordText.setText("");
    }

    public boolean validate() {
        boolean valid = true;

        String firstName = _nameText.getText().toString().trim();
        String mid_name = input_middle_name.getText().toString().trim();
        String lastName = _addressText.getText().toString().trim();
        String email = _emailText.getText().toString().trim();
        String mobile = _mobileText.getText().toString().trim();
        String ulbName = _passwordText.getText().toString().trim();;
        String ulbNumber = _reEnterPasswordText.getText().toString().trim();

        if (firstName.isEmpty() || firstName.length() < 3) {
            _nameText.setError("First name should be atleast 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (lastName.isEmpty()) {
            _addressText.setError("Enter a Valid Last name");
            valid = false;
        } else {
            _addressText.setError(null);
        }


        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            _mobileText.setError("Enter a Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        /*if (ulbName.isEmpty() || ulbName.length() <1 ) {
            _passwordText.setError("Enter a valid ULB Name");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (ulbNumber.isEmpty() || ulbNumber.length() < 1) {
            _reEnterPasswordText.setError("Enter a valid ULB number");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }
*/
        return valid;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, null);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}