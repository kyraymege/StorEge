package com.kyraymege.StorEge.repositories;

import com.kyraymege.StorEge.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> getCredentialByUserId(Long userId);
}
