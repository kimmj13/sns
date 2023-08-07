package com.fastcampus.sns.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {

    //변화 가능성이 있는건 db가 아닌 서버에서 관리하는 것이 좋다
    NEW_COMMENT_ON_POST("new comment!"),
    NEW_LIKE_ON_POST("new like!"),;

    private final String alarmText;
}
