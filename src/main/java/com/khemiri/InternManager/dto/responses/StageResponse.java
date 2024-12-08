package com.khemiri.InternManager.dto.responses;

import lombok.*;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StageResponse {
    private Long id;
    private String dateDebut;
    private String dateFin;
    private String tuteur;
    private String sujet;
    private String description;
    private String etablissement;
}
