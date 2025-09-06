package com.itb.inf2fm.projetoback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Batch Service - Operações em lote para melhor performance
 * 
 * Otimizações:
 * - Inserções em lote
 * - Atualizações em massa
 * - Redução de round-trips ao banco
 */
@Service
public class BatchService {
    
    @Autowired
    private DataSource dataSource;
    
    @Transactional
    public void batchInsertUsuarios(List<Object[]> usuarios) throws SQLException {
        String sql = "INSERT INTO Usuario (nome, email, senha, nivelAcesso, statusUsuario, dataCadastro) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Object[] usuario : usuarios) {
                stmt.setString(1, (String) usuario[0]); // nome
                stmt.setString(2, (String) usuario[1]); // email
                stmt.setString(3, (String) usuario[2]); // senha
                stmt.setString(4, ((String) usuario[3]).toUpperCase(java.util.Locale.ROOT)); // nivelAcesso
                stmt.setString(5, ((String) usuario[4]).toUpperCase(java.util.Locale.ROOT)); // statusUsuario
                stmt.setObject(6, usuario[5]); // dataCadastro
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    private static final Set<String> ALLOWED_TABLES = Set.of("Usuario", "Cliente", "Tecnico");
    
    @Transactional
    public void batchUpdateStatus(List<Long> ids, String novoStatus, String tabela) throws SQLException {
        if (!ALLOWED_TABLES.contains(tabela)) {
            throw new IllegalArgumentException("Tabela não permitida: " + tabela);
        }
        
        String sql = String.format("UPDATE %s SET statusUsuario = ? WHERE id = ?", tabela);
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Long id : ids) {
                stmt.setString(1, novoStatus);
                stmt.setLong(2, id);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
}