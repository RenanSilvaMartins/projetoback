package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    
    // Basic CRUD operations are inherited from JpaRepository
    // Additional methods can be added here if needed
}