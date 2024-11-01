package com.itb.inf2fm.projetoback.control;

import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.service.TecnicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins="*", maxAge = 3600, allowCredentials = "false")
@RequestMapping("/tecnico")
public class TecnicoController {


        final TecnicoService tecnicoService;


        public TecnicoController(TecnicoService _tecnicoService) {
            this.tecnicoService = _tecnicoService;
        }

        // ROTA POST
        @PostMapping
        public ResponseEntity<Object> saveTecnico(@RequestBody Tecnico tecnico){
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(tecnicoService.save(tecnico));
        }

        // ROTA GET
        @GetMapping
        public ResponseEntity<List<Tecnico>> getAllTecnicos(){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(tecnicoService.findAll());
        }

        @PutMapping("/{id}")
        public ResponseEntity<Object> updateTecnico( @PathVariable Long id, @RequestBody Tecnico tecnico){
            tecnico.setId(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(tecnicoService.update(tecnico));
        }

        @DeleteMapping
        public ResponseEntity<Object> deleteTecnico(@RequestBody Tecnico tecnico){
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(tecnicoService.delete(tecnico));
        }
}
