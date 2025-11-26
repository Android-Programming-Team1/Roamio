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

    public static int resultShowType = 0; //0: new, 1: saved, 2:fix

    public static String fixTarget = "";

    public static String fixResult = "";

    public static boolean isBackFromFix = false;

    public static final int SHOW_NEW = 0;
    public static final int SHOW_SAVED = 1;
    public static final int SHOW_FIX = 2;
}
