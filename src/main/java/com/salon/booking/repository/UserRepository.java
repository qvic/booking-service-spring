package com.salon.booking.repository;

import com.salon.booking.entity.RoleEntity;
import com.salon.booking.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Integer> {

    Page<UserEntity> findAllByRole(RoleEntity role, Pageable properties);

    Optional<UserEntity> findByEmail(String email);
}
