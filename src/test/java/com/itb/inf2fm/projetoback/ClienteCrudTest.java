// Testes de CRUD para Cliente
// Estes testes garantem que os endpoints e repositórios funcionam corretamente
// Integração com Frontend: simule operações que serão realizadas pelo Flutter/ReactJS
// Exemplo: cadastro, busca, atualização e remoção de clientes via API REST
// Para Flutter, crie testes equivalentes usando pacotes de teste e integração
// Para ReactJS + Vite, utilize bibliotecas de teste como Jest para simular chamadas à API
package com.itb.inf2fm.projetoback;

import com.itb.inf2fm.projetoback.model.Cliente;
import com.itb.inf2fm.projetoback.repository.ClienteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClienteCrudTest {
    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @Order(1)
    void testCreateCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Teste CRUD");
        cliente.setCpf("12345678901");
        cliente.setEmail("crud@teste.com");
        clienteRepository.save(cliente);
        Assertions.assertNotNull(cliente.getId());
    }

    @Test
    @Order(2)
    void testReadCliente() {
        Optional<Cliente> cliente = clienteRepository.findByEmail("crud@teste.com");
        Assertions.assertTrue(cliente.isPresent());
        Assertions.assertEquals("Teste CRUD", cliente.get().getNome());
    }

    @Test
    @Order(3)
    void testUpdateCliente() {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail("crud@teste.com");
        Assertions.assertTrue(clienteOpt.isPresent());
        Cliente cliente = clienteOpt.get();
        cliente.setNome("CRUD Atualizado");
        clienteRepository.save(cliente);
        Optional<Cliente> updated = clienteRepository.findByEmail("crud@teste.com");
        Assertions.assertEquals("CRUD Atualizado", updated.get().getNome());
    }

    @Test
    @Order(4)
    void testDeleteCliente() {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail("crud@teste.com");
        Assertions.assertTrue(clienteOpt.isPresent());
        clienteRepository.delete(clienteOpt.get());
        Optional<Cliente> deleted = clienteRepository.findByEmail("crud@teste.com");
        Assertions.assertFalse(deleted.isPresent());
    }
}
