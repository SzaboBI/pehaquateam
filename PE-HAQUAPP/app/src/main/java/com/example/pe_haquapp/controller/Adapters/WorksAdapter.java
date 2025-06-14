package com.example.pe_haquapp.controller.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pe_haquapp.R;
import com.example.pe_haquapp.model.Works;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorksAdapter extends RecyclerView.Adapter<WorksAdapter.ViewHolder> implements Filterable {
    protected static final String LOG_TAG = WorksAdapter.class.getName();
    private ArrayList<Works> allWorks;
    private ArrayList<Works> filteredWorks;
    private Context mContext;

    private int lastPosition = -1;



    private enum ActivityType{
        MY_ACTIVITY,
        MY_FINISHED_ACTIVITY,
        ALL_ACTIVITY,
        ALL_FINISHED_ACTIVITY
    }

    private ActivityType activityType;
    private WorkClickListener listener;

    public WorksAdapter(Context context, ArrayList<Works> works, String activityType, WorkClickListener listener){
        this.allWorks = works;
        this.filteredWorks = allWorks;
        this.listener = listener;
        mContext = context;
        switch (activityType){
            case "myActivity":
                this.activityType = ActivityType.MY_ACTIVITY;
                break;
            case "myFinishedActivity":
                this.activityType = ActivityType.MY_FINISHED_ACTIVITY;
                break;
            case "allActivity":
                this.activityType = ActivityType.ALL_ACTIVITY;
                break;
            case "allFinishedActivity":
                this.activityType = ActivityType.ALL_FINISHED_ACTIVITY;
                break;
        }
    }

    @NonNull
    @Override
    public WorksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.job_list, parent, false),listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull WorksAdapter.ViewHolder holder, int position) {
        Works currentItem = filteredWorks.get(position);
        holder.bindTo(currentItem);
    }

    @Override
    public int getItemCount() {
        return filteredWorks.size();
    }

    private Filter worksFilterByStreet = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Works> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length()==0){
                results.values = allWorks;
                results.count = allWorks.size();
            }
            else {
                /*String cleared = constraint.toString().trim().replace(",","").replaceAll(" +"," ");
                String[] parts = cleared.split(" ");
                Pattern postcodePattern = Pattern.compile("[0-9]{4}");
                Pattern roadPattern = Pattern.compile("[A-Za-z]+");
                String addressRoad="";
                Matcher postcodeMatcher = postcodePattern.matcher(parts[0]);
                int houseNum = 0;
                if (postcodeMatcher.find()){

                }
                else {
                    int i = 0;
                    while (i<parts.length && roadPattern.matcher(parts[i]).find()){
                        addressRoad = addressRoad+parts[i];
                        i++;
                    }
                    try {
                        houseNum = Integer.parseInt(parts[i]);
                    } catch (NumberFormatException ignored){

                    }
                    if (houseNum!=0 && !addressRoad.isEmpty()){
                        ArrayList<Works> appropiates= new ArrayList<>();
                        for (int j = 0; j < allWorks.size(); j++){
                            if (allWorks.get(i).getJobAddress().getAddressRoad().replace(" ","").equals(addressRoad) &&
                                allWorks.get(i).getJobAddress().getHouseNum() == houseNum){
                                appropiates.add(allWorks.get(i));
                            }
                        }
                        results.values = appropiates;
                        results.count = appropiates.size();
                    }
                }*/
            }

            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    };


    @Override
    public Filter getFilter() {
        return worksFilterByStreet;
    }



    static class ViewHolder extends RecyclerView.ViewHolder{
        private final CardView CVWorkItem;
        private final TextView TVName;
        private final TextView TVDatum;
        private final TextView TVCim;

        public ViewHolder(View itemView, WorkClickListener listener){
            super(itemView);
            CVWorkItem = itemView.findViewById(R.id.workItem);
            TVName = itemView.findViewById(R.id.workName);
            TVDatum = itemView.findViewById(R.id.datumContent);
            TVCim = itemView.findViewById(R.id.workAddress);
            CVWorkItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = (String) CVWorkItem.getTag();
                    listener.onWorkClicked(id);
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindTo(Works currentItem){
            TVName.setText(currentItem.getName());
            CVWorkItem.setTag(currentItem._getId());
            TVDatum.setText(currentItem.getJobDate().toString());
            TVCim.setText(currentItem.getJobAddress().getPostcode()
                    +" "+currentItem.getJobAddress().getCity()
                    +", "+currentItem.getJobAddress().getAddressRoad()
                    +" "+currentItem.getJobAddress().getHouseNum());
        }
    }
}
