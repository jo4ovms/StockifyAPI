package com.jo4ovms.StockifyAPI.repository;

import com.jo4ovms.StockifyAPI.model.ERole;
import com.jo4ovms.StockifyAPI.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);


}
