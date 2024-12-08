package com.khemiri.InternManager.repositories;

import com.khemiri.InternManager.entities.ChatMessage;
import com.khemiri.InternManager.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.expediteur.id = :userId OR cm.destinataire.id = :userId ORDER BY cm.dateEnvoi ASC")
    List<ChatMessage> findByUserId(@Param("userId") Long userId);
}
