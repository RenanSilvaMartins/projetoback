package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.Regiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegiaoRepository extends JpaRepository<Regiao, Long> {
    
    Optional<Regiao> findByNome(String nome);
    
    List<Regiao> findByStatusRegiao(String statusRegiao);
    
    List<Regiao> findByCidade(String cidade);
    
    boolean existsByNome(String nome);
    
    Optional<Regiao> findByNomeAndCidade(String nome, String cidade);
}
