package com.khemiri.InternManager.entities;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "admins")
@DiscriminatorValue("admin")
public class Admin extends Utilisateur{
    @OneToMany(mappedBy = "admin")
    private List<Stagiaire> stagiaires;

}
