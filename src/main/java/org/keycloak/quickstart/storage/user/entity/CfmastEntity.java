package org.keycloak.quickstart.storage.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedQueries({
//        @NamedQuery(name="getUserByUsername", query="select u from CfmastEntity u where lower(u.userName) = lower(:username)  or u.email = :email or u.phone = :phone and u.status = 'A' order by u.openTime desc "),
        @NamedQuery(name="getUserByUsernameOrEmail", query="select u from CfmastEntity u where (lower(u.userName) like lower(:username)  or u.email like :email or u.phone like :phone) and (u.status = 'A' or u.status = 'P') order by u.openTime desc "),
        @NamedQuery(name="getUserByEmail", query="select u from CfmastEntity u where u.email = :email and (u.status = 'A' or u.status = 'P') order by u.openTime desc "),
        @NamedQuery(name="getUserCount", query="select count(u) from CfmastEntity u  WHERE u.status = 'A' order by u.openTime desc "),
        @NamedQuery(name="searchForUser", query="select u from CfmastEntity u WHERE " +
                "( lower(u.userName) like :search or u.email like :search or u.phone like :search) and u.status = 'A' order by u.openTime desc "),
})

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Table(name="CFMAST") // table name
@Entity(name = "CfmastEntity")
public class CfmastEntity {
    @Id
    @Column(name = "CUSTID", nullable = false)
    private String id;

    @Column(name = "USERNAME")
    private String userName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "MOBILESMS")
    private String phone;

    @Column(name = "FULLNAME")
    private String fullName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IDCODE")
    private String idCode;

    @Column(name = "OPENTIME")
    private LocalDateTime openTime;

}
