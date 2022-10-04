package org.keycloak.quickstart.storage.user.dao;


import org.apache.commons.lang.StringUtils;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.quickstart.storage.user.entity.CfmastEntity;
import org.keycloak.quickstart.storage.user.entity.UserLoginEntity;
import org.keycloak.quickstart.storage.user.model.UserDto;
import org.keycloak.quickstart.storage.user.utils.ConvertUtils;
import lombok.extern.jbosslog.JBossLog;

import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UserDAO {

//    public static final int MAX_RESULT = 50;

    private EntityManager entityManager;
    Logger log = Logger.getLogger(UserDAO.class.getName());

    public UserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<UserDto> findAll() {
        return findAll(null, null);
    }

    public List<UserDto> findAll(int start, int max) {
        return findAll((Integer) start, (Integer) max);
    }

    private List<UserDto> findAll(Integer start, Integer max) {
        TypedQuery<CfmastEntity> query = entityManager.createNamedQuery("searchForUser", CfmastEntity.class);
        query.setParameter("search", "%");

        if (start != null) {
            query.setFirstResult(start);
        }
        if (max != null) {
            query.setMaxResults(max);
        }


        List<CfmastEntity> users = query.getResultList();
        return users.stream().map(ConvertUtils::convertCfmastToUserDto).collect(Collectors.toList());
    }

    public Optional<UserDto> getUserByUsername(String username) {
        log.info("getUserByUsername(username: " + username + ")");
        log.info("getUserByUsername(username: " + username + ")");

        Query query = entityManager.createQuery("SELECT u ,user FROM CfmastEntity u inner join UserLoginEntity user on  (u.userName= user.username and user.status = 'A') where (lower(u.userName) = lower(:username)  or u.email = :email or u.phone = :phone) and (u.status = 'A' or u.status = 'P') order by u.openTime desc ")
                .setParameter("username", username)
                .setParameter("email", username)
                .setParameter("phone", username);
        List resultList = query.getResultList();
        if(CollectionUtil.isEmpty(resultList))
            return Optional.empty();
        return Optional.ofNullable(ConvertUtils.convertToUserDto(resultList));
//        return query.getResultList().stream().map(ConvertUtils::convertToUserDto).findFirst();
    }

    public Optional<UserDto> getUserByEmail(String email) {
        log.info("getUserByEmail(email: " + email + ")");
//        TypedQuery<CfmastEntity> query = entityManager.createNamedQuery("getUserByEmail", CfmastEntity.class);
//        query.setParameter("email", email);
        return getUserByUsername(email);
    }

    public List<UserDto> searchForUserByUsernameOrEmail(String searchString) {
        log.info("searchForUserByUsernameOrEmail(searchString: " + searchString + ")");
        return searchForUserByUsernameOrEmail(searchString, null, null);
    }

    public List<UserDto> searchForUserByUsernameOrEmail(String searchString, int start, int max) {
        log.info("searchForUserByUsernameOrEmail(searchString: " + searchString + ", start: " + start + ", max: " + max + ")");
        return searchForUserByUsernameOrEmail(searchString, (Integer) start, (Integer) max);
    }

    private List<UserDto> searchForUserByUsernameOrEmail(String searchString, Integer start, Integer max) {
        log.info("searchForUserByUsernameOrEmail(searchString: " + searchString + ", start: " + start + ", max: " + max + ")");
        TypedQuery<CfmastEntity> query = entityManager.createNamedQuery("getUserByUsernameOrEmail", CfmastEntity.class);
        query.setParameter("username", "%" + searchString + "%");
        query.setParameter("email", "%" + searchString + "%");
        query.setParameter("phone", "%" + searchString + "%");
        if (start != null) {
            query.setFirstResult(start);
        }
        if (max != null) {
            query.setMaxResults(max);

        }
//        else
//            query.setMaxResults(MAX_RESULT);
        return query.getResultList().stream().map(ConvertUtils::convertCfmastToUserDto).collect(Collectors.toList());
    }

    public List<UserDto> searchForUserByParam(Map<String, String> param, Integer start, Integer max) {
        log.info("searchForUserByParam(param: " + param + ", start: " + start + ", max: " + max + ")");
        System.out.println("searchForUserByParam(param: " + param + ", start: " + start + ", max: " + max + ")");
        String queryDB = "SELECT c,user FROM CfmastEntity c inner join UserLoginEntity user on  (c.userName= user.username and user.status = 'A')  where (lower(c.userName) like lower(:username) or c.email like :email or c.phone like :phone) and (c.status = 'A' or c.status = 'P')";
        Query query = entityManager.createQuery(queryDB);

        if (!param.isEmpty()) {
            if (StringUtils.isNotBlank(param.get("username")))
                query.setParameter("username", "%" + param.get("username") + "%");
            else
                query.setParameter("username", null);

            if (StringUtils.isNotBlank(param.get("email")))
                query.setParameter("email", "%" + param.get("email") + "%");
            else
                query.setParameter("email", null);

            if (StringUtils.isNotBlank(param.get("phone")))
                query.setParameter("phone", "%" + param.get("phone") + "%");
            else
                query.setParameter("phone", null);
        } else {
            query.setParameter("username", "%");
            query.setParameter("email", "%");
            query.setParameter("phone", "%");
        }

        if (start != null) {
            query.setFirstResult(start);
        }
        if (max != null) {
            query.setMaxResults(max);

        }

        List<Object[]> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.stream().map(ConvertUtils::convertToUserDto).collect(Collectors.toList());

        }
        return null;
    }

    public UserDto getUserById(String id) {
        log.info("getUserById(id: " + id + ")");
        CfmastEntity cfmastEntity = entityManager.find(CfmastEntity.class, id);
        UserLoginEntity userLoginEntity = (UserLoginEntity) entityManager.createQuery("SELECT user from UserLoginEntity  user where user.username = :username and user.status = 'A'").setParameter("username", cfmastEntity.getUserName()).getSingleResult();

        UserDto userDto = ConvertUtils.convertCfmastToUserDto(cfmastEntity);
        if (userDto != null) {
            userDto.setIsReset(userLoginEntity.getIsReset());
        }
        return userDto;
    }


    public void close() {
        this.entityManager.close();
    }

