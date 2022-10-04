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
@Table(name="AFMAST") // table name
@Entity(name = "AfmastEntity")
public class AfmastEntity {
    @Id
    @Column(name = "CUSTID", nullable = false)
    private String id;

    @Column(name = "STATUS")
    private String status;
}
