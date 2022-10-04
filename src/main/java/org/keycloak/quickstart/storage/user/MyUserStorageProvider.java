/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.quickstart.storage.user;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.quickstart.storage.user.dao.UserDAO;
import org.keycloak.quickstart.storage.user.model.UserDto;
import org.keycloak.quickstart.storage.user.representations.UserRepresentation;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MyUserStorageProvider implements UserStorageProvider,
        UserLookupProvider,
        UserRegistrationProvider,
        UserQueryProvider,
        CredentialInputUpdater,
        CredentialInputValidator {
    private static final Logger log = Logger.getLogger(MyUserStorageProvider.class);

    protected ComponentModel model;
    protected KeycloakSession session;
    private final UserDAO userDAO;

    protected EntityManager em;

    MyUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        em = session.getProvider(JpaConnectionProvider.class, "flex-store").getEntityManager();
        userDAO = new UserDAO(em);
    }


    @Override
    public void close() {
        userDAO.close();
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.info("isConfiguredFor(" + realm + ", " + user + ", " + credentialType + ")");
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        return userDAO.validateCredentials(user.getUsername(), cred.getChallengeResponse());
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.info("supportsCredentialType(" + credentialType + ")");
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel userModel, CredentialInput input) {
        log.info("updateCredential: " + userModel.getUsername());
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        try {
            return userDAO.updatePassword(userModel.getUsername(), cred.getChallengeResponse());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        log.info("disableCredentialType(" + realm + ", " + user + ", " + credentialType + ")");

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }

    public UserRepresentation getUserRepresentation(UserModel user) {
        UserRepresentation userRepresentation = null;
        if (user instanceof CachedUserModel) {
            userRepresentation = (UserRepresentation) ((CachedUserModel) user).getDelegateForUpdate();
        } else {
            userRepresentation = (UserRepresentation) user;
        }
        return userRepresentation;
    }

    public UserRepresentation getUserRepresentation(UserDto user, RealmModel realm) {
        return new UserRepresentation(session, realm, model, user, userDAO);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        log.info("getUsersCount(" + realm + ")");
        return (int) userDAO.size();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        log.info("getUsers(" + realm + ")");
        return userDAO.findAll()
                .stream()
                .map(user -> new UserRepresentation(session, realm, model, user, userDAO))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        log.info("getUsers(RealmModel realm, int firstResult, int maxResults)");
        return userDAO.findAll(firstResult, maxResults)
                .stream()
                .map(user -> new UserRepresentation(session, realm, model, user, userDAO))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        log.info("searchForUser(String search, RealmModel realm)");
        return userDAO.searchForUserByUsernameOrEmail(search)
                .stream()
                .map(user -> new UserRepresentation(session, realm, model, user, userDAO))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        log.info("searchForUser(String search, RealmModel realm, int firstResult, int maxResults)");
        return userDAO.searchForUserByUsernameOrEmail(search, firstResult, maxResults)
                .stream()
                .map(user -> new UserRepresentation(session, realm, model, user, userDAO))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        log.info("searchForUser(params: " + params + ", realm: " + realm + ")");
        // TODO Will probably never implement; Only used by REST API

        return userDAO.searchForUserByParam(params, null, null)
                .stream()
                .map(user -> new UserRepresentation(session, realm, model, user, userDAO))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult,
                                         int maxResults) {
        return userDAO.searchForUserByParam(params, firstResult, maxResults)
                .stream()
                .map(user -> new UserRepresentation(session, realm, model, user, userDAO))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        // TODO Will probably never implement
        return new ArrayList<>();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        // TODO Will probably never implement
        return new ArrayList<>();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        // TODO Will probably never implement
        if (attrName.equals("phone")) {
            Map<String, String> map = new HashMap<>();
            map.put("phone", attrValue);
            return userDAO.searchForUserByParam(map, null, null)
                    .stream()
                    .map(user -> new UserRepresentation(session, realm, model, user, userDAO))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public UserModel getUserById(String keycloakId, RealmModel realm) {
        // keycloakId := keycloak internal id; needs to be mapped to external id
        log.info("getUserById(String keycloakId, RealmModel realm)");
        String id = StorageId.externalId(keycloakId);
        return new UserRepresentation(session, realm, model, userDAO.getUserById(id), userDAO);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.info("getUserByUsername(String username, RealmModel realm)");
        Optional<UserDto> optionalUser = userDAO.getUserByUsername(username);
        return optionalUser.map(user -> getUserRepresentation(user, realm)).orElse(null);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.info("getUserByEmail(String email, RealmModel realm)");
        Optional<UserDto> optionalUser = userDAO.getUserByEmail(email);
        if (optionalUser.isPresent())
            return optionalUser.map(user -> getUserRepresentation(user, realm)).orElse(null);
        else return null;
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        return false;
    }
}
