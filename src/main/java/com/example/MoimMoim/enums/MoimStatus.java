package com.example.MoimMoim.enums;

public enum MoimStatus {
    모집중("모임 모집중"),
    인원마감("모임 인원 마감"),
    모임진행중("모임 진행중"),
    종료("모임 종료"),
    취소("모임 취소");

    private final String description;

    MoimStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
