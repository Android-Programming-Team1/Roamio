package com.example.stamps.Data;

import androidx.annotation.NonNull;
public class Country {
    private long id;
    private String name;
    private String description;
    private Integer flagResId;
    private String photoUrl;

    public Country(long id, String name, String description, Integer flagResId, String photoUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.flagResId = flagResId;
        this.photoUrl = photoUrl;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getFlagResId() { return flagResId; }
    public void setFlagResId(int flagResId) { this.flagResId = flagResId; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}