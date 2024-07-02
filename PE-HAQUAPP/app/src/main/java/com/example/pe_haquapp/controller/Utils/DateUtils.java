package com.example.pe_haquapp.controller.Utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    private static ArrayList<Integer> long30= new ArrayList<>(List.of(4,6,9,11));

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate getDateAMonthBefore(LocalDate date){
        if (date.getDayOfMonth() == 31 && long30.contains(date.getMonthValue()-1)){
            return LocalDate.of(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth()-1);
        } else if (date.getDayOfMonth() == 31 && date.getMonthValue()-1 == 2) {
            return LocalDate.of(date.getYear(), date.getMonthValue()-1, 29);
        }
        else if (date.getMonthValue()==1){
            return LocalDate.of(date.getYear()-1, 12, date.getDayOfMonth());
        }
        else {
            return LocalDate.of(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth());
        }
    }
}
