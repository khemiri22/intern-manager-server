package com.khemiri.InternManager.services.stagiaire;

import com.khemiri.InternManager.entities.Stagiaire;
import com.khemiri.InternManager.enums.Status;
import com.khemiri.InternManager.repositories.StagiaireRepository;
import com.khemiri.InternManager.utils.PasswordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServiceStagiaire implements IServiceStagiaire {
    @Autowired
    private StagiaireRepository stagiaireRepository;
    @Value("${upload.path.intern}")
    private String internUploadPath;
    @Override
    public Stagiaire save(Stagiaire stagiaire) {
        stagiaire.setMotDePasse(PasswordManager.hashPassword(stagiaire.getMotDePasse()));
        stagiaire.setStatus(Status.OFFLINE);
        return stagiaireRepository.save(stagiaire);
    }
    @Override
    public Stagiaire update(Stagiaire stagiaire,Long id)
    {
        return stagiaireRepository.findById(id).map(
                stg -> {
                    if (stagiaire.getNom() != null && !stagiaire.getNom().isEmpty()) {
                        stg.setNom(stagiaire.getNom());
                    }
                    if (stagiaire.getPrenom() != null && !stagiaire.getPrenom().isEmpty()) {
                        stg.setPrenom(stagiaire.getPrenom());
                    }
                    if (stagiaire.getEmail() != null && !stagiaire.getEmail().isEmpty()) {
                        stg.setEmail(stagiaire.getEmail());
                    }
                    if (stagiaire.getMotDePasse() != null && !stagiaire.getMotDePasse().isEmpty()) {
                        stg.setMotDePasse(PasswordManager.hashPassword(stagiaire.getMotDePasse()));
                    }
                    if (stagiaire.getImageDeStagiaire() != null && !stagiaire.getImageDeStagiaire().isEmpty()) {
                        stg.setImageDeStagiaire(stagiaire.getImageDeStagiaire());
                    }
                    return stagiaireRepository.save(stg);
                }
        ).orElse(null);

    }
    @Override
    public List<Stagiaire> findAll() {
        return stagiaireRepository.findAll();
    }

    @Override
    public Optional<Stagiaire> findById(Long id) {
        return stagiaireRepository.findById(id);
    }
    public Optional<Stagiaire> findByEmail(String email) {
        return stagiaireRepository.findByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        stagiaireRepository.deleteById(id);
    }

    @Override
    public String saveImage(MultipartFile file) throws IOException{
        File uploadDir = new File(internUploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // Create directories if they do not exist
        }
        String randomName = UUID.randomUUID().toString();
        String newFileName = randomName  + "_intern.jpg";
        Path path = Paths.get(internUploadPath + newFileName);
        Files.write(path, file.getBytes());
        return "uploads/"+newFileName;
    }


}
