package com.khemiri.InternManager.services.stagiaire;

import com.khemiri.InternManager.entities.Stage;
import com.khemiri.InternManager.entities.Stagiaire;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IServiceStagiaire {
    public Stagiaire save(Stagiaire stagiaire);
    public List<Stagiaire> findAll();
    public Optional<Stagiaire> findById(Long id);
    public Optional<Stagiaire> findByEmail(String email);
    public Stagiaire update(Stagiaire stagiaire,Long id);
    public void deleteById(Long id);
    String saveImage(MultipartFile file) throws IOException;
}
