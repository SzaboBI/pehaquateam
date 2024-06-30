package com.example.pe_haquapp.controller;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pe_haquapp.R;
import com.example.pe_haquapp.controller.Utils.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getName();

    private EditText emailET;
    private EditText passwordET;

    private TextView errorTV;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        errorTV = findViewById(R.id.error);
        auth = FirebaseAuth.getInstance();
        errorTV.setText("");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void login(View view) {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        if (email.isEmpty()){
            errorTV.setText("Add meg az e-mail címet!");
        }else if (password.isEmpty()) {
            errorTV.setText("Add meg a jelszót!");
        }
        else if (!NetworkUtils.isConnected((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))){
            errorTV.setText("Nincs internet!");
        }
        else {
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.find()){
                errorTV.setText("");
                //Log.i(LOG_TAG, "Belepes");
                Intent intent = new Intent(this, Joblist_Activity.class);
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(intent);
                        }
                        else {
                            errorTV.setText("Sikertelen bejelentkezés!");
                        }
                    }
                });
            }
            else {
                errorTV.setText("Adj meg egy megfelelő e-mail címet!");
            }
        }
    }
}