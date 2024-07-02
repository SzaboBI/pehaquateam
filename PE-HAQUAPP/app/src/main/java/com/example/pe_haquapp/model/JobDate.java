package com.example.pe_haquapp.model;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JobDate(LocalDateTime start){
        this.startYear = start.getYear();
        this.startMonth = start.getMonthValue();
        this.startDayOfMonth = start.getDayOfMonth();
        this.startHour = start.getHour();
        this.startMinute = start.getMinute();
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
        if (endYear != -1){
            this.endYear = endYear;
            this.endMonth = endMonth;
            this.endDayOfMonth = endDayOfMonth;
            this.endHour = endHour;
            this.endMinute = endMinute;
            this.finished = true;
        }
        else {
            this.finished = false;
        }
    }
    public JobDate(JobDate jobDate){
        this.startYear = jobDate.getStartYear();
        this.startMonth = jobDate.getStartMonth();
        this.startDayOfMonth = jobDate.getStartDayOfMonth();
        this.startHour = jobDate.getStartHour();
        this.startMinute = jobDate.getStartMinute();
        this.endYear = jobDate.getEndYear();
        this.endMonth = jobDate.getEndMonth();
        this.endDayOfMonth = jobDate.getEndDayOfMonth();
        this.endHour = jobDate.getEndHour();
        this.endMinute = jobDate.getEndMinute();
        this.finished = jobDate.isFinished();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public String toString() {
        String output;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        if (endYear == -1 || endMonth == -1 || endDayOfMonth == -1 || endHour == -1 || endMinute == -1){
           output = LocalDateTime.of(startYear, startMonth, startDayOfMonth, startHour, startMinute).format(formatter);
        }
        else {
            output = LocalDateTime.of(startYear,startMonth, startDayOfMonth, startHour, startMinute).format(formatter)+" - "+
                    LocalDateTime.of(endYear,endMonth, endDayOfMonth, endHour, endMinute).format(formatter);
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
        this.finished = true;
    }

    public void unfinish(){
        this.endYear = -1;
        this.endMonth = -1;
        this.endDayOfMonth = -1;
        this.endHour = -1;
        this.endMinute = -1;
        this.finished = false;
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

    public boolean checkCorrect(){
        if (endYear != -1){
            return (startYear<endYear ||
                    (startYear == endYear && startMonth<endMonth) ||
                    (startYear == endYear && startMonth == endMonth && startDayOfMonth<endDayOfMonth) ||
                    (startYear == endYear && startMonth == endMonth && startDayOfMonth == endDayOfMonth && startHour<endHour) ||
                    (startYear == endYear && startMonth == endMonth && startDayOfMonth == endDayOfMonth && startHour == endHour && startMinute<endMinute));
        }
        else {
            return true;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean equals(LocalDate localDate){
        if (isFinished()){
            return ((getStartYear() == localDate.getYear() &&
                    getStartMonth() == localDate.getMonthValue() &&
                    getStartDayOfMonth() == localDate.getDayOfMonth()) ||
                    (getEndYear() == localDate.getYear() &&
                    getEndMonth() == localDate.getMonthValue() &&
                    getEndDayOfMonth() == localDate.getDayOfMonth()));
        }
        else {
            return getStartYear() == localDate.getYear() &&
                    getStartMonth() == localDate.getMonthValue() &&
                    getStartDayOfMonth() == localDate.getDayOfMonth();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean _isFuture(){
        return !isFinished() &&
                (getStartYear() > LocalDate.now().getYear() ||
                (getStartYear() == LocalDate.now().getYear() && getStartMonth() > LocalDate.now().getMonthValue()) ||
                (getStartYear() == LocalDate.now().getYear() && getStartMonth() == LocalDate.now().getMonthValue() &&
                        getStartDayOfMonth() > LocalDate.now().getDayOfMonth()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean _isGreaterStart(LocalDate date){
            return getStartYear() > date.getYear() ||
                    (getStartYear() == date.getYear() && getStartMonth() > date.getMonthValue()) ||
                    (getStartYear() == date.getYear() && getStartMonth() == date.getMonthValue() && getStartDayOfMonth() > date.getDayOfMonth());
    }
}
