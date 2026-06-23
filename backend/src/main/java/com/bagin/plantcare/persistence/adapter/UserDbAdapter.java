package com.bagin.plantcare.persistence.adapter;

import com.bagin.plantcare.domain.model.User;
import com.bagin.plantcare.persistence.mapper.UserMapper;
import com.bagin.plantcare.persistence.repository.UserJpaRepository;
import com.bagin.plantcare.persistence.tablemodel.UserEntity;
import com.bagin.plantcare.ports.out.UserPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserDbAdapter implements UserPort {

  private final UserJpaRepository jpaRepository;
  private final UserMapper mapper;

  public UserDbAdapter(UserJpaRepository jpaRepository, UserMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public User save(User user) {
    UserEntity entity = mapper.toEntity(user);
    UserEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<User> findById(Long id) {
    return jpaRepository.findById(id)
        .map(mapper::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jpaRepository.findByEmail(email)
        .map(mapper::toDomain);
  }
}
