// Controller REST para Cliente
// Expõe endpoints para operações CRUD e consultas customizadas
// Integração com Frontend: endpoints como /cliente, /cliente/{id}, /cliente?email= podem ser consumidos por Flutter/ReactJS
// Para Flutter, utilize pacotes http/dio para consumir endpoints REST
// Para ReactJS + Vite, utilize fetch/Axios para consumir endpoints REST
package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Cliente;
import com.itb.inf2fm.projetoback.service.ClienteService;
import com.itb.inf2fm.projetoback.service.PasswordEncryptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * CLIENTE CONTROLLER - API REST para gerenciamento de clientes
 * Base URL: http://localhost:8082/cliente
 * 
 * FLUTTER: Use http package ou dio
 * REACT: Use axios ou fetch
 */
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService, PasswordEncryptService passwordEncryptService) {
        this.clienteService = clienteService;
    }

    /**
     * CREATE - Criar novo cliente
     * POST /cliente
     * 
     * FLUTTER:
     * final response = await http.post(
     *   Uri.parse('$baseUrl/cliente'),
     *   headers: {'Content-Type': 'application/json'},
     *   body: jsonEncode({
     *     'cpf': '11144477735',
     *     'dataNascimento': '1990-01-01',
     *     'usuario': {
     *       'nome': 'João Silva',
     *       'email': 'joao@email.com',
     *       'senha': '123456'
     *     },
     *     'statusCliente': 'ATIVO'
     *   })
     * );
     * 
     * REACT:
     * const response = await axios.post('/cliente', {
     *   cpf: '11144477735',
     *   dataNascimento: '1990-01-01',
     *   usuario: {
     *     nome: 'João Silva',
     *     email: 'joao@email.com',
     *     senha: '123456'
     *   },
     *   statusCliente: 'ATIVO'
     * });
     */
    @PostMapping
    public ResponseEntity<Object> saveCliente(@RequestBody Cliente cliente){
        try {
            if (cliente == null) {
                return ResponseEntity.badRequest().body("Dados do cliente são obrigatórios");
            }
            
            Cliente clienteSalvo = clienteService.save(cliente);
            
            if (clienteSalvo.isValid()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
            } else {
                return ResponseEntity.badRequest().body(clienteSalvo);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    /**
     * READ - Buscar cliente por ID
     * GET /cliente/cliente/{id}
     * 
     * FLUTTER: final response = await http.get(Uri.parse('$baseUrl/cliente/cliente/$id'));
     * REACT: const response = await axios.get(`/cliente/cliente/${id}`);
     */
    @GetMapping("/cliente/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteService.findById(id);
        if (clienteOpt.isPresent()) {
            return ResponseEntity.ok(clienteOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * READ - Listar todos os clientes
     * GET /cliente
     * 
     * FLUTTER: final response = await http.get(Uri.parse('$baseUrl/cliente'));
     * REACT: const response = await axios.get('/cliente');
     * 
     * Retorna: List<Cliente>
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.findAll());
    }
    
    @Operation(summary = "Buscar clientes por nome", description = "Retorna uma lista de clientes que contenham o nome fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes encontrados")
    })
    @GetMapping("/buscar/{nome}")
    public ResponseEntity<List<Cliente>> buscarClientesPorNome(
            @Parameter(description = "Nome ou parte do nome do cliente") 
            @PathVariable String nome) {
        List<Cliente> clientes = clienteService.findByNome(nome);
        return ResponseEntity.ok(clientes);
    }

    /**
     * UPDATE - Atualizar cliente
     * PUT /cliente/{id}
     * 
     * FLUTTER: 
     * final response = await http.put(
     *   Uri.parse('$baseUrl/cliente/$id'),
     *   headers: {'Content-Type': 'application/json'},
     *   body: jsonEncode(clienteData)
     * );
     * 
     * REACT: const response = await axios.put(`/cliente/${id}`, clienteData);
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCliente(@PathVariable Long id, @RequestBody Cliente cliente){
        ResponseEntity<Object> validationError = validateId(id);
        if (validationError != null) {
            return validationError;
        }
        if (cliente == null) {
            return ResponseEntity.badRequest().body("Dados do cliente são obrigatórios");
        }
        cliente.setId(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteService.update(cliente));
    }

    /**
     * DELETE - Deletar cliente
     * DELETE /cliente/{id}
     * 
     * FLUTTER: final response = await http.delete(Uri.parse('$baseUrl/cliente/$id'));
     * REACT: const response = await axios.delete(`/cliente/${id}`);
     * 
     * Retorna: 200 (sucesso) ou 404 (não encontrado)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCliente(@PathVariable Long id){
        ResponseEntity<Object> validationError = validateId(id);
        if (validationError != null) {
            return validationError;
        }
        boolean deleted = clienteService.delete(id);
        if (deleted) {
            return ResponseEntity.ok("Cliente deletado com sucesso");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    private ResponseEntity<Object> validateId(Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID inválido");
        }
        return null;
    }
}
