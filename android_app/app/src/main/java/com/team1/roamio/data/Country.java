package com.team1.roamio.data;

public class Country {
    private long id;            // Country ID
    private String region;      // 지역
    private String name;        // 국가
    private String city;        // 도시
    private String description; // 설명
    private String photoUrl;    // 국가 사진
    private String stampName;   // 국가별 stamp image

    public Country(long id, String region, String name, String city, String description, String photoUrl, String stampName) {
        this.id = id;
        this.region = region;
        this.name = name;
        this.city = city;
        this.description = description;
        this.photoUrl = photoUrl;
        this.stampName = stampName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getStampName() {return stampName;}

    public void setStampName(String stampName) {this.stampName = stampName;}
}