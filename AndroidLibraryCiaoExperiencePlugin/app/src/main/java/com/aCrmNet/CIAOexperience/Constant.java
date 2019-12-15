package com.aCrmNet.CIAOexperience;

/**
 * Created by Andrea on 12/02/2017.
 */

public class Constant {
    private static int screenWidth =0;
    public static boolean isLogged = false;
    public static int CURRENT_STATUS;
    public static String ROOT_PATH;
    public static boolean IS_LOCALE = false;

    ///CLICK ACTION
    public static final int IMAGE =0;
    public static final int ITEM =1;
    public static final int LIKE =2;
    public static final int SHARE =3;
    public static final int CATEGORY = 4;
    public static final int MAP = 5;
    public static final int PROMO_CODE = 6;


    ///STATUS LIST
    public static final int HOME = 0;
    public static final int DEFAULT = 1;
    public static final int RECOMMENDATION =2;
    public static final int ACCOMMODATION =3;
    public static final int BUSINESS_CATEGORY =4;
    public static final int EXPERIENCE = 5;

    ///INITDASHBOARD
    public static String DASHBOARD = "";

    //LIST ITEM TYPE
    public static final int SIMPLE =0;
    public static final int HORIZONTAL_LIST =1;




    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int get75pScreen(){
        return screenWidth*75/100;
    }

    public static void setScreenWidth(int screenWidth) {
        Constant.screenWidth = screenWidth;
    }
}
