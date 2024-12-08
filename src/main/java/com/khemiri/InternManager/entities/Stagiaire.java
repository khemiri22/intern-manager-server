package com.khemiri.InternManager.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "stagiaires")
@DiscriminatorValue("intern")
public class Stagiaire extends Utilisateur{
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
    @OneToMany(mappedBy = "stagiaire")
    private List<Stage> stages;
    private String imageDeStagiaire;
}
