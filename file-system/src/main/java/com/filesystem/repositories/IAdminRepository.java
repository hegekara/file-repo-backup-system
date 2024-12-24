package com.filesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filesystem.entities.user.Admin;

@Repository
public interface IAdminRepository extends JpaRepository<Admin, Long>{

    Admin findByUsername(String username);

}
