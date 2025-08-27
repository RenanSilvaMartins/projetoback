package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncryptService passwordEncryptService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setSenha("123456");
        usuario.setStatusUsuario("ATIVO");
        usuario.setNivelAcesso("USER");
    }

    @Test
    void shouldSaveUsuarioSuccessfully() {
        // Given
        when(passwordEncryptService.encryptPassword(anyString())).thenReturn("encrypted_password");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario savedUsuario = usuarioService.save(usuario);

        // Then
        assertNotNull(savedUsuario);
        assertEquals("João Silva", savedUsuario.getNome());
        assertEquals("joao@test.com", savedUsuario.getEmail());
        verify(passwordEncryptService).encryptPassword("123456");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void shouldThrowExceptionWhenUsuarioIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.save(null)
        );
        
        assertEquals("Usuário não pode ser nulo", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNomeIsEmpty() {
        // Given
        usuario.setNome("");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.save(usuario)
        );
        
        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        // Given
        usuario.setEmail("");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.save(usuario)
        );
        
        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    void shouldFindUsuarioById() {
        // Given
        when(cacheService.get(anyString(), eq(Usuario.class))).thenReturn(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        Usuario foundUsuario = usuarioService.findById(1L);

        // Then
        assertNotNull(foundUsuario);
        assertEquals(1L, foundUsuario.getId());
        assertEquals("João Silva", foundUsuario.getNome());
    }

    @Test
    void shouldReturnNullWhenUsuarioNotFound() {
        // Given
        when(cacheService.get(anyString(), eq(Usuario.class))).thenReturn(null);
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Usuario foundUsuario = usuarioService.findById(999L);

        // Then
        assertNull(foundUsuario);
    }

    @Test
    void shouldAuthenticateUsuarioSuccessfully() {
        // Given
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncryptService.checkPassword("123456", usuario.getSenha())).thenReturn(true);

        // When
        Usuario authenticatedUsuario = usuarioService.authenticate("joao@test.com", "123456");

        // Then
        assertNotNull(authenticatedUsuario);
        assertTrue(authenticatedUsuario.isValid());
        assertEquals("joao@test.com", authenticatedUsuario.getEmail());
    }

    @Test
    void shouldFailAuthenticationWithWrongPassword() {
        // Given
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncryptService.checkPassword("wrong_password", usuario.getSenha())).thenReturn(false);

        // When
        Usuario authenticatedUsuario = usuarioService.authenticate("joao@test.com", "wrong_password");

        // Then
        assertNotNull(authenticatedUsuario);
        assertFalse(authenticatedUsuario.isValid());
        assertEquals("Email ou senha incorretos!", authenticatedUsuario.getMensagemErro());
    }

    @Test
    void shouldFailAuthenticationWithNonExistentEmail() {
        // Given
        when(usuarioRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When
        Usuario authenticatedUsuario = usuarioService.authenticate("nonexistent@test.com", "123456");

        // Then
        assertNotNull(authenticatedUsuario);
        assertFalse(authenticatedUsuario.isValid());
        assertEquals("Email ou senha incorretos!", authenticatedUsuario.getMensagemErro());
    }

    @Test
    void shouldFailAuthenticationWithInactiveUser() {
        // Given
        usuario.setStatusUsuario("INATIVO");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncryptService.checkPassword("123456", usuario.getSenha())).thenReturn(true);

        // When
        Usuario authenticatedUsuario = usuarioService.authenticate("joao@test.com", "123456");

        // Then
        assertNotNull(authenticatedUsuario);
        assertFalse(authenticatedUsuario.isValid());
        assertEquals("Usuário inativo!", authenticatedUsuario.getMensagemErro());
    }
}
