package com.example.pe_haquapp.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContainer;
import androidx.appcompat.widget.ActionBarOverlayLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pe_haquapp.R;
import com.example.pe_haquapp.controller.Adapters.WorkClickListener;
import com.example.pe_haquapp.controller.Adapters.WorksAdapter;
import com.example.pe_haquapp.model.JobAddress;
import com.example.pe_haquapp.model.Works;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Joblist_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, WorkClickListener {
    private final String LOG_TAG = Joblist_Activity.class.getName();
    private final int SECRET_KEY = 1;

    private FirebaseUser user;
    private RecyclerView recyclerView;
    private WorksAdapter worksAdapter;
    private FirebaseFirestore worksDB;
    private CollectionReference worksItems;
    private ArrayList<Works> works;
    private FirebaseStorage storage;

    private DrawerLayout drawerLayout;
    private TextView TVError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_joblist);

            TVError = findViewById(R.id.error);
            TVError.setVisibility(View.GONE);

            storage= FirebaseStorage.getInstance();
            worksDB = FirebaseFirestore.getInstance();

            recyclerView = findViewById(R.id.workRecyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

            works = new ArrayList<>();

            worksAdapter = new WorksAdapter(this, works, "myActivity",this);
            recyclerView.setAdapter(worksAdapter);

            Toolbar toolbar = findViewById(R.id.mainToolBar);
            setSupportActionBar(toolbar);

            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            getWorksFromFirestore();

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        else {
            finish();
            Intent openLogin = new Intent(this, MainActivity.class);
            startActivity(openLogin);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getWorksFromFirestore(){
        worksItems = worksDB.collection("works");
        worksItems.orderBy("name").get().addOnSuccessListener(queryDocumentSnapshot -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshot) {
                Works works1 = document.toObject(Works.class);
                works1.setDocumentID(document.getId());
                works.add(works1);
                worksAdapter.notifyDataSetChanged();
            }
            if (works.isEmpty()){
                initializeData();
                getWorksFromFirestore();
            }
        });
    }

    private void initializeData(){
        worksItems.add(new Works("test1",2000, (byte) 1, (byte) 1, (byte) 10, (byte) 0,new JobAddress((Activity) this, "Szeged","Kossuth Lajos sgt.", 99),user.getEmail()));

        worksItems.add(new Works("test2",2000, (byte) 1, (byte) 1, (byte) 10, (byte) 0,new JobAddress((Activity) this, "Szeged","Kossuth Lajos sgt.", 99),user.getEmail()));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home){
            Log.i(LOG_TAG, "Home");
        } else if (menuItem.getItemId() == R.id.nav_befejezett) {
            Log.i(LOG_TAG, "Befejezett");
        } else if (menuItem.getItemId() == R.id.nav_all_folyamatban) {
            Log.i(LOG_TAG, "All folyamatban");
        }
        else if (menuItem.getItemId() == R.id.nav_all_finished){
            Log.i(LOG_TAG, "All finished");
        }
        else {
            FirebaseAuth.getInstance().signOut();
            Intent openMainActivity = new Intent(this, MainActivity.class);
            startActivity(openMainActivity);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void workSelect(View view) {
        Log.i(LOG_TAG,"Work selected");
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onWorkClicked(String id) {
        TVError.setVisibility(View.GONE);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = null;
        if (manager != null){
            capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
        }
        if (manager != null && manager.getActiveNetwork() != null &&
                capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
            Context context = this;
            worksItems.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Works works1 = task.getResult().toObject(Works.class);
                    if (works1 != null && (!works1.isLocked() || (works1.isLocked() && works1.getLockingUser().equals(user.getEmail())))){
                        works1.setDocumentID(task.getResult().getId());
                        new LockingTask().doInBackground(works1._getId());
                        Intent intent = new Intent(context, ShowWorkActivity.class);
                        Log.i(LOG_TAG,id);
                        intent.putExtra("documentID",id);
                        intent.putExtra("key",SECRET_KEY);
                        intent.putExtra("previousActivity","Joblist_activity");
                        startActivity(intent);
                    }
                    else {
                        runOnUiThread(()->{
                            TVError.setText("Munka zárolva! Próbáld újra később!");
                            TVError.setVisibility(View.VISIBLE);
                            Log.i(LOG_TAG, String.valueOf(TVError.getVisibility()));
                        });


                    }
                }
            });
        }
        else {
            TVError.setText("Nincs internet!");
            TVError.setVisibility(View.VISIBLE);
            Log.i(LOG_TAG, String.valueOf(TVError.getVisibility()));
        }

    }
    class LockingTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... id) {
            worksItems.document(id[0]).update("locked",true);
            worksItems.document(id[0]).update("lockingUser", user.getEmail());
            return null;
        }
    }

    class ShowErrorTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            Log.i(LOG_TAG, "Eltunt!");
            return null;
        }
    }
}