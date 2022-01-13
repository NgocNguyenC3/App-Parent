package com.os.appparent.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.os.appparent.R;
import com.os.appparent.adapter.ScheduleNonClickAbleAdapter;
import com.os.appparent.api.ApiCallGraph;
import com.os.appparent.model.TimeScheduleFake;
import com.os.appparent.ultility.Ultility;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLTActivity extends AppCompatActivity {
    private EditText date;
    private RecyclerView ryc;
    private ImageButton btnAdd;
    private ImageButton btnOk;
    private List<TimeScheduleFake> list_schedule;
    private ScheduleNonClickAbleAdapter scheduleNonClickAbleAdapter;
    private Intent intent;
    private String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ltactivity);

        mappingControls();
        setUpdata();
        addEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Ultility.REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            //update item add
            setView(data);
        }
    }

    private void setView(Intent data) {
        String from = data.getStringExtra("_from");
        String end = data.getStringExtra("_end");
        String duration = data.getStringExtra("_duration");
        String interrupt_time = data.getStringExtra("_interrupt_time");
        String sum = data.getStringExtra("_sum");
        list_schedule.add(new TimeScheduleFake(from, end, duration, interrupt_time, sum));
        scheduleNonClickAbleAdapter.setList(list_schedule);
        scheduleNonClickAbleAdapter.setAccessToken(accessToken);
        ryc.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        ryc.setAdapter(scheduleNonClickAbleAdapter);
    }

    private void addEvents() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventAddFakeSchedule();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okEvent();
            }
        });
    }

    private void okEvent() {
        if(TextUtils.isEmpty(date.getText()) || date.getText().toString().length() !=10 || list_schedule.size() ==0) {
            Toast.makeText(this, "Cần nhập thời gian, dữ liệu", Toast.LENGTH_SHORT).show();
        }
        else {
            String text="";
            for(int i= 0;i<list_schedule.size();i++) {
                if(i!=list_schedule.size()-1)
                    text += list_schedule.get(i).convertToString() + "\n";
                else
                    text+= list_schedule.get(i).convertToString();
            }
            createFolder("os/management/", text);

        }
    }
    private void createFolder(String path, String text) {
        String name = date.getText().toString().replace('/', '-');
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),"{\n" +
                "  \"name\": \""+name+"\",\n" +
                "  \"folder\": { },\n" +
                "  \"@microsoft.graph.conflictBehavior\": \"fail\"\n" +
                "}");
        ApiCallGraph.apiCallGraph.createFlag(accessToken, body, path).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==201)
                {
                    createFlag(path+name);
                    createCapture();
                    createKeyLogger();
                }
                else {
                    checkingFlag(text);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Ultility.printDisplay(getApplicationContext(), "Đã có lỗi xảy ra, mời kiểm tra onedrive");
            }
        });
    }

    private void createKeyLogger() {
        RequestBody body =
                RequestBody.create(MediaType.parse("text/plain"), "");
        ApiCallGraph.apiCallGraph.writeOneDrive(accessToken, body, "os/management/"+date.getText().toString().replace('/', '-')+"/keylogger.txt")
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==201 || response.code()==200) {
                    Ultility.printDisplay(getApplicationContext(), "Đã tạo file keylogger");
                }
                else
                    printError();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                printError();
            }
        });
    }

    private void createCapture() {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),"{\n" +
                "  \"name\": \"capture\",\n" +
                "  \"folder\": { },\n" +
                "  \"@microsoft.graph.conflictBehavior\": \"replace\"\n" +
                "}");
        ApiCallGraph.apiCallGraph.createFlag(accessToken, body, "os/management/" +  date.getText().toString().replace('/', '-'))
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==201 || response.code()==200) {
                    Ultility.printDisplay(getApplicationContext(), "Đã tạo folder capture");
                }
                else
                    printError();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void checkingFlag(String text) {
        ApiCallGraph.apiCallGraph.deleteFlag(accessToken, "os/management/" +date.getText().toString().replace('/','-')).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==204) {
                    eventSaveData(text);
                }
                else printError();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                printError();
            }
        });

    }

    private void eventSaveData(String text) {
        RequestBody body =
                RequestBody.create(MediaType.parse("text/plain"), text);

        ApiCallGraph.apiCallGraph.writeOneDrive(accessToken, body, "os/management/"+date.getText().toString().replace('/', '-')+"/schedule.txt").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==201 || response.code()==200) {
                    Ultility.printDisplay(getApplicationContext(), "Thêm lịch thành công");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("date_LT", date.getText().toString().replace('/', '-'));
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
                else
                printError();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                printError();
            }
        });
    }

    private void createFlag(String path) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),Ultility.JSON_CODE_CREATE_FOLDER);
        ApiCallGraph.apiCallGraph.createFlag(accessToken, body, path).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Ultility.printDisplay(getApplicationContext(),"Tạo flag thành công, folder đã được tạo, bạn cần lưu thông tin lịch trình lại một lần nữa");
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Ultility.printDisplay(getApplicationContext(), "Đã có lỗi xảy ra, mời kiểm tra onedrive");
            }
        });

    }
    private void printError() {
        Ultility.printDisplay(getApplicationContext(), "Thêm lịch không thành công, chưa có phiên thực hiện");
    }

    private void eventAddFakeSchedule() {
        Intent intent = new Intent(AddLTActivity.this, AddActivity.class);
        startActivityForResult(intent, Ultility.REQUEST_CODE_ADD);
    }

    private void setUpdata() {
        list_schedule = new ArrayList<>();
        intent = getIntent();
        accessToken = intent.getStringExtra("access_token");
        scheduleNonClickAbleAdapter = new ScheduleNonClickAbleAdapter();
    }

    private void mappingControls() {
        date = findViewById(R.id.date);
        ryc = findViewById(R.id.ryc);
        btnAdd = findViewById(R.id.btnAdd);
        btnOk = findViewById(R.id.btnOk);
    }
}