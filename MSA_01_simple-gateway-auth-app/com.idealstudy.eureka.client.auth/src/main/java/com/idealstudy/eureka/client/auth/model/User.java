package com.idealstudy.eureka.client.auth.model;

import com.idealstudy.eureka.client.auth.common.shared.BaseEntity;
import com.idealstudy.eureka.client.auth.common.shared.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "p_user")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    // 유저 생성 메서드
    public static User create(String username, String password, UserRole role) {

        User user = new User();
        user.username = username;
        user.password = password;
        user.role = role;

        return user;
    }

    // 비밀번호 업데이트 메서드
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}