package com.example.pe_haquapp.model;

import android.os.Build;
import android.provider.ContactsContract;

import androidx.annotation.RequiresApi;

import com.google.type.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kotlinx.coroutines.Job;

public class Works {
    private String documentID;
    private String name;
    private JobDate jobDate;
    private JobAddress jobAddress;
    private String user;
    private boolean locked;
    private String lockingUser;

    public Works(){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Works(String name, int startYear, byte startMonth, byte startDay, byte startHour, byte startMinute, int endYear, byte endMonth, byte endDay, byte endHour, byte endMinute, JobAddress jobAddress, String user){
        this.name = name;
        this.jobDate = new JobDate(startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
        this.jobAddress = jobAddress;
        this.user = user;
        this.locked = false;
        this.lockingUser = null;
    }
    public Works(String name, int startYear, byte startMonth, byte startDay, byte startHour, byte startMinute, JobAddress jobAddress, String user){
        this.name = name;
        this.jobDate = new JobDate(startYear, startMonth, startDay, startHour, startMinute);
        this.jobAddress = jobAddress;
        this.user = user;
        this.locked = false;
        this.lockingUser = null;
    }

    public String _getId(){
        return documentID;
    }
    public String getName() {
        return name;
    }

    public JobDate getJobDate(){
        return  jobDate;
    }

    public JobAddress getJobAddress() {
        return jobAddress;
    }

    public String getUser() {
        return user;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getLockingUser() {
        return lockingUser;
    }

    public void setDocumentID(String documentID){
        this.documentID = documentID;
    }
    public void lock(String lockingUser){
        this.locked = true;
        this.lockingUser = lockingUser;
    }
    public void unLock(){
        this.locked = false;
        this.lockingUser = null;
    }

}
