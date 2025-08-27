package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    
    Optional<Especialidade> findByNome(String nome);
    
    List<Especialidade> findByStatusEspecialidade(String statusEspecialidade);
    
    boolean existsByNome(String nome);
}
