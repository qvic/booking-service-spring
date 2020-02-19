package com.salon.booking.repository;

import com.salon.booking.entity.RoleEntity;
import com.salon.booking.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Integer> {

    List<UserEntity> findAllByRole(RoleEntity role);

    Optional<UserEntity> findByEmail(String email);
}
