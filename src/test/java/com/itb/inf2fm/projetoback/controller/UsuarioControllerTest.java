package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("deprecation")
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setStatusUsuario("ATIVO");
        usuario.setNivelAcesso("USER");
    }

    @Test
    void shouldCreateUsuarioSuccessfully() throws Exception {
        // Given
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        // When & Then
        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@test.com"));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        // Given
        Usuario invalidUsuario = new Usuario();
        invalidUsuario.setNome(""); // Nome vazio deve falhar na validação
        invalidUsuario.setEmail("invalid-email"); // Email inválido

        // When & Then
        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUsuario)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllUsuarios() throws Exception {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioService.findAll()).thenReturn(usuarios);

        // When & Then
        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    void shouldGetUsuarioById() throws Exception {
        // Given
        when(usuarioService.findById(1L)).thenReturn(usuario);

        // When & Then
        mockMvc.perform(get("/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void shouldReturnNotFoundWhenUsuarioDoesNotExist() throws Exception {
        // Given
        when(usuarioService.findById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/usuario/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUsuario() throws Exception {
        // Given
        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setId(1L);
        updatedUsuario.setNome("João Silva Updated");
        updatedUsuario.setEmail("joao.updated@test.com");
        
        when(usuarioService.save(any(Usuario.class))).thenReturn(updatedUsuario);

        // When & Then
        mockMvc.perform(put("/usuario/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Updated"));
    }

    @Test
    void shouldDeleteUsuario() throws Exception {
        // When & Then
        mockMvc.perform(delete("/usuario/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldAuthenticateUsuarioSuccessfully() throws Exception {
        // Given
        Usuario loginRequest = new Usuario();
        loginRequest.setEmail("joao@test.com");
        loginRequest.setSenha("123456");
        
        usuario.setIsValid(true);
        when(usuarioService.authenticate(anyString(), anyString())).thenReturn(usuario);

        // When & Then
        mockMvc.perform(post("/usuario/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@test.com"));
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthenticationFails() throws Exception {
        // Given
        Usuario loginRequest = new Usuario();
        loginRequest.setEmail("joao@test.com");
        loginRequest.setSenha("wrong_password");
        
        Usuario invalidUser = new Usuario();
        invalidUser.setIsValid(false);
        when(usuarioService.authenticate(anyString(), anyString())).thenReturn(invalidUser);

        // When & Then
        mockMvc.perform(post("/usuario/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
