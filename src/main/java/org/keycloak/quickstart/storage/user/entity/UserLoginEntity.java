package org.keycloak.quickstart.storage.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity(name = "UserLoginEntity")
@Table(name = "USERLOGIN")
public class UserLoginEntity {

    @Id
    @Column(name = "USERNAME")
    private String username;

    @Column(name = "LOGINPWD")
    private String password;

    @Column(name = "ISRESET")
    private String isReset;

    @Column(name = "STATUS")
    private String status;
}
