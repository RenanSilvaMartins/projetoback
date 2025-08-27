package com.itb.inf2fm.projetoback.service;
import com.itb.inf2fm.projetoback.model.Cliente;
import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.repository.ClienteRepository;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class PasswordEncryptService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordEncryptService.class);
    
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;

    public PasswordEncryptService(ClienteRepository clienteRepository, TecnicoRepository tecnicoRepository) {
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    public String encryptPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(password, hashedPassword);
    }

    public void encryptExistingPasswords() {
        logger.info("Iniciando criptografia de senhas existentes");
        
        encryptClientePasswords();
        encryptTecnicoPasswords();
        
        logger.info("Criptografia de senhas existentes concluída");
    }
    
    private void encryptClientePasswords() {
        List<Cliente> clientesParaAtualizar = new ArrayList<>();
        List<Cliente> clientes = clienteRepository.findAll();
        
        for (Cliente cliente : clientes) {
            if (needsPasswordEncryption(cliente.getUsuario())) {
                cliente.getUsuario().setSenha(encryptPassword(cliente.getUsuario().getSenha()));
                clientesParaAtualizar.add(cliente);
            }
        }
        
        if (!clientesParaAtualizar.isEmpty()) {
            clienteRepository.saveAll(clientesParaAtualizar);
            logger.info("Criptografadas {} senhas de clientes", clientesParaAtualizar.size());
        }
    }
    
    private void encryptTecnicoPasswords() {
        List<Tecnico> tecnicosParaAtualizar = new ArrayList<>();
        List<Tecnico> tecnicos = tecnicoRepository.findAll();
        
        for (Tecnico tecnico : tecnicos) {
            if (needsPasswordEncryption(tecnico.getUsuario())) {
                tecnico.getUsuario().setSenha(encryptPassword(tecnico.getUsuario().getSenha()));
                tecnicosParaAtualizar.add(tecnico);
            }
        }
        
        if (!tecnicosParaAtualizar.isEmpty()) {
            tecnicoRepository.saveAll(tecnicosParaAtualizar);
            logger.info("Criptografadas {} senhas de técnicos", tecnicosParaAtualizar.size());
        }
    }
    
    private boolean needsPasswordEncryption(Usuario usuario) {
        return usuario != null && 
               usuario.getSenha() != null && 
               !usuario.getSenha().startsWith("$2a$");
    }
}
