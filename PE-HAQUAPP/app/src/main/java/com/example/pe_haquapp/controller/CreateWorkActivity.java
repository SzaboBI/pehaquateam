package com.example.pe_haquapp.controller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
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
import com.example.pe_haquapp.controller.Joblist_Activity;
import com.example.pe_haquapp.controller.Tasks.CreateTask;
import com.example.pe_haquapp.controller.Tasks.UnLockingTask;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateWorkActivity extends AppCompatActivity {

    private final String LOG_TAG = CreateWorkActivity.class.getName();
    private FirebaseUser user;

    private JobDate current;

    private EditText ETWorkName;
    private EditText ETCity;
    private EditText ETStreet;
    private EditText ETHouseNumber;
    private TextView TVStartDate;
    private TextView TVEndDate;
    private TextView TVError;
    private CheckBox CBFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null && getIntent().getIntExtra("key",1)==1){
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_create_work);

            ETWorkName = findViewById(R.id.workName);
            ETCity = findViewById(R.id.city);
            ETStreet = findViewById(R.id.street);
            ETHouseNumber = findViewById(R.id.houseNumber);
            TVStartDate = findViewById(R.id.startDateTextView);
            TVEndDate = findViewById(R.id.endDateTextView);
            TVError = findViewById(R.id.error);
            CBFinished = findViewById(R.id.completeCheckBox);


            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            CBFinished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    if (current == null && isChecked){
                        current = new JobDate(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(),
                                            LocalDateTime.now().getHour(), LocalDateTime.now().getMinute());
                        current.finish();
                        current.setEndMinute(current.getEndMinute()+1);
                        TVStartDate.setText(String.format("%s: %s", "Kezdés" ,
                                LocalDateTime.of(current.getStartYear(), current.getStartMonth(), current.getStartDayOfMonth(), current.getStartHour(), current.getStartMinute()).format(formatter)));
                        LocalDateTime local = LocalDateTime.of(current.getEndYear(),
                                current.getEndMonth(),
                                current.getEndDayOfMonth(),
                                current.getEndHour(),
                                current.getEndMinute());
                        TVEndDate.setText(String.format("%s: %s", "Vége", local.format(formatter)));
                    }
                    if (isChecked && current.getEndYear() == -1) {
                        current.finish();
                        LocalDateTime local = LocalDateTime.of(current.getEndYear(),
                                current.getEndMonth(),
                                current.getEndDayOfMonth(),
                                current.getEndHour(),
                                current.getEndMinute());
                        TVEndDate.setText(String.format("%s: %s", "Vége", local.format(formatter)));
                    } else if (!isChecked) {
                        current.unfinish();
                        TVEndDate.setText(String.format("%s:", "Vége"));
                    }
                }
            });
        }
    }


    public void back(View view) {
        Intent intent = new Intent(this, Joblist_Activity.class);
        //String previous = getIntent().getStringExtra("previousActivity");
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openStartDatePicker(View view) {
        if (current == null){
            current = new JobDate();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                current.setStartHour(hourOfDay);
                current.setStartMinute(minute);
                TVStartDate.setText(String.format("%s: %s", "Kezdés" ,
                        LocalDateTime.of(current.getStartYear(), current.getStartMonth(), current.getStartDayOfMonth(), current.getStartHour(), current.getStartMinute()).format(formatter)));
                if (!current.checkCorrect()){
                    TVError.setText("Helytelen dátum!");
                }
                else {
                    TVError.setText("");
                }
            }
        }, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(),true);
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                current.setStartYear(year);
                current.setStartMonth(month);
                current.setStartDayOfMonth(dayOfMonth);
                timePicker.show();
            }
        }, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth());
        datePicker.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openEndDatePicker(View view) {
        if (current == null){
            current = new JobDate();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                current.setEndHour(hourOfDay);
                current.setEndDayOfMonth(minute);
                TVEndDate.setText(String.format("%s: %s", "Vége" ,
                        LocalDateTime.of(current.getStartYear(), current.getStartMonth(), current.getStartDayOfMonth(), current.getStartHour(), current.getStartMinute()).format(formatter)));
                if (!current.checkCorrect()){
                    TVError.setText("Helytelen dátum!");
                }
                else {
                    TVError.setText("");
                }
                CBFinished.setChecked(current.isFinished());
            }
        }, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(),true);
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                current.finish();
                current.setEndYear(year);
                current.setEndMonth(month);
                current.setEndDayOfMonth(dayOfMonth);
                timePicker.show();
            }
        }, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth());
        datePicker.show();
    }

    public void create(View view) {
        int errorCount = 0;
        if (ETWorkName.getText().toString().isEmpty()){
            TVError.setText("Adjon meg egy nevet a munkának!");
            errorCount++;
        }
        if (errorCount == 0 && ETCity.getText().toString().isEmpty()){
            TVError.setText("Adja meg munkavégzés városát!");
            errorCount++;
        }
        if (errorCount == 0 && ETStreet.getText().toString().isEmpty()){
            TVError.setText("Adja meg a munkavégzés közterületének nevét!");
            errorCount++;
        }
        if (errorCount == 0 && current == null){
            TVError.setText("Adja meg a munkavégzés dátumát!");
            errorCount++;
        } else if (errorCount == 0 && !current.checkCorrect()) {
            errorCount++;
        }
        if (!NetworkUtils.isConnected((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))){
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
            Intent intent = new Intent(this, Joblist_Activity.class);
            //Log.i(LOG_TAG, ETCity.getText().toString().trim()+" "+ETStreet.getText().toString().trim()+" "+ETHouseNumber.getText().toString().trim());
            try {
                new CreateTask(new Works(ETWorkName.getText().toString().trim(),
                        current, new JobAddress(this, ETCity.getText().toString().trim(),
                        ETStreet.getText().toString().trim(),
                        Integer.parseInt(ETHouseNumber.getText().toString().trim())),
                        user.getEmail()),user).execute();
            }
            catch (RuntimeException e){
                TVError.setText("A megadott cím nem található!");
                errorCount++;
            }
            if (errorCount == 0){
                startActivity(intent);
                finish();
            }
        }
    }
}