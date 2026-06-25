package com.bagin.plantcare.persistence.mapper;

import com.bagin.plantcare.domain.model.DeviceToken;
import com.bagin.plantcare.persistence.tablemodel.DeviceTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class DeviceTokenMapper {

  public DeviceToken toDomain(DeviceTokenEntity entity) {
    return new DeviceToken(
        entity.getId(),
        entity.getUserId(),
        entity.getEndpoint(),
        entity.getP256dh(),
        entity.getAuth()
    );
  }

  public DeviceTokenEntity toEntity(DeviceToken deviceToken) {
    DeviceTokenEntity entity = new DeviceTokenEntity();
    entity.setId(deviceToken.getId());
    entity.setUserId(deviceToken.getUserId());
    entity.setEndpoint(deviceToken.getEndpoint());
    entity.setP256dh(deviceToken.getP256dh());
    entity.setAuth(deviceToken.getAuth());
    return entity;
  }
}