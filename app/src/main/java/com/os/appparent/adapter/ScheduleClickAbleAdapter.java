package com.os.appparent.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.os.appparent.Activity.EditScheduleActivity;
import com.os.appparent.R;
import com.os.appparent.model.TimeSchedule;
import com.os.appparent.ultility.Ultility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ScheduleClickAbleAdapter extends RecyclerView.Adapter<ScheduleClickAbleAdapter.ViewHolder> {
    private List<TimeSchedule> list;
    private String accessToken;
    private int current_tab = 0;
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearRyc() {
        if(list!=null)
        list.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<TimeSchedule> list, int current_tab) {
        this.list = list;
        this.current_tab = current_tab;
        notifyDataSetChanged();
    }

    @NonNull
    @Override

    public ScheduleClickAbleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleClickAbleAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bindView(list.get(position));
        holder.item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutClickEvents(holder.context, position);
            }
        });
    }

    private void layoutClickEvents(Context context, int position) {
        TimeSchedule token = list.get(position);
        Intent intent = new Intent(context, EditScheduleActivity.class);
        intent.putExtra("date", token.getDate());
        intent.putExtra("from", token.getFrom());
        intent.putExtra("end", token.getEnd());
        intent.putExtra("duration", token.getDuration());
        intent.putExtra("interrupt_time", token.getInterrupt_time());
        intent.putExtra("sum", token.getSum());
        intent.putExtra("pos", position);
        intent.putExtra("access_token", accessToken);
        intent.putExtra("current_tab", current_tab);
        ((Activity) context).startActivityForResult(intent, Ultility.REQUEST_CODE_EDIT + current_tab);
    }

    @Override
    public int getItemCount() {
        if(list!=null)
        return list.size();
        else
            return 0;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout item_layout;
        public TextView time_first_line;
        public TextView time_hour;
        public TextView time_duration;
        public TextView time_interrupt_time;
        public TextView time_sum;
        public Context context;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            item_layout = itemView.findViewById(R.id.item_layout);
            time_first_line = itemView.findViewById(R.id.time_first_line);
            time_hour = itemView.findViewById(R.id.time_hour);
            time_duration = itemView.findViewById(R.id.time_duration);
            time_interrupt_time = itemView.findViewById(R.id.time_interrupt_time);
            time_sum = itemView.findViewById(R.id.time_sum);
        }

        @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
        public void bindView(TimeSchedule token) {
            if(current_tab == Ultility.STATE_LT) {
                Date ref = null;
                try {
                    ref = new SimpleDateFormat("dd/MM/yyyy").parse(token.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String day = "";
                if(ref.getDay()  == 0)
                    day = "Chủ nhật";
                else
                    day = "Thứ: "+ String.valueOf(ref.getDay() +1);

                time_first_line.setText(day + " - Ngày: " + token.getDate());
            }
            else
                time_first_line.setText("Thời khóa biểu");


            time_hour.setText("Thời gian: " + token.getFrom() +" - " + token.getEnd());
            int dur = Integer.parseInt(token.getDuration());

            if(dur>0)
                time_duration.setText("Thời gian tối đa mỗi lần bật: " + dur + " phút");
            else time_duration.setText("Thời gian tối đa mỗi lần bật: Không");

            int inter = Integer.parseInt(token.getInterrupt_time());
            if(inter>0)
                time_interrupt_time.setText("Thời gian ngắt: " + inter + " phút");
            else time_interrupt_time.setText("Thời gian ngắt: Không");

            int sumToken = Integer.parseInt(token.getSum());
            if(sumToken>0)
                time_sum.setText("Tổng thời gian sử dụng: " + sumToken + "phút");
            else
                time_sum.setText("Tổng thời gian sử dụng: Không");
        }

    }
}

