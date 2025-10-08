package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long>{

    Optional<Tecnico> findByCpfCnpj(String cpfCnpj);

    Optional<Tecnico> findByUsuarioEmail(String email);
    
    List<Tecnico> findByStatusTecnico(String statusTecnico);
    
    boolean existsByCpfCnpj(String cpfCnpj);
    
    List<Tecnico> findByUsuarioNomeContainingIgnoreCase(String nome);
    
    @Query("SELECT DISTINCT t.especialidade FROM Tecnico t WHERE t.statusTecnico = 'ATIVO'")
    List<String> findDistinctEspecialidades();
}
