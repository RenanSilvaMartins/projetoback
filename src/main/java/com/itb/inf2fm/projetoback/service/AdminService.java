package com.itb.inf2fm.projetoback.service;

import com.itb.inf2fm.projetoback.repository.ClienteRepository;
import com.itb.inf2fm.projetoback.repository.RegiaoRepository;
import com.itb.inf2fm.projetoback.repository.ServicoRepository;
import com.itb.inf2fm.projetoback.repository.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private RegiaoRepository regiaoRepository;

    public Long getTotalTecnicos() {
        return tecnicoRepository.count();
    }

    public Long getTotalClientes() {
        return clienteRepository.count();
    }

    public Long getTotalServicos() {
        return servicoRepository.count();
    }

    public Long getTotalRegioes() {
        return regiaoRepository.count();
    }
}