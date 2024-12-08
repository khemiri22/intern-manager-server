package com.khemiri.InternManager.services.stage;

import com.khemiri.InternManager.entities.Stage;
import com.khemiri.InternManager.entities.Stagiaire;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

public interface IStageService {
    public Stage save(Stage stage);
    public List<Stage> findAll();
    public Optional<Stage> findById(Long id);
    public void deleteById(Long id);

    public ByteArrayInputStream generateCertif(Stagiaire stagiaire, Stage stage);
}
