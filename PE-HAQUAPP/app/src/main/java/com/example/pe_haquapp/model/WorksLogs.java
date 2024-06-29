package com.example.pe_haquapp.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class WorksLogs {
    private final String user;
    private final String loggedDocumentID;
    private final LogDate logDate;
    public enum operation{
        CREATE,
        UPDATE,
        DELETE,
        UPLOAD
    }
    private final operation op;

    private final String logDetails;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public WorksLogs(String documentID, String modifiedField, String value, String user){
        this.logDate = new LogDate(LocalDateTime.now());
        this.loggedDocumentID = documentID;
        this.op = operation.UPDATE;
        this.user = user;
        this.logDetails = modifiedField+":"+value;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public WorksLogs(operation op, String documentID, String user){
        this.logDate = new LogDate(LocalDateTime.now());
        this.loggedDocumentID = documentID;
        this.op = op;
        this.user = user;
        this.logDetails = "";
    }

    public String getUser() {
        return user;
    }

    public LogDate getLogDate() {
        return logDate;
    }

    public operation getOp() {
        return op;
    }

    public String getLoggedDocumentID() {
        return loggedDocumentID;
    }

    public String getLogDetails() {
        return logDetails;
    }
}
