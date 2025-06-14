package com.example.pe_haquapp.model;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JobAddress {
    private final String LOG_TAG = JobAddress.class.getName();
    private int postcode;
    private String city;
    private String addressRoad;
    private int houseNum;

    public JobAddress() {

    }

    public JobAddress(Activity baseActivity, String city, String addressRoad, int houseNum) throws RuntimeException {
        this.city = city;
        String[] roadParts = addressRoad.split(" ");
        String rightRoad = "";
        switch (roadParts[roadParts.length-1]){
            case "sugárút": case "sgt":
                roadParts[roadParts.length - 1] = "sgt.";
                break;
            case "körút": case "krt":
                roadParts[roadParts.length - 1] = "krt.";
                break;
            case "u":
                roadParts[roadParts.length - 1] = "utca";
                break;
        }
        for (String roadPart : roadParts) {
            rightRoad = rightRoad + " "+ roadPart.trim();
        }
        this.addressRoad =rightRoad;
        this.houseNum = houseNum;
        Geocoder geocoder = new Geocoder(baseActivity);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(city + " " + addressRoad + " " + houseNum, 1);
            Log.i(LOG_TAG, this.city + " " + this.addressRoad + " " + this.houseNum);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String postalCode = null;
        postalCode = addressList.get(0).getPostalCode();
        if (postalCode != null) {
            postcode = Short.parseShort(postalCode);
        }
    }

    public int getPostcode() {
        return postcode;
    }

    public String getAddressRoad() {
        return addressRoad;
    }

    public String getCity() {
        return city;
    }

    public int getHouseNum() {
        return houseNum;
    }

    public boolean equals(JobAddress other) {
        return this.getCity().equals(other.getCity()) &&
                this.getAddressRoad().equals(other.getAddressRoad()) &&
                this.getHouseNum() == other.getHouseNum();
    }

    public enum constraintType{
        FULL_ADDRESS,
        CITY_ADDRESS,
        ROAD_ADDRESS
    };

    public boolean isStringApproipate(String matchedPart, constraintType type) {
        String cleared = matchedPart.replace(","," ");
        Log.i(LOG_TAG, "Cleared:" + type);
        String[] parts = cleared.split(" ");
        for (int i = 0; i < parts.length; i++){
            Log.i(LOG_TAG, parts[i]);
        }
        switch (type){
            case FULL_ADDRESS:
                if (Integer.parseInt(parts[0].replace(",","").trim()) == getPostcode()) {
                    Log.i(LOG_TAG, "Irsz: "+parts[0]);
                    if (Integer.parseInt(parts[parts.length - 1]) == getHouseNum()) {
                        Log.i(LOG_TAG, "Housenum: "+ parts[parts.length - 1]);
                        switch (parts[parts.length - 2]) {
                            case "sugárút": case "sgt":
                                parts[parts.length - 2] = "sgt.";
                                break;
                            case "körút": case "krt":
                                parts[parts.length - 2] = "krt.";
                                break;
                            case "u":
                                parts[parts.length - 2] = "utca";
                                break;
                        }
                        for (int i=0; i< parts.length; i++){
                            Log.i(LOG_TAG, parts[i]);
                        }
                        String[] roadParts = getAddressRoad().trim().split(" ");
                        if (roadParts.length == parts.length - 3) {
                            int i = 0;
                            while (i < roadParts.length && roadParts[i].toLowerCase().equals(parts[i + 2].toLowerCase().trim())) {

                                i++;
                            }
                            return i == roadParts.length;
                        }
                    }
                }
                break;
            case CITY_ADDRESS:
                String clearedCity = parts[0].replace(",", "").substring(0,1).toUpperCase()+ parts[0].replace(",", "").toLowerCase();
                if (clearedCity.trim().equals(getCity().trim())) {
                    if (Integer.parseInt(parts[parts.length - 1].trim()) == getHouseNum()) {
                        Log.i(LOG_TAG, parts[parts.length-2]);
                        switch (parts[parts.length - 2]) {
                            case "sugárút": case "sgt":
                                parts[parts.length - 2] = "sgt.";
                                break;
                            case "körút": case "krt":
                                parts[parts.length - 2] = "krt.";
                                break;
                            case "u":
                                parts[parts.length - 2] = "utca";
                                break;
                        }
                        Log.i(LOG_TAG, "StreetType: "+parts[parts.length-2]);
                        for (int i=0; i< parts.length; i++){
                            Log.i(LOG_TAG, parts[i]);
                        }

                        String[] roadParts = getAddressRoad().trim().split(" ");
                        Log.i(LOG_TAG, String.valueOf(roadParts.length)+"|"+String.valueOf(parts.length));
                        for (int i = 0; i < roadParts.length; i++){
                            Log.i(LOG_TAG,"RoadPart:"+ roadParts[i]);
                        }
                        if (roadParts.length == parts.length - 2) {
                            int i = 0;
                            Log.i(LOG_TAG, roadParts[i]+"|"+parts[i+1]);
                            while (i < roadParts.length && roadParts[i].toLowerCase().trim().equals(parts[i + 1].toLowerCase().trim())) {
                                Log.i(LOG_TAG, roadParts[i]+"|"+parts[i]);
                                i++;
                            }
                            return i == roadParts.length;
                        }
                    }
                }
                else {
                    if (Integer.parseInt(parts[parts.length - 1].trim()) == getHouseNum()) {
                        switch (parts[parts.length - 2]) {
                            case "sugárút": case "sgt":
                                parts[parts.length - 2] = "sgt.";
                                break;
                            case "körút": case "krt":
                                parts[parts.length - 2] = "krt.";
                                break;
                            case "u":
                                parts[parts.length - 2] = "utca";
                                break;
                        }
                        Log.i(LOG_TAG, "StreetType: "+parts[parts.length-2]);

                        String[] roadParts = getAddressRoad().trim().split(" ");
                        Log.i(LOG_TAG, parts.length+"|"+ roadParts.length);
                        if (roadParts.length == parts.length - 1) {
                            int i = 0;
                            while (i < roadParts.length && roadParts[i].toLowerCase().trim().equals(parts[i].toLowerCase().trim())) {
                                Log.i(LOG_TAG, roadParts[i]+"|"+parts[i]);
                                i++;
                            }
                            return i == roadParts.length;
                        } else if (roadParts.length == parts.length - 2) {
                            int i = 0;
                            while (i < roadParts.length && roadParts[i].toLowerCase().trim().equals(parts[i+1].toLowerCase().trim())) {
                                Log.i(LOG_TAG, roadParts[i]+"|"+parts[i]);
                                i++;
                            }
                            return i == roadParts.length;
                        }
                    }
                }
                break;
        }
        return false;
    }
}
