package com.example.MoimMoim.enums;

public enum Gender {
    MALE("남"),
    FEMALE("여");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
