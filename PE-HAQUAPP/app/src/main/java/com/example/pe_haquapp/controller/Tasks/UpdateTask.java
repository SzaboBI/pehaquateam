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
    private final String user;

    private final Works original;
    private final Works modified;

    public UpdateTask(Works original, Works modified, String user){
        this.worksDB = FirebaseFirestore.getInstance();
        this.worksItems = worksDB.collection("works");
        this.logsItems = worksDB.collection("logs");
        this.original = original;
        this.modified = modified;
        this.user = user;
    }
    @Override
    public Void doInBackground(Activity... activities) {
        logsItems = worksDB.collection("logs");
        if (!modified.getName().equals(original.getName())){
            worksItems.document(original._getId()).update("name",modified.getName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        logsItems.add(new WorksLogs(original._getId(), "name",modified.getName(), user));
                    }
                }
            });
        }
        if (!modified.getJobAddress().equals(original.getJobAddress())){
            worksItems.document(original._getId()).update("jobAddress",new JobAddress(activities[0], modified.getJobAddress().getCity(),
                            modified.getJobAddress().getAddressRoad(),
                            modified.getJobAddress().getHouseNum()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (!modified.getJobAddress().getCity().equals(original.getJobAddress().getCity())){
                                    logsItems.add(new WorksLogs(original._getId(), "jobAddress.city",modified.getJobAddress().getCity(), user));
                                }
                                if (!modified.getJobAddress().getAddressRoad().equals(original.getJobAddress().getAddressRoad())){
                                    logsItems.add(new WorksLogs(original._getId(), "jobAddress.street", modified.getJobAddress().getAddressRoad(), user));
                                }
                                if (modified.getJobAddress().getHouseNum() != original.getJobAddress().getHouseNum()){
                                    logsItems.add(new WorksLogs(original._getId(), "jobAddress.houseNum", String.valueOf(modified.getJobAddress().getHouseNum()), user));
                                }
                            }
                        }
                    });
        }
        if (original.getJobDate().getStartYear() != modified.getJobDate().getStartYear() ||
                original.getJobDate().getStartMonth() != modified.getJobDate().getStartMonth() ||
                original.getJobDate().getStartDayOfMonth() != modified.getJobDate().getStartDayOfMonth() ||
                original.getJobDate().getStartHour() != modified.getJobDate().getStartHour() ||
                original.getJobDate().getStartMinute() != modified.getJobDate().getStartMinute() ||
                original.getJobDate().getEndYear() != modified.getJobDate().getEndYear() ||
                original.getJobDate().getEndMonth() != modified.getJobDate().getEndMonth() ||
                original.getJobDate().getEndDayOfMonth() != modified.getJobDate().getEndDayOfMonth() ||
                original.getJobDate().getEndHour() != modified.getJobDate().getEndHour() ||
                original.getJobDate().getEndMinute() != modified.getJobDate().getEndMinute() ||
                original.getJobDate().isFinished() != modified.getJobDate().isFinished()){
            worksItems.document(original._getId()).update("jobDate", new JobDate(modified.getJobDate())).addOnCompleteListener(new OnCompleteListener<Void>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if (original.getJobDate().getStartYear() != modified.getJobDate().getStartYear()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.startYear", String.valueOf(modified.getJobDate().getStartYear()), user));
                        }
                        if (original.getJobDate().getStartMonth() != modified.getJobDate().getStartMonth()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.startMonth", String.valueOf(modified.getJobDate().getStartMonth()), user));
                        }
                        if (original.getJobDate().getStartDayOfMonth() != modified.getJobDate().getStartDayOfMonth()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.startDay", String.valueOf(modified.getJobDate().getStartDayOfMonth()), user));
                        }
                        if (original.getJobDate().getStartHour() != modified.getJobDate().getStartHour()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.startHour", String.valueOf(modified.getJobDate().getStartHour()), user));
                        }
                        if (original.getJobDate().getStartMinute() != modified.getJobDate().getStartMinute()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.startMinute", String.valueOf(modified.getJobDate().getStartMinute()), user));
                        }
                        if (original.getJobDate().getEndYear() != modified.getJobDate().getEndYear()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.endYear", String.valueOf(modified.getJobDate().getEndYear()), user));
                        }
                        if (original.getJobDate().getEndMonth() != modified.getJobDate().getEndMonth()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.endMonth", String.valueOf(modified.getJobDate().getEndMonth()), user));
                        }
                        if (original.getJobDate().getEndDayOfMonth() != modified.getJobDate().getEndDayOfMonth()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.endDay", String.valueOf(modified.getJobDate().getEndDayOfMonth()), user));
                        }
                        if (original.getJobDate().getEndHour() != modified.getJobDate().getEndHour()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.endHour", String.valueOf(modified.getJobDate().getEndHour()), user));
                        }
                        if (original.getJobDate().getEndMinute() != modified.getJobDate().getEndMinute()){
                            logsItems.add(new WorksLogs(original._getId(),"jobDate.endMinute", String.valueOf(modified.getJobDate().getEndMinute()), user));
                        }
                    }
                }
            });
        }
        UnLockingTask.getInstance(worksItems).doInBackground(original._getId());
        return null;
    }
}
