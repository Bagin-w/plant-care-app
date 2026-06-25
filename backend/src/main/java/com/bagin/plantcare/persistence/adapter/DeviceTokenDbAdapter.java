package com.bagin.plantcare.persistence.adapter;

import com.bagin.plantcare.domain.model.DeviceToken;
import com.bagin.plantcare.persistence.mapper.DeviceTokenMapper;
import com.bagin.plantcare.persistence.repository.DeviceTokenJpaRepository;
import com.bagin.plantcare.ports.out.DeviceTokenPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceTokenDbAdapter implements DeviceTokenPort {

  private final DeviceTokenJpaRepository jpaRepository;
  private final DeviceTokenMapper mapper;

  public DeviceTokenDbAdapter(DeviceTokenJpaRepository jpaRepository, DeviceTokenMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public DeviceToken save(DeviceToken deviceToken) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(deviceToken)));
  }

  @Override
  public List<DeviceToken> findAllByUserId(Long userId) {
    return jpaRepository.findAllByUserId(userId).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void deleteByEndpoint(String endpoint) {
    jpaRepository.deleteByEndpoint(endpoint);
  }
}