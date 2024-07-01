package com.example.pe_haquapp.controller.Tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.pe_haquapp.model.Works;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;

public class DeleteTask extends AsyncTask<Void, Void, Void> {

    FirebaseFirestore workDB;
    CollectionReference deletedWorks;

    @Override
    protected Void doInBackground(Void... voids) {
        workDB = FirebaseFirestore.getInstance();
        deletedWorks = workDB.collection("deletedWorks");
        deletedWorks.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Works works1 = document.toObject(Works.class);
                    works1.setDocumentID(document.getId());
                    if (works1.getDelete()._isOlderThan90()) {
                        deletedWorks.document(document.getId()).delete();
                    }
                }
            }
        });
        return null;
    }
}
