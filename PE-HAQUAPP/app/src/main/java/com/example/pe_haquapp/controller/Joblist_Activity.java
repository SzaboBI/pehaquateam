package com.example.pe_haquapp.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.pe_haquapp.controller.Tasks.ArchiveTask;
import com.example.pe_haquapp.controller.Tasks.DeleteTask;
import com.example.pe_haquapp.controller.Utils.NetworkUtils;
import com.example.pe_haquapp.model.DeleteDate;
import com.example.pe_haquapp.model.JobAddress;
import com.example.pe_haquapp.model.Works;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Joblist_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, WorkClickListener {
    private final String LOG_TAG = Joblist_Activity.class.getName();
    private final int SECRET_KEY = 1;

    private FirebaseUser user;
    private RecyclerView recyclerView;
    private WorksAdapter worksAdapter;
    private FirebaseFirestore worksDB;
    private CollectionReference worksItems;

    private ArrayList<Works> allWorks;
    private ArrayList<Works> works;
    private FirebaseStorage storage;

    private DrawerLayout drawerLayout;
    private TextView TVError;
    private SearchView SVAddress;
    private ImageButton IBDateFilter;
    private ImageButton IBSettings;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_joblist);

            SVAddress = findViewById(R.id.addressSearch);
            IBDateFilter = findViewById(R.id.filterButton);
            IBDateFilter.setImageResource(R.drawable.filter);
            IBDateFilter.setTag("Filter");

            IBSettings = findViewById(R.id.settingButton);

            TVError = findViewById(R.id.error);
            TVError.setVisibility(View.GONE);

            storage= FirebaseStorage.getInstance();
            worksDB = FirebaseFirestore.getInstance();

            recyclerView = findViewById(R.id.workRecyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

            allWorks = new ArrayList<>();
            works = new ArrayList<>();

            worksAdapter = new WorksAdapter(this, works, "myActivity",this);
            recyclerView.setAdapter(worksAdapter);

            Toolbar toolbar = findViewById(R.id.mainToolBar);
            setSupportActionBar(toolbar);

            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setBackgroundResource(R.color.background);
            navigationView.setNavigationItemSelectedListener(this);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            getWorksFromFirestore();

            SVAddress.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SVAddress.isIconified()){
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) SVAddress.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.setMarginStart(25);
                        IBSettings.setVisibility(View.INVISIBLE);
                        IBDateFilter.setVisibility(View.INVISIBLE);
                    }
                }
            });
            SVAddress.setOnCloseListener(new SearchView.OnCloseListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public boolean onClose() {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) SVAddress.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.setMarginEnd(10);
                    IBSettings.setVisibility(View.VISIBLE);
                    IBDateFilter.setVisibility(View.VISIBLE);
                    TVError.setVisibility(View.GONE);
                    works.clear();
                    works.addAll(allWorks);
                    worksAdapter.notifyDataSetChanged();
                    return false;
                }
            });
            SVAddress.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query.trim().isEmpty()){
                        works.addAll(allWorks);
                        TVError.setVisibility(View.GONE);
                    }
                    else {
                        works.clear();
                        String cleared = query.replaceAll(" +", " ")
                                .replaceAll(", ", ",").replaceAll("[|.?!:&*#()/_\\[\\]{}+<>]", "");
                        Log.i(LOG_TAG, cleared);
                        Pattern fullAddressPattern = Pattern.compile("^\\d{4} [A-Za-záéúüűóöőÁÉÚÜŰÓÖŐ]{2,},? *[A-Za-záéúüűóöőÁÉÚÜŰÓÖŐ \\-]+ (sgt|sugárút|utca|u|krt|körút|fasor|dülő|sor|út|rakpart|köz) \\d+");
                        Pattern cityRoadAddressPattern = Pattern.compile("^[A-Za-záéúüűóöőÁÉÚÜŰÓÖŐ]{2,},? *[A-Za-záéúüűóöőÁÉÚÜŰÓÖŐ \\-]+ (sgt|sugárút|utca|u|krt|körút|fasor|dülő|sor|út|rakpart|köz) \\d+");

                        Matcher fullAddressMatcher = fullAddressPattern.matcher(cleared.trim());
                        Matcher cityRoadAddressMatcher = cityRoadAddressPattern.matcher(cleared.trim());

                        String matchedPart = null;
                        JobAddress.constraintType type = null;
                        if (fullAddressMatcher.find()){
                            matchedPart = fullAddressMatcher.group(0);
                            type = JobAddress.constraintType.FULL_ADDRESS;
                        }
                        else if (cityRoadAddressMatcher.find()){
                            matchedPart = cityRoadAddressMatcher.group(0);
                            type = JobAddress.constraintType.CITY_ADDRESS;
                        }
                        if (matchedPart != null && type != null){
                            Log.i(LOG_TAG, String.valueOf(type));
                            for (int i = 0; i < allWorks.size(); i++){
                                if (allWorks.get(i).getJobAddress().isStringApproipate(matchedPart, type)){
                                    works.add(allWorks.get(i));
                                }
                            }
                        }
                    }
                    if (works.isEmpty()){
                        TVError.setText("Nincs találat!");
                        TVError.setVisibility(View.VISIBLE);
                    }
                    else {
                        TVError.setVisibility(View.GONE);
                    }
                    worksAdapter.notifyDataSetChanged();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

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

    @Override
    protected void onPause() {
        super.onPause();
        if (NetworkUtils.isConnected((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))){
            new ArchiveTask().execute();
            new DeleteTask().execute();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getWorksFromFirestore(){
        worksItems = worksDB.collection("works");
        worksItems.orderBy("name").get().addOnSuccessListener(queryDocumentSnapshot -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshot) {
                Works works1 = document.toObject(Works.class);
                works1.setDocumentID(document.getId());
                allWorks.add(works1);
                works.add(works1);
                worksAdapter.notifyDataSetChanged();
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                getTodayWorks();
            }
        });

    }

    private void initializeData(){
        worksItems.add(new Works("test1",2000, (byte) 1, (byte) 1, (byte) 10, (byte) 0,new JobAddress((Activity) this, "Szeged","Kossuth Lajos sgt.", 99),user.getEmail()));

        worksItems.add(new Works("test2",2000, (byte) 1, (byte) 1, (byte) 10, (byte) 0,new JobAddress((Activity) this, "Szeged","Kossuth Lajos sgt.", 99),user.getEmail()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        TVError.setVisibility(View.GONE);
        if (menuItem.getItemId() == R.id.nav_home){
            getTodayWorks();
        } else if (menuItem.getItemId() == R.id.nav_befejezett) {
            getFinishedWorks();
        } else if (menuItem.getItemId() == R.id.nav_unfinished) {
            getUnfinishedWorks();
        }
        else if (menuItem.getItemId() == R.id.nav_future){
            getFutureWorks();
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

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getTodayWorks(){
        works.clear();
        for (int i = 0; i < allWorks.size(); i++){
            if (allWorks.get(i).getJobDate().equals(LocalDate.now())){
                works.add(allWorks.get(i));
            }
        }
        worksAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getFinishedWorks(){
        works.clear();
        for (int i = 0; i< allWorks.size(); i++){
            if (allWorks.get(i).getJobDate().isFinished()){
                works.add(allWorks.get(i));
            }
        }
        worksAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getUnfinishedWorks(){
        works.clear();
        for (int i = 0; i < allWorks.size(); i++){
            if (!allWorks.get(i).getJobDate().isFinished()){
                works.add(allWorks.get(i));
            }
        }
        worksAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getFutureWorks(){
        works.clear();
        for (int i = 0; i< allWorks.size(); i++){
            if (allWorks.get(i).getJobDate()._isFuture()){
                works.add(allWorks.get(i));
            }
        }
        worksAdapter.notifyDataSetChanged();
    }

    public void workSelect(View view) {
        Log.i(LOG_TAG,"Work selected");
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onWorkClicked(String id) {
        TVError.setVisibility(View.GONE);
        if (NetworkUtils.isConnected((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))){
            Context context = this;
            worksItems.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Works works1 = task.getResult().toObject(Works.class);
                    if (works1 != null && (!works1.isLocked() ||
                            (works1.isLocked() && works1.getLockingUser()!=null &&
                                    works1.getLockingUser().equals(user.getEmail())))){
                        works1.setDocumentID(task.getResult().getId());
                        LockingTask.getInstance(worksItems,user).doInBackground(works1._getId());
                        Intent intent = new Intent(context, ShowWorkActivity.class);
                        Log.i(LOG_TAG,id);
                        intent.putExtra("documentID",id);
                        intent.putExtra("key",SECRET_KEY);
                        intent.putExtra("previousActivity","Joblist_activity");
                        startActivity(intent);
                    }
                    else {
                        TVError.setText("Munka zárolva! Próbáld újra később!");
                        TVError.setVisibility(View.VISIBLE);
                        Log.i(LOG_TAG, String.valueOf(TVError.getVisibility()));
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

    public void openCreateActivity(View view) {
        Intent intent = new Intent(this, CreateWorkActivity.class);
        intent.putExtra("key", SECRET_KEY);
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void filterByDate(View view) {
        if (IBDateFilter.getTag() == "Filter"){
            Log.i(LOG_TAG, "Filter");
            DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @SuppressLint("NotifyDataSetChanged")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    works.clear();
                    for (int i = 0; i < allWorks.size(); i++){
                        if (allWorks.get(i).getJobDate().equals(LocalDate.of(year,month,dayOfMonth))){
                            works.add(allWorks.get(i));
                        }
                    }
                    worksAdapter.notifyDataSetChanged();
                    IBDateFilter.setImageResource(R.drawable.baseline_close_24);
                    IBDateFilter.setTag("Erase");
                }
            }, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth());
            datePicker.show();
        }
        else {
            Log.i(LOG_TAG, "Erase");
            works.clear();
            works.addAll(allWorks);
            worksAdapter.notifyDataSetChanged();
            IBDateFilter.setImageResource(R.drawable.filter);
            IBDateFilter.setTag("Filter");
        }
    }

    /**
     * This class lock the work (document), it won't be allowed to modify, while the lock is on the document!
     */
    static class LockingTask extends AsyncTask<String, Void, Void>{

        private static LockingTask instance;
        private static CollectionReference reference;
        private static FirebaseUser user;
        private LockingTask(CollectionReference collectionReference, FirebaseUser user){
            reference = collectionReference;
            LockingTask.user = user;
        }

        public static LockingTask getInstance(CollectionReference collectionReference, FirebaseUser user) {
            if (instance == null){
                instance = new LockingTask(collectionReference, user);
            }
            else {
                setReference(collectionReference);
                setUser(user);
            }
            return instance;
        }

        public static void setReference(CollectionReference reference) {
            LockingTask.reference = reference;
        }

        public static void setUser(FirebaseUser user) {
            LockingTask.user = user;
        }

        @Override
        protected Void doInBackground(String... id) {
            reference.document(id[0]).update("locked",true);
            reference.document(id[0]).update("lockingUser", user.getEmail());
            return null;
        }
    }
}