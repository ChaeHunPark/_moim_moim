package com.example.MoimMoim.enums;

public enum ParticipationStatus {

    ACCEPTED("수락"),
    REJECTED("거절"),
    PENDING("대기"),
    CANCELED("취소");

    private final String label;

    ParticipationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


}
