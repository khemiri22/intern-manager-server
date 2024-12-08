package com.khemiri.InternManager.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "stages")
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date dateDebut;
    private Date dateFin;
    @Column(length = 50)
    private String tuteur;
    @Column(length = 50)
    private String sujet;
    private String description;
    @Column(length = 100)
    private String etablissement;
    @ManyToOne
    @JoinColumn(name = "intern_id")
    private Stagiaire stagiaire;
}
