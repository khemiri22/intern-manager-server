package com.khemiri.InternManager.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class StagiaireRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private MultipartFile image;
}
