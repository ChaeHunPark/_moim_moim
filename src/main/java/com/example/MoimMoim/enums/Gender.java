package com.example.MoimMoim.enums;

public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


}
