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
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Cliente> saveCliente(@RequestBody Cliente cliente){
        Cliente clienteSalvo = clienteService.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    /**
     * READ - Buscar cliente por ID
     * GET /cliente/{id}
     * 
     * FLUTTER: final response = await http.get(Uri.parse('$baseUrl/cliente/$id'));
     * REACT: const response = await axios.get(`/cliente/${id}`);
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteService.findById(id);
        return clienteOpt.map(ResponseEntity::ok)
                         .orElseThrow(() -> new com.itb.inf2fm.projetoback.exception.ResourceNotFoundException("Cliente", "id", id));
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
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente cliente){
        cliente.setId(id);
        Cliente clienteAtualizado = clienteService.update(cliente);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * DELETE - Deletar cliente
     * DELETE /cliente/{id}
     * 
     * FLUTTER: final response = await http.delete(Uri.parse('$baseUrl/cliente/$id'));
     * REACT: const response = await axios.delete(`/cliente/${id}`);
     * 
     * Retorna: 204 (sucesso) ou erro específico
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id){
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * PATCH - Inativar cliente
     * PATCH /cliente/{id}/inativar
     */
    @Operation(summary = "Inativar cliente", description = "Inativa um cliente sem removê-lo do sistema")
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Cliente> inativarCliente(@PathVariable Long id) {
        Cliente clienteInativado = clienteService.inativar(id);
        return ResponseEntity.ok(clienteInativado);
    }
}
