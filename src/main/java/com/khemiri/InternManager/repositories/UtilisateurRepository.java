package com.khemiri.InternManager.repositories;

import com.khemiri.InternManager.entities.Utilisateur;
import com.khemiri.InternManager.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByStatus(Status status);
}
