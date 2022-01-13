package com.os.appparent.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.os.appparent.R;
import com.os.appparent.model.TimeScheduleFake;

import java.util.List;

public class ScheduleNonClickAbleAdapter extends RecyclerView.Adapter<ScheduleNonClickAbleAdapter.ViewHolder> {
    private List<TimeScheduleFake> list;
    private String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    private int state = 0;
    public void setList(List<TimeScheduleFake> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override

    public ScheduleNonClickAbleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleNonClickAbleAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
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

        @SuppressLint("SetTextI18n")
        public void onBind(TimeScheduleFake token) {
            time_first_line.setText("Thời khóa biểu");

            time_hour.setText("Thời gian: " + token.getFrom() +" - " + token.getEnd());
            int dur = Integer.parseInt(token.getDuration());

            if(dur>0)
                time_duration.setText("Thời gian tối đa mỗi lần bật: " + String.valueOf(dur) + " phút");
            else
                time_duration.setText("Thời gian tối đa mỗi lần bật: Không");

            int inter = Integer.valueOf(token.getInterrupt_time());
            if(inter>0)
                time_interrupt_time.setText("Thời gian ngắt: " + String.valueOf(inter) + " phút");
            else time_interrupt_time.setText("Thời gian ngắt:: Không");

            int sumToken = Integer.parseInt(token.getSum());
            if(sumToken>0)
                time_sum.setText("Tổng thời gian sử dụng: " + String.valueOf(sumToken) + "phút");
            else
                time_sum.setText("Tổng thời gian sử dụng: Không");
        }
    }
}

