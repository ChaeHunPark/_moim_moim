package com.example.MoimMoim.enums;

public enum MoimStatus {
    RECRUITING("모집 중"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료됨"),
    CANCELLED("취소됨");

    private final String description;

    MoimStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
