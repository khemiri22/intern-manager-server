package com.khemiri.InternManager.services.admin;

import com.khemiri.InternManager.entities.Admin;

import java.util.List;
import java.util.Optional;

public interface IServiceAdmin {
    public Admin save(Admin admin);
    public List<Admin> findAll();
    public Optional<Admin> findById(Long id);
    public Optional<Admin> findByEmail(String email);
    public void deleteById(Long id);
}
