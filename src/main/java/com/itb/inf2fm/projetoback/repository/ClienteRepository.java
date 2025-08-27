// Repositório JPA para Cliente
// Use este repositório para operações CRUD e consultas customizadas
// Integração com Frontend: endpoints REST consomem métodos deste repositório
// Flutter/ReactJS: Utilize endpoints como /cliente, /cliente/{id}, /cliente?email= para buscar dados
// Métodos findByEmail, findByCpf e findByUsuarioEmail facilitam autenticação e busca por dados únicos
// Para integração com Vite/ReactJS, utilize fetch/Axios para consumir endpoints REST
// Para integração com Flutter, utilize pacotes como http/dio para consumir endpoints REST
package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByUsuarioEmail(String email);
    
    List<Cliente> findByStatusCliente(String statusCliente);
    
    boolean existsByCpf(String cpf);
}
