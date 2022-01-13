package com.os.appparent.model;

public class TimeSchedule {
    private int isRunning = 0;
    private String date;
    private String from;
    private String end;
    private String duration;
    private String interrupt_time;
    private String sum;


    public TimeSchedule(String date, String from, String end, String duration, String interrupt_time, String sum) {
        this.date = date;
        this.from = from;
        this.end = end;
        this.duration = String.valueOf(Integer.valueOf(duration));
        this.interrupt_time = String.valueOf(Integer.valueOf(interrupt_time));
        this.sum = String.valueOf(Integer.valueOf(sum));
    }
    public String convertToString() {
        return "F" + from + " T"+ end + " D" + duration + " I" + interrupt_time + " S" + sum;
    }
    public TimeSchedule(String info) {
        String[] data = info.split(" ");
        this.from = data[0].substring(1);
        this.end = data[1].substring(1);
        this.date = "";
        this.duration = data[2].substring(1);
        this.interrupt_time = data[3].substring(1);
        this.sum = data[4].substring(1);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(int isRunning) {
        this.isRunning = isRunning;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInterrupt_time() {
        return interrupt_time;
    }

    public void setInterrupt_time(String interrupt_time) {
        this.interrupt_time = interrupt_time;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
