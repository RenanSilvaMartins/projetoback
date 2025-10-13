package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.dto.AgendamentoRequest;
import com.itb.inf2fm.projetoback.model.Agendamento;
import com.itb.inf2fm.projetoback.model.Tecnico;
import com.itb.inf2fm.projetoback.model.Usuario;
import com.itb.inf2fm.projetoback.repository.AgendamentoRepository;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import com.itb.inf2fm.projetoback.repository.UsuarioRepository;
import com.itb.inf2fm.projetoback.repository.ServicoRepository;
import com.itb.inf2fm.projetoback.repository.ClienteRepository;
import com.itb.inf2fm.projetoback.model.Servico;
import com.itb.inf2fm.projetoback.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ServicoRepository servicoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;

    public List<Agendamento> findAll() {
        return agendamentoRepository.findAll();
    }

    public Optional<Agendamento> findById(Long id) {
        return agendamentoRepository.findById(id);
    }

    public Agendamento save(AgendamentoRequest request) {
        if (request.getTecnicoId() == null) {
            throw new IllegalArgumentException("tecnicoId é obrigatório");
        }
        if (request.getUsuarioId() == null) {
            throw new IllegalArgumentException("usuarioId é obrigatório");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setDataAgendamento(LocalDate.parse(request.getDataAgendamento()));
        agendamento.setHoraAgendamento(request.getHoraAgendamento());
        agendamento.setDescricao(request.getDescricao());
        agendamento.setUrgencia(request.getUrgencia());
        agendamento.setSituacao(request.getSituacao());
        agendamento.setPreco(request.getPreco());

        Tecnico tecnico = tecnicoRepository.findById(request.getTecnicoId())
                .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));
        

        
        agendamento.setTecnico(tecnico);

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        agendamento.setUsuario(usuario);
        
        if (request.getServicoId() != null) {
            Servico servico = servicoRepository.findById(request.getServicoId())
                    .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
            agendamento.setServico(servico);
        }
        
        if (request.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
            agendamento.setCliente(cliente);
        }

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento update(Long id, AgendamentoRequest request) {
        if (agendamentoRepository.findById(id).isPresent()) {
            Agendamento agendamento = new Agendamento();
            agendamento.setId(id);
            agendamento.setDataAgendamento(LocalDate.parse(request.getDataAgendamento()));
            agendamento.setHoraAgendamento(request.getHoraAgendamento());
            agendamento.setDescricao(request.getDescricao());
            agendamento.setUrgencia(request.getUrgencia());
            agendamento.setSituacao(request.getSituacao());
            agendamento.setPreco(request.getPreco());

            Tecnico tecnico = tecnicoRepository.findById(request.getTecnicoId())
                    .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));
            

            
            agendamento.setTecnico(tecnico);

            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            agendamento.setUsuario(usuario);
            
            if (request.getServicoId() != null) {
                Servico servico = servicoRepository.findById(request.getServicoId())
                        .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
                agendamento.setServico(servico);
            }
            
            if (request.getClienteId() != null) {
                Cliente cliente = clienteRepository.findById(request.getClienteId())
                        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
                agendamento.setCliente(cliente);
            }

            return agendamentoRepository.save(agendamento);
        }
        return null;
    }

    public void deleteById(Long id) {
        agendamentoRepository.deleteById(id);
    }

    public List<Agendamento> findByUsuarioId(Long usuarioId) {
        return agendamentoRepository.findByUsuarioId(usuarioId);
    }

    public List<Agendamento> findByTecnicoId(Long tecnicoId) {
        return agendamentoRepository.findByTecnicoId(tecnicoId);
    }

    public List<Agendamento> findByDataAgendamento(LocalDate dataAgendamento) {
        return agendamentoRepository.findByDataAgendamento(dataAgendamento);
    }
    
    public List<Tecnico> findTecnicosByServicoId(Long servicoId) {
        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        return tecnicoRepository.findByEspecialidadeAndStatusTecnico(servico.getTipo(), "ATIVO");
    }
}