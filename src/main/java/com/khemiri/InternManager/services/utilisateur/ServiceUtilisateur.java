package com.khemiri.InternManager.services.utilisateur;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollerFlux;
import com.khemiri.InternManager.entities.Utilisateur;
import com.khemiri.InternManager.enums.Status;
import com.khemiri.InternManager.repositories.UtilisateurRepository;
import com.khemiri.InternManager.utils.PasswordManager;
import com.khemiri.InternManager.utils.RandomString;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ServiceUtilisateur implements IServiceUtilisateur {

    private UtilisateurRepository utilisateurRepository;
    private final JavaMailSender mailSender;
    private Environment env;
    private EmailClient emailClient ;
    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        utilisateur.setStatus(Status.ONLINE);
        return utilisateurRepository.save(utilisateur);
    }
    @Override
    public void disconnect(Utilisateur utilisateur){
        Utilisateur storedUser = utilisateurRepository.findByEmail(utilisateur.getEmail()).orElse(null);
        if(storedUser != null)
        {
            storedUser.setStatus(Status.OFFLINE);
            utilisateurRepository.save(storedUser);
        }
    }

    @Override
    public void connect(Utilisateur utilisateur){
        Utilisateur storedUser = utilisateurRepository.findByEmail(utilisateur.getEmail()).orElse(null);
        if(storedUser != null)
        {
            storedUser.setStatus(Status.ONLINE);
            utilisateurRepository.save(storedUser);
        }
    }

    @Override
    public List<Utilisateur> findConnectedUsers(){
        return utilisateurRepository.findByStatus(Status.ONLINE);
    }

    @Override
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        utilisateurRepository.deleteById(id);
    }


    @Override
    public Utilisateur sendMailToUser(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email).orElse(null);
        if(user == null)
            return null;
        String password = RandomString.generatePassword(10,user.getId().toString());
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("RESET PASSWORD");
            helper.setText("<h2>Your new password is : <h1>"+password+"</h1></h2>", true); // Set the HTML content to true
            helper.setFrom("SIP INTERN-MANAGER" + " <" + env.getProperty("spring.mail.username") + ">");
            mailSender.send(message);
        }catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
        user.setMotDePasse(PasswordManager.hashPassword(password));
        return utilisateurRepository.save(user);
    }

    @Override
    public Utilisateur sendMailToUserWithAzure(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email).orElse(null);
        if(user == null)
            return null;
        String password = RandomString.generatePassword(10,user.getId().toString());
        EmailMessage emailMessage = new EmailMessage()
                .setSenderAddress("<"+env.getProperty("azure.comminication.email.subdomain.fromMail")+">")
                .setToRecipients("<"+email+">")
                .setSubject("RESET PASSWORD")
                .setBodyHtml("<h2>Votre nouveau mot de passe est : <h1>"+password+"</h1></h2>");  // Set HTML content here
        EmailSendResult result = emailClient.beginSend(emailMessage).getFinalResult();
        if(result.getStatus() == EmailSendStatus.SUCCEEDED) {
            user.setMotDePasse(PasswordManager.hashPassword(password));
            return utilisateurRepository.save(user);

        }
        return null;

    }




    }

