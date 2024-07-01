package com.example.pe_haquapp.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DeleteDate {
    private int year;
    private int month;
    private int dayOfMonth;

    public DeleteDate(){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DeleteDate(LocalDate date){
        this.year = date.getYear();
        this.month = date.getMonthValue();
        this.dayOfMonth = date.getDayOfMonth();
    }

    public DeleteDate(int year, int month, int dayOfMonth){
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean _isOlderThan90(){
        int year = 0;
        int month = 0;
        int dayOfMonth = 0;
        if (LocalDate.now().getMonthValue()>0 && LocalDate.now().getMonthValue()<4)
        {
            year = LocalDate.now().getYear()-1;
            month = 12-(3-LocalDate.now().getMonthValue());
        }
        else {
            year = LocalDate.now().getYear();
            month = LocalDate.now().getMonthValue();
        }
        if (LocalDate.now().getDayOfMonth()>=29 && LocalDate.now().getDayOfMonth() <= 31){
            dayOfMonth = 28;
        }
        else {
            dayOfMonth = LocalDate.now().getDayOfMonth();
        }
        return getYear() > year ||
                (getYear() == year && getMonth() > month) ||
                (getYear() == year && getMonth() == month && getDayOfMonth() > getDayOfMonth());

    }
}
