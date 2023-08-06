package com.fastcampus.sns.model;

import com.fastcampus.sns.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

//todo : implement
//dto
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class User {

    private Long id;
    private String userName;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    //entity -> dto
    public static User fromEntity(UserEntity entity) {
        return new User(entity.getId(), entity.getUserName(), entity.getPassword(), entity.getRole(), entity.getRegisteredAt(), entity.getUpdatedAt(), entity.getDeletedAt());
    }
}
