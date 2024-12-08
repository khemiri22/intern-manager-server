package com.khemiri.InternManager.entities;

import com.khemiri.InternManager.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "utilisateurs")
@DiscriminatorColumn(name="role")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String nom;
    @Column(length = 50)
    private String prenom;
    @Column(length = 50)
    private String email;
    private String motDePasse;
    @Column(insertable = false, updatable = false,length = 10)
    private String role;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "expediteur")
    private List<ChatMessage> messagesEnvoye;
    @OneToMany(mappedBy = "destinataire")
    private List<ChatMessage> messagesRecuses;

}
