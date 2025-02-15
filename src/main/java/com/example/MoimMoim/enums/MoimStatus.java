package com.example.MoimMoim.enums;

public enum MoimStatus {
    RECRUITING("모임 모집중"),       // 모임 모집중
    FULL("모임 인원 마감"),                   // 모임 인원 마감
    COMPLETED("모임 종료"),
    CANCELED("모임 취소");

    private final String description;

    MoimStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
