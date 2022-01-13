package com.os.appparent.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.os.appparent.R;
import com.os.appparent.api.ApiCallGraph;
import com.os.appparent.ultility.Ultility;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditScheduleActivity extends AppCompatActivity {
    private TextView txtTitle;
    private TextView txtDate;
    private EditText txtFrom;
    private EditText txtEnd;
    private EditText txtDur;
    private EditText txtIT;
    private EditText txtSum;
    private Button btnSave;
    private Button btnViewCapture;
    private ImageButton btnHistory;
    private Intent intent;

    private String date;
    private String from;
    private String end;
    private String duration;
    private String interrupt_time;
    private String sum;
    private String accessToken;
    private int pos;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
        mappingControls();
        setUpData();
        setDataView();

        addEvents();
    }

    private void addEvents() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check flag and state
                if(state == Ultility.STATE_LT)
                    ApiCallGraph.apiCallGraph.deleteFlag(accessToken, "os/management/" +date).enqueue(new Callback<ResponseBody>() {
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

                if(state == Ultility.STATE_SCHEDULE)
                    ApiCallGraph.apiCallGraph.deleteFlag(accessToken, "os").enqueue(new Callback<ResponseBody>() {
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
        });

        // View keylogger
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIntentKeylogger();
            }
        });
        // View All image in folder capture
        btnViewCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIntentCapture();
            }
        });
    }

    private void returnIntent() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("_date", txtDate.getText().toString());
        returnIntent.putExtra("_from", txtFrom.getText().toString());
        returnIntent.putExtra("_end", txtEnd.getText().toString());
        returnIntent.putExtra("_duration", txtDur.getText().toString());
        returnIntent.putExtra("_interrupt_time", txtIT.getText().toString());
        returnIntent.putExtra("_sum", txtSum.getText().toString());
        returnIntent.putExtra("position", pos);
        returnIntent.putExtra("state", state);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void startIntentCapture() {
        Intent intent = new Intent(EditScheduleActivity.this, CaptureActivity.class);
        intent.putExtra("access_token", accessToken);
        intent.putExtra("name",date);
        startActivity(intent);
    }

    private void startIntentKeylogger() {
        Intent intent = new Intent(EditScheduleActivity.this, KeyloggerActivity.class);
        intent.putExtra("access_token", accessToken);
        intent.putExtra("name",date);
        startActivity(intent);
    }
    private void printError() {
        Ultility.printDisplay(getApplicationContext(), "Đang có người khác chiếm quyền chỉnh sửa/ thêm lịch trình");
    }
    private void setDataView() {
        if(!date.equals("")) {
            txtDate.setText(date);
        }
        else {
            txtDate.setVisibility(View.INVISIBLE);
            btnViewCapture.setVisibility(View.INVISIBLE);
            btnHistory.setVisibility(View.INVISIBLE);
        }
        txtTitle.setText("Chỉnh sửa thông tin");
        txtDur.setText(String.valueOf(Integer.valueOf(duration)));
        txtFrom.setText(from);
        txtEnd.setText(end);
        txtIT.setText(String.valueOf(Integer.valueOf(interrupt_time)));
        txtSum.setText(String.valueOf(Integer.valueOf(sum)));
    }
    //Check info
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
    private void setUpData() {
        intent = getIntent();
        date = intent.getStringExtra("date").replace('/','-');;
        state = intent.getIntExtra("current_tab", -1);
        from = intent.getStringExtra("from");
        end = intent.getStringExtra("end");
        duration = intent.getStringExtra("duration");
        interrupt_time = intent.getStringExtra("interrupt_time");
        sum = intent.getStringExtra("sum");
        pos = intent.getIntExtra("pos", -1);
        accessToken = intent.getStringExtra("access_token");
    }

    private void mappingControls() {
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        txtFrom = findViewById(R.id.txtFrom);
        txtEnd = findViewById(R.id.txtEnd);
        txtDur = findViewById(R.id.txtDur);
        txtIT = findViewById(R.id.txtIT);
        txtSum = findViewById(R.id.txtSum);
        btnSave = findViewById(R.id.btnSave);
        btnViewCapture = findViewById(R.id.btnViewCapture);
        btnHistory = findViewById(R.id.btnHistory);
    }
}