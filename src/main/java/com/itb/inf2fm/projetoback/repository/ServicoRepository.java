package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    
    List<Servico> findByNomeContainingIgnoreCase(String nome);
    
    List<Servico> findByTipoIgnoreCase(String tipo);
    
    @Query("SELECT s FROM Servico s WHERE s.nome LIKE %:termo% OR s.tipo LIKE %:termo%")
    List<Servico> buscarPorTermo(@Param("termo") String termo);
}