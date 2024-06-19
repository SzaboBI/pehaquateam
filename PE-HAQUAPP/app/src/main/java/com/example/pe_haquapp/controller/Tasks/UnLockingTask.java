package com.example.pe_haquapp.controller.Tasks;

import android.os.AsyncTask;

import com.google.firebase.firestore.CollectionReference;

public class UnLockingTask extends AsyncTask<String, Void, Void> {

    private static UnLockingTask instance;
    private static CollectionReference reference;
    private UnLockingTask(CollectionReference collectionReference){
        reference = collectionReference;
    }

    public static UnLockingTask getInstance(CollectionReference reference) {
        if (instance == null){
            instance = new UnLockingTask(reference);
        }
        else {
            setReference(reference);
        }
        return instance;
    }

    @Override
    public Void doInBackground(String... id) {
        reference.document(id[0]).update("locked",false);
        reference.document(id[0]).update("lockingUser", null);
        return null;
    }

    public static void setReference(CollectionReference reference) {
        UnLockingTask.reference = reference;
    }
}
