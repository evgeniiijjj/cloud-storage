package com.example.cloudstorage.repositories;

import com.example.cloudstorage.entities.AuthorizationData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorizationDataRepository extends JpaRepository<AuthorizationData, Long> {

    Boolean existsByLogin(String login);
    Optional<AuthorizationData> findByLogin(String login);
}
