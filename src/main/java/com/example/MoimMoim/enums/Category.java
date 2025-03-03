package com.example.MoimMoim.enums;

public enum Category {
    SPORTS("스포츠"),
    STUDY("스터디"),
    HOBBY("취미"),
    VOLUNTEER("봉사활동"),
    TRAVEL("여행"),
    FOOD("맛집 탐방"),
    MUSIC("음악"),
    ART("예술"),
    TECHNOLOGY("기술/IT"),
    LANGUAGE("언어/문화 교류"),
    BUSINESS("비즈니스/창업"),
    FITNESS("헬스/요가"),
    GAME("게임/보드게임"),
    OUTDOOR("캠핑/등산"),
    PET("반려동물"),
    MOVIE("영화/드라마"),
    BOOK("독서/토론"),
    PHOTOGRAPHY("사진/영상"),
    WELLNESS("명상/마음 챙김"),
    KIDS("아이/가족"),
    DIY("DIY/공예"),
    SOCIAL("사회적 네트워킹"),
    ENVIRONMENT("환경/에코"),
    ETC("기타");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() { // displayName getter
        return label;
    }


}
