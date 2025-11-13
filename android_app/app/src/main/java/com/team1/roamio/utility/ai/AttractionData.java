package com.team1.roamio.utility.ai;

public class AttractionData {
    private String name;      // 관광지 이름
    private String address;   // 위치
    private String category;  // 카테고리 (예: 음식점, 명소, 박물관)
    private int starPoint;    // 별점
    private String uri;       // 링크

    // 기본 생성자 (JSON 라이브러리에서 사용 가능)
    public AttractionData() {
    }

    // 모든 필드를 받는 생성자
    public AttractionData(String name, String address, String category, int starPoint, String uri) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.starPoint = starPoint;
        this.uri = uri;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    public int getStarPoint() {
        return starPoint;
    }

    public String getUri() {
        return uri;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStarPoint(int starPoint) {
        this.starPoint = starPoint;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "AttractionData{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", starPoint=" + starPoint +
                '}';
    }
}