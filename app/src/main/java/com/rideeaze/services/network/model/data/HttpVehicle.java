package com.rideeaze.services.network.model.data;

import com.rideeaze.R;

public class HttpVehicle {

    public static String ANY = "Any";
    public static String SEDAN = "Sedan";
    public static String SUV = "SUV";
    public static String LIMOUSINE = "Limousine";
    public static String VAN = "Van";
    public static String MINI_BUS = "Mini Bus";
    public static String SHUTTLE = "Shuttle";
    public static String LIMO_BUS = "Limo Bus";
    public static String COACH_BUS = "Coach Bus";
    public static String STRETCHED_SUV = "Stretched SUV";
    public static String PARTY_BUS = "Party Bus";

    public String VehicleNumber;
    public String Style;
    public String Color;
    public String Make;
    public String Year;
    public String Model;
    public String Class;

    public String NumberOfPass;

    public Boolean NoSmoking;
    public Boolean TakeCC;
    public Boolean HandicapAccess;

    public int getCarSmallImg() {
        int idResource =  R.drawable.car00;
        int currentStyleId = 0;
        int currentColorId = 0;

        if(this.Style != null) {
            if (this.Style.equals(SEDAN)) {
                currentStyleId = 0;
            } else if (this.Style.equals(SUV)) {
                currentStyleId = 4;
            } else if (this.Style.equals(VAN)) {
                currentStyleId = 5;
            } else if (this.Style.equals(LIMOUSINE)) {
                currentStyleId = 6;
            } else if (this.Style.equals(MINI_BUS)) {
                currentStyleId = 5;
            } else if (this.Style.equals(SHUTTLE)) {
                currentStyleId = 4;
            } else if (this.Style.equals(LIMO_BUS)) {
                currentStyleId = 6;
            } else if (this.Style.equals(COACH_BUS)) {
                currentStyleId = 5;
            } else if (this.Style.equals(STRETCHED_SUV)) {
                currentStyleId = 4;
            } else if (this.Style.equals(PARTY_BUS)) {
                currentStyleId = 5;
            }
        }


        if(this.Color != null) {
            if (this.Color.equals("Yellow")) {
                currentColorId = 0;
            } else if (this.Color.equals("White")) {
                currentColorId = 1;
            } else if (this.Color.equals("Black")) {
                currentColorId = 2;
            } else if (this.Color.equals("Red")) {
                currentColorId = 3;
            } else if (this.Color.equals("Green")) {
                currentColorId = 4;
            } else if (this.Color.equals("Blue")) {
                currentColorId = 5;
            }
        }

        return idResource+currentStyleId+currentColorId;
    }
}
