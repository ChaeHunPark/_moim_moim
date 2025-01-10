package com.example.MoimMoim.dto.MapSearch;


import lombok.NoArgsConstructor;



/*
*
* */

@NoArgsConstructor
public class SearchRequestDTO {

    private String title;
    private String category;
    private String address;
    private String roadAddress;
    private String mapx;  // 소수점으로 변환된 좌표
    private String mapy;  // 소수점으로 변환된 좌표

    // Getter & Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }


    /*
    * 네이버 검색 api는 원시 데이터는 String이다.
    * double로 받을 시 데이터가 손상될 가능성이 있으므로 String으로 받은 후 getter에서 소수점 변환을 하는게 안전하다.
    * */
    public double getMapx() {
        // "1272086715" -> 127.2086715 변환
        return Double.parseDouble(mapx) / 1_000_0000.0;
    }

    public void setMapx(String mapx) {
        this.mapx = mapx;
    }

    public double getMapy() {
        // "375402987" -> 37.5402987 변환
        return Double.parseDouble(mapy) / 1_000_0000.0;
    }

    public void setMapy(String mapy) {
        this.mapy = mapy;
    }

}
