package com.os.appparent.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.os.appparent.R;
import com.os.appparent.api.ApiCallGraph;
import com.os.appparent.ultility.Ultility;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity {

    private EditText txtFrom;
    private EditText txtEnd;
    private EditText txtDur;
    private EditText txtIT;
    private EditText txtSum;
    private Button btnSave;
    private int state;
    private int count;
    private Intent intent;
    private String access_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mappingControls();
        setUpData();
        addEvent();
    }

    private void setUpData() {
        intent = getIntent();
        count = intent.getIntExtra("count", -1);
        state = intent.getIntExtra("state", Ultility.STATE_LT);
        access_token = intent.getStringExtra("access_token");
    }

    private void addEvent() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eventChecking()==1) {
                    if(count == -1) {
                        returnIntent();
                    }
                    else {
                        // Check flag
                        ApiCallGraph.apiCallGraph.deleteFlag(access_token, "os").enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.code()==204) {
                                    returnIntent();
                                }
                                else printError();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                printError();
                            }
                        });
                    }
                }
            }
        });
    }
    private void printError() {
        Ultility.printDisplay(getApplicationContext(), "Đang có người khác chiếm quyền chỉnh sửa/ thêm lịch trình");
    }
    private void returnIntent() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("_from", txtFrom.getText().toString());
        returnIntent.putExtra("_end", txtEnd.getText().toString());
        returnIntent.putExtra("_duration", txtDur.getText().toString());
        returnIntent.putExtra("_interrupt_time", txtIT.getText().toString());
        returnIntent.putExtra("_sum", txtSum.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private int eventChecking() {
        if(txtFrom.getText().toString().length() !=5 || txtEnd.getText().toString().length() !=5) {
            Toast.makeText(getApplicationContext(),"Nhập sai định dạng dữ liệu",Toast.LENGTH_SHORT).show();
            return 0;
        }
        if(txtDur.getText().toString().length() > 4||
                txtIT.getText().toString().length() > 4||
                txtSum.getText().toString().length() > 4) {
            Toast.makeText(getApplicationContext(),"Nhập sai định dạng dữ liệu",Toast.LENGTH_SHORT).show();
            return 0;
        }
        String[] data = txtFrom.getText().toString().split(":");
        String[] data1 = txtEnd.getText().toString().split(":");
        if(data[0].length()!=2 || data1[0].length()!=2) {
            Toast.makeText(getApplicationContext(),"Nhập sai định dạng dữ liệu",Toast.LENGTH_SHORT).show();
            return 0;
        }
        if(TextUtils.isEmpty(txtFrom.getText())) {
            txtFrom.setText("0000");
        }
        if(TextUtils.isEmpty(txtEnd.getText())) {
            txtEnd.setText("0000");
        }
        if(TextUtils.isEmpty(txtDur.getText())) {
            txtDur.setText("0000");
        }
        if(TextUtils.isEmpty(txtIT.getText())) {
            txtIT.setText("0000");
        }
        if(TextUtils.isEmpty(txtSum.getText())) {
            txtSum.setText("0000");
        }
        return 1;
    }

    private void mappingControls() {
        txtFrom = findViewById(R.id.txtFrom);
        txtEnd = findViewById(R.id.txtEnd);
        txtDur = findViewById(R.id.txtDur);
        txtIT = findViewById(R.id.txtIT);
        txtSum = findViewById(R.id.txtSum);
        btnSave = findViewById(R.id.btnSave);
    }
}