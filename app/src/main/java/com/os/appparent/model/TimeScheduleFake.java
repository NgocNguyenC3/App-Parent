package com.os.appparent.model;

public class TimeScheduleFake {
    private String from;
    private String end;
    private String duration;
    private String interrupt_time;
    private String sum;
    private String[] ex = {"0", "00", "000", "0000"};
    public TimeScheduleFake(String from, String end, String duration, String interrupt_time, String sum) {
        this.from = from;
        this.end = end;
        this.duration = String.valueOf(Integer.valueOf(duration));
        this.interrupt_time = String.valueOf(Integer.valueOf(interrupt_time));
        this.sum = String.valueOf(Integer.valueOf(sum));
    }

    public TimeScheduleFake(String info) {
        String[] data = info.split(" ");
        this.from = data[0].substring(1);
        this.end = data[1].substring(1);
        this.duration = data[2].substring(1);
        this.interrupt_time = data[3].substring(1);
        this.sum = data[4].substring(1);

    }
    public String convertToString() {
        String _duration = duration;
        String _interrupt_time = interrupt_time;
        String _sum = sum;
        if(duration.length() < 4) {
            _duration = ex[3-duration.length()] + duration;
        }
        if(interrupt_time.length() < 4) {
            _interrupt_time = ex[3-interrupt_time.length()] + interrupt_time;
        }
        if(sum.length() < 4) {
            _sum = ex[3-sum.length()] + sum;
        }
        return "F" + from + " T"+ end + " D" + _duration + " I" + _interrupt_time + " S" + _sum;
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
