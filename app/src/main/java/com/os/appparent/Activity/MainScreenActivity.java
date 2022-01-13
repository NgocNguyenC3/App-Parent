package com.os.appparent.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.os.appparent.R;
import com.os.appparent.adapter.ScheduleClickAbleAdapter;
import com.os.appparent.api.ApiCallGraph;
import com.os.appparent.model.DriveItem;
import com.os.appparent.model.FolderDrive;
import com.os.appparent.model.TimeSchedule;
import com.os.appparent.ultility.Ultility;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainScreenActivity extends AppCompatActivity {
    private LinearLayout background_layout;
    private TextView txtShowOnRunning;
    private Button tabLT;
    private Button tabSchedule;
    private RecyclerView ryc_view;
    private Button btnFilter;
    private ImageButton btnAdd;

    private Intent intent;
    private String accessToken;

    private List<TimeSchedule> list_data;
    private List<String> list_name_folder;
    private ScheduleClickAbleAdapter scheduleClickAbleAdapter;
    private String state_url;
    private int current_state;
    private int curr_filter = 0;
    private int pos;
    private String schedule_id;
    private final String[] fillList = {"0", "00", "000", "0000"};
    private final String[] filterTime = {"Quá khứ", "Hôm nay", "Tương lai"};
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        mappingControls();
        setUpData();
        addEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Ultility.REQUEST_CODE_ADD && resultCode == Activity.RESULT_OK) {
            eventAddScheduleDefault(data);
        }
        if(requestCode == Ultility.REQUEST_CODE_EDIT + Ultility.STATE_LT && resultCode == Activity.RESULT_OK) {
            eventEditDateSchedule(data);
        }
        if(requestCode == Ultility.REQUEST_CODE_EDIT + Ultility.STATE_SCHEDULE && resultCode == Activity.RESULT_OK) {
            eventEditSchedule(data);
        }
        if(requestCode == Ultility.REQUEST_CODE_ADD_LT && resultCode == Activity.RESULT_OK) {
            tabLTEvents();
            createFlag("os/management/" +data.getStringExtra("date_LT"));
        }
    }

    private void eventEditDateSchedule(Intent data) {
        // Edit calendar for any day
        EditDateScheduleAsyncTask editDateScheduleAsyncTask = new EditDateScheduleAsyncTask();
        editDateScheduleAsyncTask.setData(data);
        editDateScheduleAsyncTask.execute();
    }

    private void eventEditSchedule(Intent data) {
        //Edit default calendar
        EditDefaultScheduleAsyncTask editDefaultScheduleAsyncTask = new EditDefaultScheduleAsyncTask();
        editDefaultScheduleAsyncTask.setData(data);
        editDefaultScheduleAsyncTask.execute();
    }

    private void eventAddScheduleDefault(Intent data) {
        //add new schedule
        WriteScheduleDefautAsyncTask writeScheduleDefautAsyncTask = new WriteScheduleDefautAsyncTask();
        writeScheduleDefautAsyncTask.setData(data);
        writeScheduleDefautAsyncTask.execute();
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

    private void addEvents() {
        //Check state Child PC, (shutdown/running)
        //first event
        tabLTEvents();

        tabLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLTEvents();
            }
        });
        tabSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabScheduleEvents();
            }
        });
        // Event "Lọc"
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                eventFilter();
            }
        });
        //Event "Thêm"
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_state == Ultility.STATE_LT)
                    addLT();
                else
                    addSchedule();
            }
        });
        //Checking Child Pc per X second
        checkState();
    }


    private void checkState() {
        ApiCallGraph.apiCallGraph.getDriveItem(accessToken,"os/state.txt").enqueue(new Callback<DriveItem>() {
            @Override
            public void onResponse(Call<DriveItem> call, Response<DriveItem> response) {
                DriveItem driveItem = response.body();
                state_url = driveItem.getDownUrl();
                new StateAsyncTask().execute(state_url);
            }

            @Override
            public void onFailure(Call<DriveItem> call, Throwable t) {

            }
        });
    }

    private void addSchedule() {
        Intent intent = new Intent(MainScreenActivity.this, AddActivity.class);
        intent.putExtra("access_token", accessToken);
        intent.putExtra("count", list_data.size());
        intent.putExtra("state", Ultility.STATE_SCHEDULE);
        startActivityForResult(intent, Ultility.REQUEST_CODE_ADD);
    }

    private void addLT() {
        Intent intent = new Intent(MainScreenActivity.this, AddLTActivity.class);
        intent.putExtra("access_token", accessToken);
        startActivityForResult(intent, Ultility.REQUEST_CODE_ADD_LT);
    }

    // past, now, future
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void eventFilter() {
        displayDialog();
        List<TimeSchedule> timeSchedules = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/u");
        LocalDate currentDate = LocalDate.now();
        curr_filter = (-3*curr_filter*curr_filter+ 5*curr_filter + 2)/2;

        if(curr_filter==0) {
            for(int i =0;i<list_data.size();i++) {
                LocalDate validDate = LocalDate.parse(list_data.get(i).getDate(), dateFormatter);
                if(validDate.isBefore(currentDate)) {
                    timeSchedules.add(list_data.get(i));
                }
            }
        }
        else if(curr_filter==1) {
            for(int i =0;i<list_data.size();i++) {
                LocalDate validDate = LocalDate.parse(list_data.get(i).getDate(), dateFormatter);
                if(validDate.isEqual(currentDate)) {
                    timeSchedules.add(list_data.get(i));
                }
            }
        }
        else
        if(curr_filter==2) {
            for(int i =0;i<list_data.size();i++) {
                LocalDate validDate = LocalDate.parse(list_data.get(i).getDate(), dateFormatter);
                if(currentDate.isBefore(validDate)) {
                    timeSchedules.add(list_data.get(i));
                }
            }
        }
        scheduleClickAbleAdapter.setList(timeSchedules, Ultility.STATE_LT);
        scheduleClickAbleAdapter.setAccessToken(accessToken);
        ryc_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ryc_view.setAdapter(scheduleClickAbleAdapter);
        btnFilter.setText(filterTime[curr_filter]);
        cancelDialog();

    }

    private void tabScheduleEvents() {
        btnFilter.setVisibility(View.INVISIBLE);
        list_data.clear();
        scheduleClickAbleAdapter.clearRyc();
        current_state = Ultility.STATE_SCHEDULE;
        //setColor
        tabSchedule.setTextColor(Color.RED);
        tabLT.setTextColor(Color.BLACK);
        
        getDataScheduleEvents();
    }

    private void getDataScheduleEvents() {
        displayDialog();
        ApiCallGraph.apiCallGraph.getDriveItem(accessToken, "os/default-schedule.txt").enqueue(new Callback<DriveItem>() {
            @Override
            public void onResponse(Call<DriveItem> call, Response<DriveItem> response) {
                if(response.code()==200) {
                    DriveItem driveItem = response.body();
                    schedule_id = driveItem.getId();
                    new GetScheduleAsyncTask().execute(driveItem.getDownUrl());
                } else
                cancelDialog();
            }

            @Override
            public void onFailure(Call<DriveItem> call, Throwable t) {
                cancelDialog();
            }
        });
    }

    private void tabLTEvents() {
        btnFilter.setVisibility(View.VISIBLE);
        btnFilter.setText("Lọc");
        list_data.clear();
        scheduleClickAbleAdapter.clearRyc();
        current_state = Ultility.STATE_LT;
        //setColor
        tabLT.setTextColor(Color.RED);
        tabSchedule.setTextColor(Color.BLACK);

        getDataLTEvents();
    }

    private void getDataLTEvents() {
        displayDialog();
        ApiCallGraph.apiCallGraph.getFolder(accessToken, "os/management").enqueue(new Callback<FolderDrive>() {
            @Override
            public void onResponse(Call<FolderDrive> call, Response<FolderDrive> response) {
                if(response.code()==200) {
                    FolderDrive folderDrive = response.body();
                    if (folderDrive.getCount() > 0) {
                        FilterManagementFolderAsyncTask filterManagementFolderAsyncTask = new FilterManagementFolderAsyncTask();
                        filterManagementFolderAsyncTask.setList(folderDrive.getValue());
                        filterManagementFolderAsyncTask.execute();
                    }

                }else
                cancelDialog();
            }

            @Override
            public void onFailure(Call<FolderDrive> call, Throwable t) {
                cancelDialog();
            }
        });
    }

    private void setViewRyc(int currentState) {
        if(list_data !=null) {
            if(list_data.size()!=0) {
                scheduleClickAbleAdapter.setList(list_data, currentState);
                scheduleClickAbleAdapter.setAccessToken(accessToken);
            }
            else
                scheduleClickAbleAdapter.clearRyc();
        }
        ryc_view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        ryc_view.setAdapter(scheduleClickAbleAdapter);
    }

    private void cancelDialog() {
        mProgressDialog.cancel();
    }
    private void displayDialog() { mProgressDialog.show(); }

    private void setUpData() {
        intent = getIntent();
        accessToken = intent.getStringExtra("token");
        mProgressDialog = new ProgressDialog(this);
        list_data = new ArrayList<>();
        list_name_folder = new ArrayList<>();
        scheduleClickAbleAdapter = new ScheduleClickAbleAdapter();
    }

    private void mappingControls() {
        background_layout = findViewById(R.id.background_layout);
        txtShowOnRunning = findViewById(R.id.txtShowOnRunning);
        tabLT = findViewById(R.id.tabLT);
        tabSchedule = findViewById(R.id.tabSchedule);
        ryc_view = findViewById(R.id.ryc_view);
        btnFilter = findViewById(R.id.btnFilter);
        btnAdd = findViewById(R.id.btnAdd);
    }

    private void printFalse() {
        Ultility.printDisplay(getApplicationContext(), "Thêm lịch không thành công");
    }
    private class FilterManagementFolderAsyncTask extends AsyncTask<Void, Void, Void> {
        private List<DriveItem> list;
        public void setList(List<DriveItem> list) {
            this.list = list;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            list_name_folder.clear();
            for(int i=0;i<list.size();i++) {
                String path = list.get(i).getName();
                list_name_folder.add(path);

                ApiCallGraph.apiCallGraph.getDriveItem(accessToken, "os/management/"+ path+"/schedule.txt").enqueue(new Callback<DriveItem>() {
                    @Override
                    public void onResponse(Call<DriveItem> call, Response<DriveItem> response) {
                        if (response.code() == 200) {
                            ShowLTAsyncTask showLTAsyncTask = new ShowLTAsyncTask();
                            showLTAsyncTask.setDate(path);
                            showLTAsyncTask.execute(response.body().getDownUrl());
                        }
                    }

                    @Override
                    public void onFailure(Call<DriveItem> call, Throwable t) {

                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            cancelDialog();
        }
    }

    private class ShowLTAsyncTask extends AsyncTask<String, String, String> {
        private String date = "";
        public void setDate(String date) {
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!mProgressDialog.isShowing())
            displayDialog();
        }

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
                    TimeSchedule timeSchedule = new TimeSchedule(data);
                    timeSchedule.setDate(date.replace('-', '/'));
                    list_data.add(timeSchedule);
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
            setViewRyc(Ultility.STATE_LT);
            if(mProgressDialog.isShowing())
                cancelDialog();
        }
    }

    private class GetScheduleAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... mUrl) {
            list_data.clear();
            try {
                String data ="";
                URL url = new URL(mUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                while((data = bufferedReader.readLine()) !=null) {
                    list_data.add(new TimeSchedule(data));
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
            setViewRyc(Ultility.STATE_SCHEDULE);
            cancelDialog();
        }
    }

    private class WriteScheduleDefautAsyncTask extends AsyncTask<Void, Void, String> {
        private Intent data ;
        public void setData(Intent data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String date = data.getStringExtra("_date");
            String from = data.getStringExtra("_from");
            String end = data.getStringExtra("_end");
            String duration = data.getStringExtra("_duration");
            String interrupt_time = data.getStringExtra("_interrupt_time");
            String sum = data.getStringExtra("_sum");

            if(duration.length() < 4) {
                duration = fillList[3-duration.length()] + duration;
            }
            if(interrupt_time.length() < 4) {
                interrupt_time = fillList[3-interrupt_time.length()] + interrupt_time;
            }
            if(sum.length() < 4) {
                sum = fillList[3-sum.length()] + sum;
            }
            list_data.add(new TimeSchedule("",from,end,duration,interrupt_time,sum));
            String text = "";
            for(int i= 0;i<list_data.size();i++) {
                if(i!=list_data.size()-1)
                    text += list_data.get(i).convertToString() + "\n";
                else
                    text+= list_data.get(i).convertToString();
            }

            return text;
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
            RequestBody body =
                    RequestBody.create(MediaType.parse("text/plain"), text);
            ApiCallGraph.apiCallGraph.writeOneDrive(accessToken, body, "os/default-schedule.txt").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==200) {
                        Ultility.printDisplay(getApplicationContext(), "Thêm lịch thành công");
                        tabScheduleEvents();
                    }
                    else
                        printFalse();
                    createFlag("os");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    printFalse();
                    createFlag("os");
                }
            });

        }
    }
    private class EditDefaultScheduleAsyncTask extends AsyncTask<Void, Void, String> {
        private Intent data ;
        private String date;
        public void setData(Intent data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... voids) {
            date = data.getStringExtra("_date");
            String from = data.getStringExtra("_from");
            String end = data.getStringExtra("_end");
            String duration = data.getStringExtra("_duration");
            String interrupt_time = data.getStringExtra("_interrupt_time");
            String sum = data.getStringExtra("_sum");
            pos = data.getIntExtra("position", -1);
            list_data.get(pos).setDate(date);
            list_data.get(pos).setFrom(from);
            list_data.get(pos).setEnd(end);
            if(duration.length() < 4) {
                duration = fillList[3-duration.length()] + duration;
            }
            if(interrupt_time.length() < 4) {
                interrupt_time = fillList[3-interrupt_time.length()] + interrupt_time;
            }
            if(sum.length() < 4) {
                sum = fillList[3-sum.length()] + sum;
            }
            list_data.get(pos).setDuration(duration);
            list_data.get(pos).setInterrupt_time(interrupt_time);
            list_data.get(pos).setSum(sum);
            String text = "";
            for(int i= 0;i<list_data.size();i++) {
                if(i!=list_data.size()-1)
                    text += list_data.get(i).convertToString() + "\n";
                else
                    text+= list_data.get(i).convertToString();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
            RequestBody body =
                    RequestBody.create(MediaType.parse("text/plain"), text);
            ApiCallGraph.apiCallGraph.writeOneDrive(accessToken, body, "os/default-schedule.txt").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==200) {
                        Ultility.printDisplay(getApplicationContext(), "Thêm lịch thành công");
                        tabScheduleEvents();
                    }
                    else
                        printFalse();
                    createFlag("os");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    printFalse();
                    createFlag("os");
                }
            });

        }
    }

    private class EditDateScheduleAsyncTask extends AsyncTask<Void, Void, String> {
        private Intent data ;
        private String date;

        public void setData(Intent data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... voids) {
            date = data.getStringExtra("_date");
            String from = data.getStringExtra("_from");
            String end = data.getStringExtra("_end");
            String duration = data.getStringExtra("_duration");
            String interrupt_time = data.getStringExtra("_interrupt_time");
            String sum = data.getStringExtra("_sum");
            pos = data.getIntExtra("position", -1);
            list_data.get(pos).setDate(date.replace('-', '/'));
            list_data.get(pos).setFrom(from);
            list_data.get(pos).setEnd(end);
            if(duration.length() < 4) {
                duration = fillList[3-duration.length()] + duration;
            }
            if(interrupt_time.length() < 4) {
                interrupt_time = fillList[3-interrupt_time.length()] + interrupt_time;
            }
            if(sum.length() < 4) {
                sum = fillList[3-sum.length()] + sum;
            }
            list_data.get(pos).setDuration(duration);
            list_data.get(pos).setInterrupt_time(interrupt_time);
            list_data.get(pos).setSum(sum);
            String text = "";
            for(int i= 0;i<list_data.size();i++) {
                if(list_data.get(i).getDate().equals(date.replace('-', '/'))) {
                    if (i != list_data.size() - 1)
                        text += list_data.get(i).convertToString() + "\n";
                    else
                        text += list_data.get(i).convertToString();
                }
            }
            return text;
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), text);
            ApiCallGraph.apiCallGraph.writeOneDrive(accessToken, body, "os/management/"+date+"/schedule.txt").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==200) {
                        Ultility.printDisplay(getApplicationContext(), "Thêm lịch thành công");
                        tabLTEvents();
                    }else
                        printFalse();
                    createFlag("os/management/" + date);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    printFalse();
                    createFlag("os/management/" + date);
                }
            });
        }
    }
// Check your child PC is Running?
    private class StateAsyncTask extends AsyncTask<String, String, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... mUrl) {
            try {

                URL url = new URL(mUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                byte data[] = new byte[1];
                input.read(data);
                input.close();
                return new String(data, StandardCharsets.UTF_8);
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
            s = s.substring(0,1);
            if(s.equals("0"))
                setState(0);
            else if(s.equals("1"))
                setState(1);
        }
    }
    @SuppressLint("SetTextI18n")
    private void setState(int state) {
        if(state == 1) {
            txtShowOnRunning.setText("Máy của bạn đang hoạt động");
            txtShowOnRunning.setTextColor(Color.RED);
            background_layout.setBackgroundColor(Color.GREEN);
        }
        else {
            txtShowOnRunning.setText("Máy của bạn đang không hoạt động");
            txtShowOnRunning.setTextColor(Color.GREEN);
            background_layout.setBackgroundColor(Color.RED);
        }
    }
}