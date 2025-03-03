package com.example.MoimMoim.enums;

import java.util.Arrays;

public enum MoimStatus {
    RECRUITING("모임 모집중"),       // 모임 모집중
    FULL("모임 인원 마감"),                   // 모임 인원 마감
    COMPLETED("모임 종료"),
    CANCELED("모임 취소");

    private final String displayName;

    MoimStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
