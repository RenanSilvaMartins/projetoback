package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long>{

    Tecnico findAllById(long id);
}
