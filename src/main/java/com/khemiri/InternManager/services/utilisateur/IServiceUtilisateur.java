package com.khemiri.InternManager.services.utilisateur;

import com.khemiri.InternManager.entities.Utilisateur;
import com.khemiri.InternManager.enums.Status;

import java.util.List;
import java.util.Optional;

public interface IServiceUtilisateur {
    public Utilisateur save(Utilisateur utilisateur);

    public void connect(Utilisateur utilisateur);
    public void disconnect(Utilisateur utilisateur);
    public Utilisateur sendMailToUser(String email);
    public Utilisateur sendMailToUserWithAzure(String email);
    public List<Utilisateur> findAll();
    public Optional<Utilisateur> findById(Long id);
    public Optional<Utilisateur> findByEmail(String email);
    public List<Utilisateur> findConnectedUsers();
    public void deleteById(Long id);
}
