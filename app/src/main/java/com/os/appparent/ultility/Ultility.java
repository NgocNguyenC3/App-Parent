package com.os.appparent.ultility;

import android.content.Context;
import android.widget.Toast;

public class Ultility {
    public static final int STATE_LT = 0;
    public static final int STATE_SCHEDULE = 1;
    public static final int REQUEST_CODE_EDIT = 5;
    public static final int REQUEST_CODE_ADD_LT = 30;
    public static final int REQUEST_CODE_ADD_SCHEDULE = 10;
    public static final int REQUEST_CODE_ADD = 25;
    public static final String JSON_CODE_CREATE_FOLDER = "{\n" +
            "  \"name\": \"flag\",\n" +
            "  \"folder\": { },\n" +
            "  \"@microsoft.graph.conflictBehavior\": \"replace\"\n" +
            "}";
    public static final String JSON_CODE_CREATE_DRIVE = "{\n" +
            "  \"name\": \"flag\",\n" +
            "  \"folder\": { },\n" +
            "  \"@microsoft.graph.conflictBehavior\": \"fail\"\n" +
            "}";
    public static void printDisplay(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
