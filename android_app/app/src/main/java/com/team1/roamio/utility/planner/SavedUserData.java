package com.team1.roamio.utility.planner;

import com.team1.roamio.data.TravelPlanData;

import java.util.HashMap;

public class SavedUserData {
    public static int day = 0;
    public static boolean style = true;

    public static String hotelLocation = "";

    public static String country = "";

    public static HashMap<String, Boolean> userStyle = new HashMap<String, Boolean>();

    public static TravelPlanData planData = null;

    public static boolean isShowSavedData = false;
}
