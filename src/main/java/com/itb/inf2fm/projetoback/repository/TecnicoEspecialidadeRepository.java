package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.TecnicoEspecialidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TecnicoEspecialidadeRepository extends JpaRepository<TecnicoEspecialidade, Long> {
    
    List<TecnicoEspecialidade> findByTecnicoId(Long tecnicoId);
    
    List<TecnicoEspecialidade> findByEspecialidadeId(Long especialidadeId);
    
    List<TecnicoEspecialidade> findByStatusTecnicoEspecialidade(String statusTecnicoEspecialidade);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TecnicoEspecialidade te WHERE te.tecnico.id = :tecnicoId AND te.especialidade.id = :especialidadeId")
    void deleteByTecnicoIdAndEspecialidadeId(@Param("tecnicoId") Long tecnicoId, @Param("especialidadeId") Long especialidadeId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TecnicoEspecialidade te WHERE te.tecnico.id = :tecnicoId")
    void deleteByTecnicoId(@Param("tecnicoId") Long tecnicoId);
}
