package org.keycloak.quickstart.storage.user.representations;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.quickstart.storage.user.dao.UserDAO;
import org.keycloak.quickstart.storage.user.model.UserDto;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JBossLog
public class UserRepresentation extends AbstractUserAdapterFederatedStorage {
    private UserDto userDto;

    public static final String PHONE_ATTRIBUTE = "phone";
    public static final String ID_CODE_ATTRIBUTE = "idCode";
    public static final String FLEX_ID_ATTRIBUTE = "flex_cus_id";
    public static final String IS_RESET_PASSWORD = "is_reset";

    public UserRepresentation(KeycloakSession session,
                              RealmModel realm,
                              ComponentModel storageProviderModel,
                              UserDto userDto,
                              UserDAO userDAO) {
        super(session, realm, storageProviderModel);
        this.userDto = userDto;
        RoleModel user_flex = KeycloakModelUtils.getRoleFromString(realm, "user_flex");
        if (user_flex != null) {
            log.info("Role user flex exists in realm role");
            if (!hasRole(user_flex))
                this.grantRole(user_flex);
        } else {
            log.error("Role user flex not found in realm role");

        }

    }

    @Override
    public String getUsername() {
        return userDto.getUserName();
    }

    @Override
    public void setUsername(String username) {
//        userDto.setUserName(username);
//        userDto = userDAO.updateUser(userDto);
    }

    @Override
    public void setEmail(String email) {
//        userDto.setEmail(email);
//        userDto = userDAO.updateUser(userDto);
    }

    @Override
    public String getEmail() {
        return userDto.getEmail();
    }

    @Override
    public void setSingleAttribute(String name, String value) {
//        if (name.equals(PHONE_ATTRIBUTE)) {
//            userDto.setPhone(value);
//        } else {
//            super.setSingleAttribute(name, value);
//        }
    }

    @Override
    public void removeAttribute(String name) {
//        if (name.equals("phone")) {
//            userDto.setPhone(null);
//        } else {
//            super.removeAttribute(name);
//        }
//        userDto = userDAO.updateUser(userDto);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
//        if (name.equals("phone")) {
//            userDto.setPhone(values.get(0));
//        }else if(name.equals("idCode")) {
//            userDto.setPhone(values.get(0));
//        } else {
//            super.setAttribute(name, values);
//        }
//        userDto = userDAO.updateUser(userDto);
    }

    @Override
    public String getFirstAttribute(String name) {
        log.info("getFirstAttribute: " + name);
        if (name.equals(PHONE_ATTRIBUTE)) {
            return userDto.getPhone();
        } else if (name.equals(ID_CODE_ATTRIBUTE)) {
            return userDto.getIdCode();
        } else if (name.equals(FLEX_ID_ATTRIBUTE)) {
            return userDto.getId();
        } else if (name.equals(IS_RESET_PASSWORD)) {
            return userDto.getIsReset();
        } else {
            return super.getFirstAttribute(name);
        }
    }

    @Override
    public Map<String, List<String>> getAttributes() {

        Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attrs);
        all.add(PHONE_ATTRIBUTE, userDto.getPhone());
        all.add(ID_CODE_ATTRIBUTE, userDto.getIdCode());
        all.add(FLEX_ID_ATTRIBUTE, userDto.getId());
        all.add(IS_RESET_PASSWORD, userDto.getIsReset());
        log.info("getAttributes: " + all);
        return all;
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }

    @Override
    public List<String> getAttribute(String name) {
        log.info("getAttribute: " + name);

        if (name.equals(PHONE_ATTRIBUTE)) {
            return Collections.singletonList(userDto.getPhone());
        } else if (name.equals(ID_CODE_ATTRIBUTE)) {
            return Collections.singletonList(userDto.getIdCode());
        } else if (name.equals(FLEX_ID_ATTRIBUTE)) {
            return Collections.singletonList(userDto.getId());
        } else if (name.equals(IS_RESET_PASSWORD)) {
            return Collections.singletonList(userDto.getIsReset());
        } else {
            return super.getAttribute(name);
        }
    }

    @Override
    public String getFirstName() {
        return userDto.getFullName();
    }


    @Override
    public String getId() {
        return StorageId.keycloakId(storageProviderModel, userDto.getId());
    }

    public String getPassword() {
        return userDto.getPassword();
    }

    @Override
    public Set<RoleModel> getRealmRoleMappings() {
        return super.getRealmRoleMappings();
    }
}
