package com.itb.inf2fm.projetoback.controller;

import com.itb.inf2fm.projetoback.config.SecurityTestConfig;
import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.service.UsuarioService;
import com.itb.inf2fm.projetoback.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(SecurityTestConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
        usuario.setSenha("senha123");
        usuario.setStatusUsuario("ATIVO");
        usuario.setNivelAcesso("USER");
    }

    @Test
    void shouldCreateUsuarioSuccessfully() throws Exception {
        // Given - criamos um objeto de usuário que será retornado pelo mock
        Usuario responseUsuario = new Usuario();
        responseUsuario.setId(1L);
        responseUsuario.setNome("João Silva");
        responseUsuario.setEmail("joao@test.com");
        responseUsuario.setStatusUsuario("ATIVO");
        responseUsuario.setNivelAcesso("USER");
        
        when(usuarioService.save(any(Usuario.class))).thenReturn(responseUsuario);

        // When & Then - enviamos um JSON com senha que o controller receberá
        String usuarioJson = "{\"nome\":\"João Silva\",\"email\":\"joao@test.com\",\"senha\":\"senha123\",\"statusUsuario\":\"ATIVO\",\"nivelAcesso\":\"USER\"}";
        
        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@test.com"));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        Usuario invalidUsuario = new Usuario();
        invalidUsuario.setNome("");

        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUsuario)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllUsuarios() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioService.findAll()).thenReturn(usuarios);

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    void shouldGetUsuarioById() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(usuario);

        mockMvc.perform(get("/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void shouldReturnNotFoundWhenUsuarioDoesNotExist() throws Exception {
        when(usuarioService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/usuario/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUsuario() throws Exception {
        // Given - criamos um objeto de resposta que será retornado pelo mock
        Usuario responseUsuario = new Usuario();
        responseUsuario.setId(1L);
        responseUsuario.setNome("João Silva Updated");
        responseUsuario.setEmail("joao.updated@test.com");
        responseUsuario.setStatusUsuario("ATIVO");
        responseUsuario.setNivelAcesso("USER");
        
        when(usuarioService.save(any(Usuario.class))).thenReturn(responseUsuario);

        // When & Then - enviamos um JSON com senha que o controller receberá
        String usuarioJson = "{\"nome\":\"João Silva Updated\",\"email\":\"joao.updated@test.com\",\"senha\":\"senha123\",\"statusUsuario\":\"ATIVO\",\"nivelAcesso\":\"USER\"}";

        mockMvc.perform(put("/usuario/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Updated"));
    }

    @Test
    void shouldDeleteUsuario() throws Exception {
        mockMvc.perform(delete("/usuario/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldAuthenticateUsuarioSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest("joao@test.com", "123456");
        
        usuario.setIsValid(true);
        when(usuarioService.authenticate(anyString(), anyString())).thenReturn(usuario);

        mockMvc.perform(post("/usuario/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@test.com"));
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthenticationFails() throws Exception {
        LoginRequest loginRequest = new LoginRequest("joao@test.com", "wrong_password");
        
        Usuario invalidUser = new Usuario();
        invalidUser.setIsValid(false);
        when(usuarioService.authenticate(anyString(), anyString())).thenReturn(invalidUser);

        mockMvc.perform(post("/usuario/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
