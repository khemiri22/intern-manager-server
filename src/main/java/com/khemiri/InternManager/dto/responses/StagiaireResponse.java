package com.khemiri.InternManager.dto.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StagiaireResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String image;
}
