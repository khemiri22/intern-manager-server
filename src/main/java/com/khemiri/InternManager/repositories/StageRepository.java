package com.khemiri.InternManager.repositories;

import com.khemiri.InternManager.entities.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface StageRepository extends JpaRepository<Stage, Long> {

}
