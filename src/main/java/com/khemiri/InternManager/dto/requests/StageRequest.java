package com.khemiri.InternManager.dto.requests;

import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StageRequest {

    private Date dateDebut;
    private Date dateFin;
    private String tuteur;
    private String sujet;
    private String description;
    private String etablissement;
}
