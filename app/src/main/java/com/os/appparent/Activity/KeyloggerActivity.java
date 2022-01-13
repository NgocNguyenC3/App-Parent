package com.os.appparent.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.os.appparent.R;
import com.os.appparent.api.ApiCallGraph;
import com.os.appparent.model.DriveItem;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeyloggerActivity extends AppCompatActivity {
    private TextView txtKeyLogger;
    private Intent intent;
    private String accessToken;
    private String date;
    private int curr_index = 0;
    private ProgressDialog mProgressDialog;
    private String stack = "null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keylogger);
        getData();
        mappingControls();
        events();
    }

    private void events() {
        mProgressDialog.show();
        //Get keylogger
        ApiCallGraph.apiCallGraph.getDriveItem(accessToken, "os/management/"+ date + "/keylogger.txt").enqueue(new Callback<DriveItem>() {
            @Override
            public void onResponse(Call<DriveItem> call, Response<DriveItem> response) {
                if(response.code()==200) {
                    stack = "";
                    GetTextKeylogger getTextKeylogger = new GetTextKeylogger();
                    getTextKeylogger.execute(response.body().getDownUrl());
                }
                else
                    setTextLine("Không có dữ liệu");
            }

            @Override
            public void onFailure(Call<DriveItem> call, Throwable t) {
                setTextLine("Không có dữ liệu");
            }
        });
    }

    private void setTextLine(String token) {
        mProgressDialog.cancel();
        txtKeyLogger.setText(token);
    }

    private void getData() {
        intent = getIntent();
        accessToken = intent.getStringExtra("access_token");
        date = intent.getStringExtra("name").replace('/','-');
        mProgressDialog = new ProgressDialog(this);
    }

    private void mappingControls() {
        txtKeyLogger = findViewById(R.id.txtKeyLogger);
    }
    private class GetTextKeylogger extends AsyncTask<String, String, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... mUrl) {
            try {
                String data ="";
                URL url = new URL(mUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                while((data = bufferedReader.readLine()) !=null) {
                    stack += data;
                }
                input.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setTextLine(stack);
        }
    }
}