package com.os.appparent.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FolderDrive {
    @SerializedName("@odata.count")
    private int count;
    private List<DriveItem> value;

    public int getCount() {
        return count;
    }
    public List<DriveItem> getValue() {
        return value;
    }

}
