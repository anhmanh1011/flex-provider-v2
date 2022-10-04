package org.keycloak.quickstart.storage.user.utils;

import org.keycloak.quickstart.storage.user.entity.CfmastEntity;
import org.keycloak.quickstart.storage.user.entity.UserLoginEntity;
import org.keycloak.quickstart.storage.user.model.UserDto;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ConvertUtils {
    public static UserDto convertCfmastToUserDto(CfmastEntity cfmastEntity) {
        if (cfmastEntity == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setUserName(cfmastEntity.getUserName());
        userDto.setEmail(cfmastEntity.getEmail());
        userDto.setFullName(cfmastEntity.getFullName());
        userDto.setPhone(cfmastEntity.getPhone());
        userDto.setId(cfmastEntity.getId());
        userDto.setIdCode(cfmastEntity.getIdCode());
        return userDto;
    }

    public static UserDto convertToUserDto(Object[] objects) {
        CfmastEntity cfmastEntity = (CfmastEntity) objects[0];
        UserLoginEntity userLoginEntity = (UserLoginEntity) objects[1];
        if (cfmastEntity == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setUserName(cfmastEntity.getUserName());
        userDto.setEmail(cfmastEntity.getEmail());
        userDto.setFullName(cfmastEntity.getFullName());
        userDto.setPhone(cfmastEntity.getPhone());
        userDto.setId(cfmastEntity.getId());
        userDto.setIdCode(cfmastEntity.getIdCode());
        if(userLoginEntity != null){
            userDto.setIsReset(userLoginEntity.getIsReset());
        }
        return userDto;
    }

    public static UserDto convertToUserDto(List Objects) {
        CfmastEntity cfmastEntity = (CfmastEntity) Objects.get(0);
        UserLoginEntity userLoginEntity = (UserLoginEntity) Objects.get(1);
        UserDto userDto = new UserDto();
        userDto.setUserName(cfmastEntity.getUserName());
        userDto.setEmail(cfmastEntity.getEmail());
        userDto.setFullName(cfmastEntity.getFullName());
        userDto.setPhone(cfmastEntity.getPhone());
        userDto.setId(cfmastEntity.getId());
        userDto.setIdCode(cfmastEntity.getIdCode());
        if(userLoginEntity != null){
            userDto.setIsReset(userLoginEntity.getIsReset());
        }
        return userDto;
    }
    public static CfmastEntity convertUserDtoToEntity(UserDto userDto) {

        CfmastEntity cfmastEntity = new CfmastEntity();
        cfmastEntity.setUserName(userDto.getUserName());
        cfmastEntity.setEmail(userDto.getEmail());
        cfmastEntity.setFullName(userDto.getFullName());
        cfmastEntity.setPhone(userDto.getPhone());
        cfmastEntity.setId(userDto.getId());
        cfmastEntity.setIdCode(userDto.getIdCode());
        return cfmastEntity;
    }
}
