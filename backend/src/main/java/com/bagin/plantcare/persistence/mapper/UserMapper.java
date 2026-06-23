package com.bagin.plantcare.persistence.mapper;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.persistence.tablemodel.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toDomain(UserEntity entity) {
    return new User(
        entity.getId(),
        entity.getEmail(),
        entity.getPasswordHash(),
        entity.getName()
    );
  }

  public UserEntity toEntity(User user) {
    UserEntity entity = new UserEntity();
    entity.setId(user.getId());
    entity.setEmail(user.getEmail());
    entity.setPasswordHash(user.getPasswordHash());
    entity.setName(user.getName());
    return entity;
  }
}