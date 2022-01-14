package com.os.appparent.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.os.appparent.R;
import com.os.appparent.api.ApiCallGraph;
import com.os.appparent.ultility.Ultility;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ImageButton btnLogin;
    private EditText txtPassword;

    private ISingleAccountPublicClientApplication mSingleAccountApp = null;
    private String accessToken;
    private String[] scopes;

    private String password;
    private SharedPreferences sharedPref;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mappingControls();

        setUpData();
        addEvents();
    }

    private void addEvents() {
        //Creates a PublicClientApplication object with res/raw/auth_config_single_account.json
        createPublicClientApplication();

        //Login Action
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(txtPassword.getText()))
                    Ultility.printDisplay(getApplicationContext(), "Mật khẩu trống");
                else
                    eventLogin();
            }
        });
    }

    private void eventLogin() {
        //login onedrive
        if(password.equals("null"))
        mSingleAccountApp.signIn(LoginActivity.this, null, scopes, new AuthenticationCallback() {
            @Override
            public void onCancel() {
                displayLoginError();
            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                getAccessToken();
            }

            @Override
            public void onError(MsalException exception) {
                displayLoginError();
            }
        });
        else
            getAccessToken();
    }

    private void getAccessToken() {
        displayDialog();
        mSingleAccountApp.acquireToken(LoginActivity.this, scopes, new AuthenticationCallback() {
            @Override
            public void onCancel() {
                cancelDialog();
            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                accessToken = authenticationResult.getAccessToken();
                cancelDialog();
                createFlag("os");
                checkPasswordLogin();
            }

            @Override
            public void onError(MsalException exception) {
                cancelDialog();
            }
        });

    }
    private void createFlag(String path) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),Ultility.JSON_CODE_CREATE_FOLDER);
        ApiCallGraph.apiCallGraph.createFlag(accessToken, body, path).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Ultility.printDisplay(getApplicationContext(),"Tạo flag thành công");
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Ultility.printDisplay(getApplicationContext(), "Đã có lỗi xảy ra, mời kiểm tra onedrive");
            }
        });
    }
    private void checkPasswordLogin() {
        if(password.equals("null")){
            writeNewPassword();
            return;
        }
        if(!password.equals(txtPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), password, Toast.LENGTH_SHORT).show();
            Ultility.printDisplay(getApplicationContext(), "Mật khẩu không chính xác");
        }
        else
            changeActivityDisplay();
    }

    private void writeNewPassword() {

        RequestBody body =
                RequestBody.create(MediaType.parse("text/plain"), txtPassword.getText().toString());

        ApiCallGraph.apiCallGraph.writeOneDrive(accessToken, body, "os/parent-password.txt").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("ALOO", String.valueOf(response.code()));
                if(response.code()==200 || response.code() == 201) {
                    savePasswordSharedPref();
                    changeActivityDisplay();
                } else
                    displayLoginError();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                displayLoginError();
            }
        });
        RequestBody body1 =
                RequestBody.create(MediaType.parse("text/plain"), "0");

        ApiCallGraph.apiCallGraph.writeOneDrive(accessToken, body1, "os/state.txt").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Ultility.printDisplay(getApplicationContext(), "Tạo state thành công");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                displayLoginError();
            }
        });
    }

    private void savePasswordSharedPref() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("password", txtPassword.getText().toString());
        editor.commit();
    }

    private void changeActivityDisplay() {
        Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
        intent.putExtra("token", accessToken);
        startActivity(intent);
    }

    private void cancelDialog() {
        mProgressDialog.cancel();
    }

    private void displayDialog() {
        mProgressDialog.show();
    }

    private void displayLoginError() {
        Ultility.printDisplay(getApplicationContext(), "Đăng nhập không thành công");
    }

    private void createPublicClientApplication() {
        PublicClientApplication.createSingleAccountPublicClientApplication(LoginActivity.this,
                R.raw.auth_config_single_account,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mSingleAccountApp = application;
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.d("login_activity", exception.toString());
                        Ultility.printDisplay(getApplicationContext(), "Lỗi xác nhận PublicClientApplication");
                    }
                });
    }

    private void setUpData() {
        // Permission
        scopes = new String[]{"Files.ReadWrite.All", "User.Read"};
        sharedPref = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        //Dialog waiting
        mProgressDialog = new ProgressDialog(this);
        //Set up sharedPre
        sharedPref = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        password = sharedPref.getString("password", "null");
    }

    private void mappingControls() {
        btnLogin = findViewById(R.id.btnLogin);
        txtPassword = findViewById(R.id.txtPassword);
    }
}