package com.example.pe_haquapp.model;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobAddress {
    private final String LOG_TAG = JobAddress.class.getName();
    private int postcode;
    private String city;
    private String addressRoad;
    private int houseNum;

    public JobAddress(){

    }
    public JobAddress(Activity baseActivity, String city, String addressRoad, int houseNum){
        this.city = city;
        this.addressRoad = addressRoad;
        this.houseNum = houseNum;
        Geocoder geocoder = new Geocoder(baseActivity);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(city+" "+addressRoad+" "+houseNum,1);
            Log.i(LOG_TAG, this.city+" "+this.addressRoad+" "+this.houseNum);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
            //throw new RuntimeException(e);
        }
        String postalCode = null;
        postalCode = addressList.get(0).getPostalCode();
        if (postalCode != null){
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
    public boolean equals(JobAddress other){
        return this.getCity().equals(other.getCity()) &&
                this.getAddressRoad().equals(other.getAddressRoad()) &&
                this.getHouseNum() == other.getHouseNum();
    }
}
