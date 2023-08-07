package com.fastcampus.sns.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmArgs {

    //알람 발생자
    private Integer fromUserId;

    //알람 주체 (post, comment ..)
    private Integer targetId;

}


// comment : oo 씨가 새 코멘트를 작성했습니다 -> postId, commentId

// oo외 2명이 새 코멘트를 작성했습니다 -> commentId, commentId