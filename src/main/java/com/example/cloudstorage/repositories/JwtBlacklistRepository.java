package com.example.cloudstorage.repositories;

import com.example.cloudstorage.entities.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, String> {
    boolean existsByJwt(String jwt);
}
