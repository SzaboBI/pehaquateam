package com.example.pe_haquapp.controller.Tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.pe_haquapp.controller.Adapters.WorksAdapter;
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

public class ArchiveTask extends AsyncTask<Void, Void, Void> {

    private FirebaseFirestore workDB;
    private CollectionReference worksItems;
    private CollectionReference archiveWorksItems;

    public ArchiveTask(){
        this.workDB = FirebaseFirestore.getInstance();
        this.worksItems = workDB.collection("works");
        this.archiveWorksItems = workDB.collection("archiveWorks");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Void doInBackground(Void... voids) {
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
            month = LocalDate.now().getMonthValue()-3;
        }
        if (LocalDate.now().getDayOfMonth()>=29 && LocalDate.now().getDayOfMonth() <= 31){
            dayOfMonth = 28;
        }
        else {
            dayOfMonth = LocalDate.now().getDayOfMonth();
        }
        int finalYear = year;
        int finalMonth = month;
        int finalDayOfMonth = dayOfMonth;
        worksItems.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                    Works works1 = document.toObject(Works.class);
                    works1.setDocumentID(document.getId());
                    if (!works1.getJobDate()._isGreaterStart(LocalDate.of(finalYear, finalMonth, finalDayOfMonth))){
                        archiveWorksItems.add(works1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                worksItems.document(works1._getId()).delete();
                            }
                        });
                    }
                }
            }
        });
        return null;
    }
}
