package com.os.appparent.model;

import com.google.gson.annotations.SerializedName;

public class DriveItem {
    @SerializedName("@microsoft.graph.downloadUrl")
    private String downUrl;

    private String name;
    private String id;

    public String getDownUrl() {
        return downUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
