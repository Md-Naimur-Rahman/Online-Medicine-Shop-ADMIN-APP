package com.sdmgapl1a0501.naimur.jpadmin.Common;


import com.sdmgapl1a0501.naimur.jpadmin.Model.Request;
import com.sdmgapl1a0501.naimur.jpadmin.Model.User;

public class Common {
    public static User currentUser;
    public static Request currentRequest;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    // Request to upload image
    public static final int PICK_IMAGE_REQUEST = 71;
    public static String convertCodeToStatus(String code){
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On my way";
        else
            return "Order Cancelled, Please call our hotline for information";
    }



}