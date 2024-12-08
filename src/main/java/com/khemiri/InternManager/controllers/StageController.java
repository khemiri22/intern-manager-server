package com.khemiri.InternManager.controllers;

import com.khemiri.InternManager.dto.requests.StageRequest;
import com.khemiri.InternManager.dto.responses.StageResponse;
import com.khemiri.InternManager.dto.responses.TokenPayloadResponse;
import com.khemiri.InternManager.entities.Stage;
import com.khemiri.InternManager.entities.Stagiaire;
import com.khemiri.InternManager.services.stage.IStageService;
import com.khemiri.InternManager.services.stagiaire.IServiceStagiaire;
import com.khemiri.InternManager.utils.CookiesManager;
import com.khemiri.InternManager.utils.JwtManager;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


@RestController
@RequestMapping("/internship")
@AllArgsConstructor
public class StageController {
    private IStageService stageService;
    private IServiceStagiaire serviceStagiaire;
    private JwtManager jwtManager;

    @PostMapping("/affecterStage/{id_intern}")

    public ResponseEntity<Object> affecterStage(@PathVariable Long id_intern, @RequestBody StageRequest request, @CookieValue(name = "token", required = false)String cookieValue)
    {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stagiaire = serviceStagiaire.findById(id_intern).orElse(null);
        if(stagiaire == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");

        Stage stage = new Stage();
        stage.setDateDebut(request.getDateDebut());
        stage.setDateFin(request.getDateFin());
        stage.setSujet(request.getSujet());
        stage.setTuteur(request.getTuteur());
        stage.setDescription(request.getDescription());
        stage.setEtablissement(request.getEtablissement());
        stage.setStagiaire(stagiaire);
        Stage result = stageService.save(stage);
        return ResponseEntity.ok().body(new StageResponse(
                result.getId(), new SimpleDateFormat("yyyy-MM-dd").format(stage.getDateDebut()),new SimpleDateFormat("yyyy-MM-dd").format(stage.getDateFin()),stage.getTuteur(),stage.getSujet(),stage.getDescription(),stage.getEtablissement()
        ));
    }



    @GetMapping()

    public ResponseEntity<Object> getAllInternships(@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        List<StageResponse> stages = stageService.findAll().stream().map(
                stage -> new StageResponse(
                        stage.getId(),new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stage.getDateDebut()),new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stage.getDateFin()),stage.getTuteur(),stage.getSujet(),stage.getDescription(),stage.getEtablissement()
                )).toList();
        return ResponseEntity.ok().body(stages);
    }

    @PutMapping("/{id}")

    public ResponseEntity<Object> updateInernshipById(@PathVariable Long id,@RequestBody StageRequest request,@CookieValue(name = "token", required = false)String cookieValue)
    {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        Stage stg = stageService.findById(id).orElse(null);
        if (stg != null){
            stg.setSujet(request.getSujet());
            stg.setDescription(request.getDescription());
            stg.setTuteur(request.getTuteur());
            stg.setDateFin(request.getDateFin());
            stg.setDateDebut(request.getDateDebut());
            stg.setEtablissement(request.getEtablissement());
            return ResponseEntity.ok().body(
                    new StageResponse(
                           stg.getId(), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stg.getDateDebut()),new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stg.getDateFin()),stg.getTuteur(),stg.getSujet(),stg.getDescription(),stg.getEtablissement()
                    ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Internship not found with ID: " + id);
    }


    @DeleteMapping("/{id}")

    public ResponseEntity<Object> deleteInernship(@PathVariable Long id,@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        if(!tokenPayloadResponse.getRole().equals("admin"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        Stage stg = stageService.findById(id).orElse(null);
        if (stg != null)
        {
            stageService.deleteById(id);
            return ResponseEntity.ok().body(
                    new StageResponse(
                            stg.getId(),new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stg.getDateDebut()),new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stg.getDateFin()),stg.getTuteur(),stg.getSujet(),stg.getDescription(),stg.getEtablissement()
                    )
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Internship not found with ID: " + id);
    }



    @GetMapping("/generateCertif/{id}")

    public ResponseEntity<Object> generateCertif(@PathVariable Long id,@CookieValue(name = "token", required = false)String cookieValue){
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if(tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        if(!tokenPayloadResponse.getRole().equals("intern"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accès refusé !");
        Stagiaire stagiaire = serviceStagiaire.findById(Long.parseLong(tokenPayloadResponse.getId())).orElse(null);
        if(stagiaire == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stagiaire non trouvé !");
        Stage stage = stageService.findById(id).orElse(null);
        if(stage == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stage non trouvé !");
        ByteArrayInputStream bis = stageService.generateCertif(stagiaire,stage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename="+"attestation_stage.pdf");
        byte[] pdfBytes = bis.readAllBytes();
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/getAllStageOfIntern/{id_intern}")

    public ResponseEntity<Object> getAllStageOfIntern(@PathVariable Long id_intern,@CookieValue(name = "token", required = false)String cookieValue) {
        String token = CookiesManager.getCookie(cookieValue);
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        TokenPayloadResponse tokenPayloadResponse = jwtManager.validateJwtToken(token);
        if (tokenPayloadResponse == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied !");
        Stagiaire stagiaire = serviceStagiaire.findById(id_intern).orElse(null);
        if (stagiaire == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id_intern);
        List<StageResponse> stages = stagiaire.getStages().stream().map(
                stage -> new StageResponse(
                        stage.getId(),new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stage.getDateDebut()),new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stage.getDateFin()),stage.getTuteur(),stage.getSujet(),stage.getDescription(),stage.getEtablissement()
                )).toList();
        return ResponseEntity.ok().body(stages);
    }
}
