package com.khemiri.InternManager.dto.responses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class InternResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private UserResponse admin;
}
