package com.khemiri.InternManager.services.admin;


import com.khemiri.InternManager.entities.Admin;
import com.khemiri.InternManager.enums.Status;
import com.khemiri.InternManager.repositories.AdminRepository;
import com.khemiri.InternManager.utils.PasswordManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ServiceAdmin implements IServiceAdmin {

    private AdminRepository adminRepository;
    @Override
    public Admin save(Admin admin) {
        admin.setMotDePasse(PasswordManager.hashPassword(admin.getMotDePasse()));
        admin.setStatus(Status.OFFLINE);
        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        adminRepository.deleteById(id);
    }
}