//    public UserDto updateUser(UserDto userDto) {
//        CfmastEntity cfmastEntity = ConvertUtils.convertUserDtoToEntity(userDto);
//        EntityTransaction transaction = entityManager.getTransaction();
//        transaction.begin();
//        entityManager.merge(cfmastEntity);
//        transaction.commit();
//        return userDto;
//    }

    public boolean updatePassword(String userName, String password) throws Exception {
        log.info("updatePassword(id: " + userName + ")");
        String md5Password = encodeMD5(password);

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            String qryString = "update UserLoginEntity s set s.password=:password , s.isReset = 'N' where lower(s.username) =lower(:username) and s.status='A' ";
            Query query = entityManager.createQuery(qryString)
                    .setParameter("username", userName)
                    .setParameter("password", md5Password);
            int i = query.executeUpdate();
            log.info("update " + i + " record to DB , username: " + userName);
            if (i == 1) {
                transaction.commit();
                return true;
            } else {
                log.info("update password false , record: " + i);
                transaction.rollback();
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            transaction.rollback();
            throw new Exception("update password failed");
        }
    }

    public long size() {
        return entityManager.createNamedQuery("getUserCount", Long.class).getSingleResult();
    }

    public boolean validateCredentials(String username, String challengeResponse) {
        log.info("validateCredentials( " + username);
        String md5Password = encodeMD5(challengeResponse);

//        log.info("validateCredentials( " + username + " pass: " + challengeResponse);
        String queryDB = "SELECT count(user) from UserLoginEntity user inner join CfmastEntity cf on user.username = cf.userName inner join AfmastEntity  af on cf.id = af.id where user.username = (SELECT c.userName FROM CfmastEntity  c where lower(c.userName) = lower(:username) or c.email = :email or c.phone = :phone ) " +
                "and user.password = :password and user.status='A' and (cf.status = 'A' or cf.status = 'P') and af.status = 'A' ";
        log.info("queryDB: " + queryDB);

        TypedQuery<Long> query = entityManager.createQuery(queryDB, Long.class);
        query.setParameter("username", username);
        query.setParameter("email", username);
        query.setParameter("phone", username);
        query.setParameter("password", md5Password);
        try {
            long count = query.getSingleResult();
            return count > 0;
        } catch (NoResultException ex) {
            log.log(Level.WARNING,ex.getMessage());
            ex.printStackTrace();
            log.info("login faild  :  " + username);
            return false;
        }
    }

    private String encodeMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter
                    .printHexBinary(digest).toLowerCase();

        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            log.info("encode md5 fail " + ex.getMessage());
            return null;
        }
    }

}
