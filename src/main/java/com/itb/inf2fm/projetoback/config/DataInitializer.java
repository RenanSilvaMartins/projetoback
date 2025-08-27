package com.itb.inf2fm.projetoback.config;

import com.itb.inf2fm.projetoback.service.EspecialidadeService;
import com.itb.inf2fm.projetoback.service.RegiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final EspecialidadeService especialidadeService;
    private final RegiaoService regiaoService;
    
    public DataInitializer(EspecialidadeService especialidadeService, RegiaoService regiaoService) {
        this.especialidadeService = especialidadeService;
        this.regiaoService = regiaoService;
    }

    @Override
    public void run(String... args) throws DataAccessException {
        try {
            especialidadeService.initializeDefaultEspecialidades();
            regiaoService.initializeDefaultRegioes();
            logger.info("Dados padrão inicializados com sucesso");
        } catch (DataAccessException e) {
            logger.error("Erro ao inicializar dados padrão", e);
            throw e;
        }
    }
}
