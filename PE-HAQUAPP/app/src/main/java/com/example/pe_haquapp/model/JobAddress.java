package com.example.pe_haquapp.model;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JobAddress {
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
        }catch (IOException ignored){

        }
        String postalCode = addressList.get(0).getPostalCode();
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
