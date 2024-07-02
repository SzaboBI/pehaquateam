package com.example.pe_haquapp.controller;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pe_haquapp.R;
import com.example.pe_haquapp.controller.Tasks.UnLockingTask;
import com.example.pe_haquapp.controller.Tasks.UpdateTask;
import com.example.pe_haquapp.controller.Utils.DateUtils;
import com.example.pe_haquapp.controller.Utils.NetworkUtils;
import com.example.pe_haquapp.model.JobAddress;
import com.example.pe_haquapp.model.JobDate;
import com.example.pe_haquapp.model.Works;
import com.example.pe_haquapp.model.WorksLogs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;

public class ShowWorkActivity extends AppCompatActivity {

    private final String LOG_TAG = ShowWorkActivity.class.getName();
    private final int SECRET_KEY = 1;

    private FirebaseUser user;
    private FirebaseFirestore worksDB;
    private CollectionReference worksItems;
    private CollectionReference logsItems;
    private CollectionReference deletedWorksItems;

    private EditText ETWorkName;
    private EditText ETCity;
    private EditText ETStreet;
    private EditText ETHouseNumber;
    private TextView TVStartDate;
    private TextView TVEndDate;
    private CheckBox CBCompleted;
    private TextView TVError;

    private JobDate current;

    private Works activeWork;

