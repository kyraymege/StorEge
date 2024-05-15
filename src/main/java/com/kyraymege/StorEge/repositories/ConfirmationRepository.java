package com.kyraymege.StorEge.repositories;

import com.kyraymege.StorEge.entity.concretes.Confirmation;
import com.kyraymege.StorEge.entity.concretes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
    Optional<Confirmation> findByKey(String key);
    Optional<Confirmation> findByUser(User user);
}
