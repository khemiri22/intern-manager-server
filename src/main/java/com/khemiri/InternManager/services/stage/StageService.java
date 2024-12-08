package com.khemiri.InternManager.services.stage;

import com.khemiri.InternManager.entities.Stage;
import com.khemiri.InternManager.entities.Stagiaire;
import com.khemiri.InternManager.repositories.StageRepository;
import com.khemiri.InternManager.utils.PdfGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StageService implements IStageService {
    private StageRepository stageRepository;
    private PdfGenerator pdfGenerator;
    @Override
    public Stage save(Stage stage) {
        return stageRepository.save(stage);
    }

    @Override
    public List<Stage> findAll() {
        return stageRepository.findAll();
    }

    @Override
    public Optional<Stage> findById(Long id) {
        return stageRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        stageRepository.deleteById(id);
    }

    @Override
    public ByteArrayInputStream generateCertif(Stagiaire stagiaire, Stage stage){
        return pdfGenerator.generateInternshipCertificate(stagiaire,stage);
    }
}
