package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.TecnicoRegiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TecnicoRegiaoRepository extends JpaRepository<TecnicoRegiao, Long> {
    
    List<TecnicoRegiao> findByTecnicoId(Long tecnicoId);
    
    List<TecnicoRegiao> findByRegiaoId(Long regiaoId);
    
    List<TecnicoRegiao> findByStatusTecnicoRegiao(String statusTecnicoRegiao);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TecnicoRegiao tr WHERE tr.tecnico.id = :tecnicoId AND tr.regiao.id = :regiaoId")
    void deleteByTecnicoIdAndRegiaoId(@Param("tecnicoId") Long tecnicoId, @Param("regiaoId") Long regiaoId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TecnicoRegiao tr WHERE tr.tecnico.id = :tecnicoId")
    void deleteByTecnicoId(@Param("tecnicoId") Long tecnicoId);
}
