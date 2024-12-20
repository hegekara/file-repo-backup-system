package com.filesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filesystem.entities.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long>{

    User findByUsername(String username);
}
