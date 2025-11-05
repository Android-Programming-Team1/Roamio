package com.example.stamps.Data;

public class Stamp {         // DB 기본키
    private long id;
    private long countryId;
    private Long stampedAt;
    private Integer imageResId;
    private String imageUri;
    private String imageUrl;

    public Stamp(long id, long countryId, Long stampedAt, Integer imageResId, String imageUri, String imageUrl) {
        this.id = id;
        this.countryId = countryId;
        this.stampedAt = stampedAt;
        this.imageResId = imageResId;
        this.imageUri = imageUri;
        this.imageUrl = imageUrl;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCountryId() { return countryId; }
    public void setCountryId(long countryId) { this.countryId = countryId; }

    public long getStampedAt() { return stampedAt; }
    public void setStampedAt(long stampedAt) { this.stampedAt = stampedAt; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
