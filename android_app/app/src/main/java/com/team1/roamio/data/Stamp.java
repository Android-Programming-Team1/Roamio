package com.team1.roamio.data;

public class Stamp {         // DB 기본키
    private long id;            // Stamp ID
    private long countryId;     // Country Id 외래키 참조
    private Long stampedAt;     // 스탬프 시각
    private Integer imageResId; // 스탬프 이미지
    private String imageUri;    // 로컬 이미지, 사진을 스탬프로 등록할 때 이용
    private String imageUrl;    // 외부 이미지, 확장성 고려

    public Stamp() {}

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
