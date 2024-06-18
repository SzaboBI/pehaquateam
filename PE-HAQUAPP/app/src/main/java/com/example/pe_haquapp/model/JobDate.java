package com.example.pe_haquapp.model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;

public class JobDate {
    private int startYear;
    private int startMonth;
    private int startDayOfMonth;
    private int startHour;
    private int startMinute;
    private int endYear = -1;
    private int endMonth = -1;
    private int endDayOfMonth = -1;
    private int endHour = -1;
    private int endMinute = -1;
    private boolean finished;

    public JobDate(){

    }

    public JobDate(int startYear, int startMonth, int startDayOfMonth, int startHour, int startMinute){
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDayOfMonth = startDayOfMonth;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.finished = false;
    }

    public JobDate(int startYear, int startMonth, int startDayOfMonth, int startHour, int startMinute, int endYear, int endMonth, int endDayOfMonth, int endHour, int endMinute){
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDayOfMonth = startDayOfMonth;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDayOfMonth = endDayOfMonth;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.finished = true;
    }

    @NonNull
    @Override
    public String toString() {
        String output;
        if (endYear == -1 || endMonth == -1 || endDayOfMonth == -1 || endHour == -1 || endMinute == -1){
           output = startYear+"."+startMonth+"."+startDayOfMonth+". "+startMonth+":"+startMinute;
        }
        else {
            output = startYear+"."+startMonth+"."+startDayOfMonth+". "+startMonth+":"+startMinute+"-"+endYear+"."+endMonth+"."+endDayOfMonth+".";
        }
        return  output;
    }

    @SuppressLint("NewApi")
    public void finish(){
        this.endYear = LocalDateTime.now().getYear();
        this.endMonth = LocalDateTime.now().getMonth().getValue();
        this.endDayOfMonth = LocalDateTime.now().getDayOfMonth();
        this.endHour = LocalDateTime.now().getHour();
        this.endMinute = LocalDateTime.now().getMinute();
        this.finished = true;
    }
    public void finish(int endYear, int endMonth, int endDayOfMonth, int endHour, int endMinute){
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDayOfMonth = endDayOfMonth;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public void unfinish(){
        this.endYear = -1;
        this.endMonth = -1;
        this.endDayOfMonth = -1;
        this.endHour = -1;
        this.endMinute = -1;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartDayOfMonth() {
        return startDayOfMonth;
    }

    public void setStartDayOfMonth(int startDayOfMonth) {
        this.startDayOfMonth = startDayOfMonth;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getEndDayOfMonth() {
        return endDayOfMonth;
    }

    public void setEndDayOfMonth(int endDayOfMonth) {
        this.endDayOfMonth = endDayOfMonth;
    }

    public boolean isFinished() {
        return finished;
    }
}
