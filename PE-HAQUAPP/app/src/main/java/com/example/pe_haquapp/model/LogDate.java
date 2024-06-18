package com.example.pe_haquapp.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

public class LogDate implements Comparable<LogDate> {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    public LogDate(){

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LogDate(LocalDateTime dateTime){
        this.year = dateTime.getYear();
        this.month = dateTime.getMonth().getValue();
        this.day = dateTime.getDayOfMonth();
        this.hour = dateTime.getHour();
        this.minute = dateTime.getMinute();
        this.second = dateTime.getSecond();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    @Override
    public int compareTo(LogDate o) {
        if (this.year > o.year){
            return 1;
        }
        else if (this.year < o.year){
            return -1;
        }
        else {
            if (this.month > o.month){
                return 1;
            }
            else if (this.month < o.month){
                return -1;
            }
            else {
                if (this.day > o.day){
                    return 1;
                } else if (this.day < o.day) {
                    return -1;
                }
                else {
                    if (this.hour > o.hour){
                        return 1;
                    } else if (this.hour < o.hour) {
                        return -1;
                    }
                    else {
                        if (this.minute > o.minute){
                            return 1;
                        } else if (this.minute < o.minute) {
                            return -1;
                        }
                        else {
                            return Integer.compare(this.second, o.second);
                        }
                    }
                }
            }
        }
    }
}
