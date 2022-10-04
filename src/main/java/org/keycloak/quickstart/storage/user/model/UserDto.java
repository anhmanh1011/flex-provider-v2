package org.keycloak.quickstart.storage.user.model;

import lombok.Data;

@Data
public class UserDto {
    String id;
    String userName;
    String password;
    String email;
    String phone;
    String fullName;
    String idCode;
    String isReset;
}
