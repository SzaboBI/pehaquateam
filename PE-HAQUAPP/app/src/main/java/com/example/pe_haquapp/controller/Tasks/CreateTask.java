package com.example.pe_haquapp.controller.Tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.pe_haquapp.model.JobAddress;
import com.example.pe_haquapp.model.Works;
import com.example.pe_haquapp.model.WorksLogs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateTask extends AsyncTask<Void, Void, Void> {

    private Works toCreate;

    private FirebaseFirestore workDB;
    private CollectionReference workItems;
    private CollectionReference logItems;
    private FirebaseUser user;

    public CreateTask(Works toCreate, FirebaseUser user){
        workDB = FirebaseFirestore.getInstance();
        workItems = workDB.collection("works");
        logItems = workDB.collection("logs");
        this.user = user;
        this.toCreate = toCreate;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        workItems.add(toCreate).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                logItems.add(new WorksLogs(WorksLogs.operation.CREATE, task.getResult().getId(), user.getEmail()));
            }
        });
        return null;
    }
}