    @SuppressLint({"SetTextI18n", "DefaultLocale", "WrongThread"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if ((getIntent().getIntExtra("key", 1) == SECRET_KEY) && (user != null)){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_show_work);

            worksDB = FirebaseFirestore.getInstance();

            ETWorkName = findViewById(R.id.workName);
            ETCity = findViewById(R.id.city);
            ETStreet = findViewById(R.id.street);
            ETHouseNumber = findViewById(R.id.houseNumber);
            TVStartDate = findViewById(R.id.startDateTextView);
            TVEndDate = findViewById(R.id.endDateTextView);
            CBCompleted = findViewById(R.id.completeCheckBox);
            TVError = findViewById(R.id.error);

            Log.i(LOG_TAG, getIntent().getStringExtra("documentID"));
            TVError.setText("");

            getWorkFromDBByDocumentID(getIntent().getStringExtra("documentID"));

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        else {
            Intent intent = new Intent(this, Joblist_Activity.class);
            startActivity(intent);
        }
    }

    private void getWorkFromDBByDocumentID(String id){
        Context context = this;
        worksItems = worksDB.collection("works");
        logsItems = worksDB.collection("logs");
        deletedWorksItems = worksDB.collection("deletedWorks");

        worksItems.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                activeWork = task.getResult().toObject(Works.class);
                activeWork.setDocumentID(task.getResult().getId());
                if (CheckLocked.getInstance(worksItems,activeWork,user).doInBackground()){
                    Intent intent = new Intent(context,Joblist_Activity.class);
                    startActivity(intent);
                    finish();
                }
                ETWorkName.setText(activeWork.getName());
                ETCity.setText(activeWork.getJobAddress().getCity());
                ETStreet.setText(activeWork.getJobAddress().getAddressRoad());
                ETHouseNumber.setText(String.format("%s",activeWork.getJobAddress().getHouseNum()));
                current = new JobDate(activeWork.getJobDate());
                LocalDateTime localStart = LocalDateTime.of(activeWork.getJobDate().getStartYear(),
                                                            activeWork.getJobDate().getStartMonth(),
                                                            activeWork.getJobDate().getStartDayOfMonth(),
                                                            activeWork.getJobDate().getStartHour(),
                                                            activeWork.getJobDate().getStartMinute());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                TVStartDate.setText(String.format("%s: %s", "Kezdés",localStart.format(formatter)));
                if (activeWork.getJobDate().isFinished()){
                    LocalDateTime localEnd = LocalDateTime.of(activeWork.getJobDate().getEndYear(),
                            activeWork.getJobDate().getEndMonth(),
                            activeWork.getJobDate().getEndDayOfMonth(),
                            activeWork.getJobDate().getEndHour(),
                            activeWork.getJobDate().getEndMinute());
                    TVEndDate.setText(String.format("%s: %s", "Vége", localEnd.format(formatter)));
                }
                CBCompleted.setChecked(activeWork.getJobDate().isFinished());
                CBCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Log.i(LOG_TAG, "Pipalas");
                        if (isChecked && current.getEndYear() == -1){
                            current.finish();
                            //Log.i(LOG_TAG, "End year: "+String.valueOf(current.getEndYear()));
                            LocalDateTime local = LocalDateTime.of(current.getEndYear(),
                                                                    current.getEndMonth(),
                                                                    current.getEndDayOfMonth(),
                                                                    current.getEndHour(),
                                                                    current.getEndMinute());
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                            TVEndDate.setText(String.format("%s: %s", "Vége" , local.format(formatter)));
                        }
                        else if (!isChecked){
                            current.unfinish();
                            TVEndDate.setText(String.format("%s:", "Vége"));
                        }
                        Log.i(LOG_TAG, "Original: "+activeWork.getJobDate().toString());
                        Log.i(LOG_TAG, "Current: "+current.toString());
                    }
                });
            }
        });
    }

    public void back(View view) {
        Intent intent = new Intent(this, Joblist_Activity.class);
        String previous = getIntent().getStringExtra("previousActivity");
        UnLockingTask.getInstance(worksItems).doInBackground(activeWork._getId());
        startActivity(intent);
        finish();
    }

    public void save(View view) {

        Log.i(LOG_TAG, "ActiveWork: "+activeWork._getId());
        int errorCount = 0;
        if (ETWorkName.getText() == null || ETWorkName.getText().toString().trim().isEmpty()){
            TVError.setText("Adjon meg egy nevet a munkának!");
            errorCount++;
        }
        if (errorCount == 0 && (ETCity.getText() == null || ETCity.getText().toString().trim().isEmpty())){
            TVError.setText("Adja meg munkavégzés városát!");
            errorCount++;
        }
        if (errorCount == 0 && (ETStreet.getText() == null || ETStreet.getText().toString().trim().isEmpty())) {
            TVError.setText("Adja meg a munkavégzés közterületének nevét!");
            errorCount++;
        }
        if (errorCount == 0 && !current.checkCorrect()){
            errorCount++;
        }
        if (errorCount == 0 && !NetworkUtils.isConnected((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))){
            TVError.setText("Nincs internet!");
            errorCount++;
        }
        try {
            if (errorCount == 0 && ETHouseNumber.getText().toString().trim().isEmpty()) {
                TVError.setText("Adjon meg egy házszámot!");
                errorCount++;
            }
            if (errorCount == 0 && Integer.parseInt(ETHouseNumber.getText().toString())<=0){
                TVError.setText("Nullánál nagyobb számot adjon meg!");
                errorCount++;
            }
        }
        catch (NumberFormatException e){
            TVError.setText("Adjon meg egy helyes házszámot!");
            errorCount++;
        }
        if (errorCount == 0){
            new UpdateTask(activeWork,
                            new Works(ETWorkName.getText().toString().trim(),
                                        current, new JobAddress(this, ETCity.getText().toString().trim(),
                                                                ETStreet.getText().toString().trim(),
                                                                Integer.parseInt(ETHouseNumber.getText().toString().trim())),user.getEmail()),
                                                                user.getEmail()).doInBackground(this);
            Intent openMain = new Intent(this, Joblist_Activity.class);
            startActivity(openMain);
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openStartDatePicker(View view) {
        //Log.i(LOG_TAG, "DateTimePicker open");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                current.setStartHour(hourOfDay);
                current.setStartMinute(minute);
                TVStartDate.setText(String.format("%s: %s", "Kezdés: " ,
                        LocalDateTime.of(current.getStartYear(), current.getStartMonth(), current.getStartDayOfMonth(), current.getStartHour(), current.getStartMinute()).format(formatter)));
                if (!current.checkCorrect()){
                    TVError.setText("Helytelen dátum!");
                }
                else {
                    TVError.setText("");
                }
            }
        }, activeWork.getJobDate().getStartHour(), activeWork.getJobDate().getStartMinute(), true);
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                current.setStartYear(year);
                current.setStartMonth(month);
                current.setStartDayOfMonth(dayOfMonth);
                timePicker.show();
            }
        }, activeWork.getJobDate().getStartYear(), activeWork.getJobDate().getStartMonth(), activeWork.getJobDate().getStartDayOfMonth());
        datePicker.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openEndDatePicker(View view) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (activeWork.getJobDate().getEndYear() != -1){
            TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    current.setEndHour(hourOfDay);
                    current.setEndHour(minute);

                    TVEndDate.setText(String.format("%s: %s", "Kezdés " ,
                            LocalDateTime.of(current.getEndYear(), current.getEndMonth(), current.getEndDayOfMonth(), current.getEndHour(), current.getEndMinute()).format(formatter)));
                    CBCompleted.setChecked(true);
                    if (!current.checkCorrect()){
                        TVError.setText("Helytelen dátum!");
                    }
                    else {
                        TVError.setText("");
                    }
                }
            }, activeWork.getJobDate().getEndHour(), activeWork.getJobDate().getEndMinute(), true);
            DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    current.finish();
                    current.setEndYear(year);
                    current.setEndMonth(month);
                    current.setEndDayOfMonth(dayOfMonth);
                    timePicker.show();
                }
            }, activeWork.getJobDate().getEndYear(), activeWork.getJobDate().getEndMonth(), activeWork.getJobDate().getEndDayOfMonth());
            datePicker.show();
        }
        else {
            TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    current.setEndHour(hourOfDay);
                    current.setEndMinute(minute);
                    TVEndDate.setText(String.format("%s: %s", "Kezdés " ,
                            LocalDateTime.of(current.getEndYear(), current.getEndMonth(), current.getEndDayOfMonth(), current.getEndHour(), current.getEndMinute()).format(formatter)));
                    CBCompleted.setChecked(true);
                    if (!current.checkCorrect()){
                        TVError.setText("Helytelen dátum!");
                    }
                    else {
                        TVError.setText("");
                    }
                }
            }, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), true);
            DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    current.finish();
                    current.setEndYear(year);
                    current.setEndMonth(month);
                    current.setEndDayOfMonth(dayOfMonth);
                    timePicker.show();
                }
            }, LocalDate.now().minusMonths(1).getYear(), LocalDate.now().minusMonths(1).getMonthValue(), LocalDate.now().minusMonths(1).getDayOfMonth());
            datePicker.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void delete(View view) {
        Intent intent = new Intent(this, ShowWorkActivity.class);
        activeWork._setDelete(LocalDate.now());
        deletedWorksItems.add(activeWork).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                worksItems.document(activeWork._getId()).delete().addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    logsItems.add(new WorksLogs(WorksLogs.operation.DELETE, activeWork._getId(),user.getEmail()));
                                    startActivity(intent);
                                }
                            }
                        }
                );
            }
        });

    }

    static class CheckLocked extends AsyncTask<Void, Void, Boolean>{

        private static CheckLocked instance;
        private static CollectionReference collectionReference;
        private static Works activeItem;
        private static FirebaseUser firebaseUser;
        private CheckLocked(CollectionReference reference, Works activeItem, FirebaseUser user){
            CheckLocked.collectionReference= reference;
            CheckLocked.activeItem = activeItem;
            CheckLocked.firebaseUser = user;
        }

        public static CheckLocked getInstance(CollectionReference reference, Works activeItem, FirebaseUser user) {
            if (instance==null){
                instance = new CheckLocked(reference, activeItem, user);
            }
            else {
                setCollectionReference(reference);
                setActiveItem(activeItem);
                setFirebaseUser(user);
            }
            return instance;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            final boolean[] res = {false};
            collectionReference.document(activeItem._getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        Works works1 = task.getResult().toObject(Works.class);
                        if (works1 != null){
                            res[0] = works1.isLocked() && !works1.getLockingUser().equals(firebaseUser.getEmail());
                        }
                    }
                }
            });
            return res[0];
        }

        public static void setCollectionReference(CollectionReference collectionReference) {
            CheckLocked.collectionReference = collectionReference;
        }

        public static void setActiveItem(Works activeItem) {
            CheckLocked.activeItem = activeItem;
        }

        public static void setFirebaseUser(FirebaseUser firebaseUser) {
            CheckLocked.firebaseUser = firebaseUser;
        }
    }
}