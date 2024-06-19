package com.example.pe_haquapp.controller.Tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.pe_haquapp.controller.ShowWorkActivity;
import com.example.pe_haquapp.model.JobAddress;
import com.example.pe_haquapp.model.JobDate;
import com.example.pe_haquapp.model.Works;
import com.example.pe_haquapp.model.WorksLogs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateTask extends AsyncTask<Activity, Void, Void> {

    private final FirebaseFirestore worksDB;
    private CollectionReference logsItems;
    private final CollectionReference worksItems;
    private final FirebaseUser user;

    private final Works activeItem;
    private final JobDate modifiedDate;

    private final String workname;
    private final String city;
    private final String street;
    private final int houseNumber;

    public UpdateTask(FirebaseFirestore worksDB, CollectionReference logsItems, CollectionReference worksItems,
                      FirebaseUser user, Works activeItem, JobDate modifiedDate, String workname, String city,
                      String street, int houseNumber){
        this.worksDB = worksDB;
        this.logsItems = logsItems;
        this.worksItems = worksItems;
        this.user = user;
        this.activeItem = activeItem;
        this.modifiedDate = modifiedDate;
        this.workname = workname;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
    }
    @Override
    public Void doInBackground(Activity... activities) {
        logsItems = worksDB.collection("logs");
        if (!workname.equals(activeItem.getName())){
            worksItems.document(activeItem._getId()).update("name",workname).addOnCompleteListener(new OnCompleteListener<Void>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        logsItems.add(new WorksLogs(activeItem._getId(), "name",workname, user.getEmail()));
                    }
                }
            });
        }
        if (!activeItem.getJobAddress().getCity().equals(city) ||
                !activeItem.getJobAddress().getAddressRoad().equals(street) ||
                activeItem.getJobAddress().getHouseNum() != houseNumber){
            worksItems.document(activeItem._getId()).update("jobAddress",new JobAddress(activities[0], city,
                            street,
                            houseNumber))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (!activeItem.getJobAddress().getCity().equals(city)){
                                    logsItems.add(new WorksLogs(activeItem._getId(), "jobAddress.city",city, user.getEmail()));
                                }
                                if (!activeItem.getJobAddress().getAddressRoad().equals(street)){
                                    logsItems.add(new WorksLogs(activeItem._getId(), "jobAddress.street", street, user.getEmail()));
                                }
                                if (activeItem.getJobAddress().getHouseNum() != houseNumber){
                                    logsItems.add(new WorksLogs(activeItem._getId(), "jobAddress.houseNum", String.valueOf(houseNumber), user.getEmail()));
                                }
                            }
                        }
                    });
        }
        if (activeItem.getJobDate().getStartYear() != modifiedDate.getStartYear() ||
                activeItem.getJobDate().getStartMonth() != modifiedDate.getStartMonth() ||
                activeItem.getJobDate().getStartDayOfMonth() != modifiedDate.getStartDayOfMonth() ||
                activeItem.getJobDate().getStartHour() != modifiedDate.getStartHour() ||
                activeItem.getJobDate().getStartMinute() != modifiedDate.getStartMinute() ||
                activeItem.getJobDate().getEndYear() != modifiedDate.getEndYear() ||
                activeItem.getJobDate().getEndMonth() != modifiedDate.getEndMonth() ||
                activeItem.getJobDate().getEndDayOfMonth() != modifiedDate.getEndDayOfMonth() ||
                activeItem.getJobDate().getEndHour() != modifiedDate.getEndHour() ||
                activeItem.getJobDate().getEndMinute() != modifiedDate.getEndMinute() ||
                activeItem.getJobDate().isFinished() != modifiedDate.isFinished()){
            worksItems.document(activeItem._getId()).update("jobDate", modifiedDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if (activeItem.getJobDate().getStartYear() != modifiedDate.getStartYear()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.startYear", String.valueOf(modifiedDate.getStartYear()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getStartMonth() != modifiedDate.getStartMonth()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.startMonth", String.valueOf(modifiedDate.getStartMonth()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getStartDayOfMonth() != modifiedDate.getStartDayOfMonth()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.startDay", String.valueOf(modifiedDate.getStartDayOfMonth()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getStartHour() != modifiedDate.getStartHour()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.startHour", String.valueOf(modifiedDate.getStartHour()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getStartMinute() != modifiedDate.getStartMinute()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.startMinute", String.valueOf(modifiedDate.getStartMinute()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getEndYear() != modifiedDate.getEndYear()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.endYear", String.valueOf(modifiedDate.getEndYear()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getEndMonth() != modifiedDate.getEndMonth()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.endMonth", String.valueOf(modifiedDate.getEndMonth()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getEndDayOfMonth() != modifiedDate.getEndDayOfMonth()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.endDay", String.valueOf(modifiedDate.getEndDayOfMonth()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getEndHour() != modifiedDate.getEndHour()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.endHour", String.valueOf(modifiedDate.getEndHour()), user.getEmail()));
                        }
                        if (activeItem.getJobDate().getEndMinute() != modifiedDate.getEndMinute()){
                            logsItems.add(new WorksLogs(activeItem._getId(),"jobDate.endMinute", String.valueOf(modifiedDate.getEndMinute()), user.getEmail()));
                        }
                    }
                }
            });
        }
        UnLockingTask.getInstance(worksItems).doInBackground(activeItem._getId());
        return null;
    }
}
