package com.os.appparent.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.os.appparent.R;
import com.os.appparent.api.ApiCallGraph;
import com.os.appparent.model.FolderDrive;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CaptureActivity extends AppCompatActivity {
    private ImageView img;
    private ImageButton btnLeft;
    private TextView txtTime;
    private ImageButton btnRight;
    private Intent intent;
    private String accessToken;
    private String date;
    private FolderDrive folderDrive;
    private int curr_index = 0;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        getData();
        mappingControls();
        addEvents();
    }

    private void getData() {
        intent = getIntent();
        accessToken = intent.getStringExtra("access_token");
        date = intent.getStringExtra("name").replace('/','-');
        mProgressDialog = new ProgressDialog(this);
    }

    private void addEvents() {
        mProgressDialog.show();

        //Lấy link từ folder capture onedrive và chúng sẽ chạy nhờ thư viện Glide
        ApiCallGraph.apiCallGraph.getFolder(accessToken, "os/management/" + date+ "/capture").enqueue(new Callback<FolderDrive>() {
            @Override
            public void onResponse(Call<FolderDrive> call, Response<FolderDrive> response) {
                if(response.code()==200) {
                    folderDrive = response.body();
                    if(folderDrive.getCount() == 0)
                        hideButton();
                    else {
                        Glide.with(getApplicationContext()).load(folderDrive.getValue().get(0).getDownUrl()).into(img);
                    }
                }
                else
                    hideButton();
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<FolderDrive> call, Throwable t) {
                hideButton();
                mProgressDialog.cancel();
            }
        });

        action();
    }

    private void action() {

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curr_index>0) {
                    curr_index--;
                    setImage(curr_index);
                }
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curr_index<folderDrive.getCount()-1) {
                    curr_index++;
                    setImage(curr_index);
                }
            }
        });
    }

    private void setImage(int pos) {
        mProgressDialog.show();
        Glide.with(getApplicationContext()).load(folderDrive.getValue().get(pos).getDownUrl()).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                mProgressDialog.cancel();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mProgressDialog.cancel();
                return false;
            }
        }).into(img);
        txtTime.setText("Thời gian: " + folderDrive.getValue().get(pos).getName().substring(0,folderDrive.getValue().get(pos).getName().indexOf('.')));
    }
    private void hideButton() {
        btnLeft.setVisibility(View.INVISIBLE);
        txtTime.setVisibility(View.INVISIBLE);
        btnRight.setVisibility(View.INVISIBLE);
    }

    private void mappingControls() {
        img = findViewById(R.id.img);
        btnLeft = findViewById(R.id.btnLeft);
        txtTime = findViewById(R.id.txtTime);
        btnRight = findViewById(R.id.btnRight);
    }
}