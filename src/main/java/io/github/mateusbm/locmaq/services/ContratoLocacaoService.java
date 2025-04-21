package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.ContratoLocacaoDTO;
import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.models.Usuario;
import io.github.mateusbm.locmaq.repositories.ClienteRepository;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratoLocacaoService {

    @Autowired
    private ContratoLocacaoRepository contratoRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private ClienteRepository clienteRepo;
    @Autowired
    private EquipamentoRepository equipamentoRepo;

    public List<ContratoLocacao> listarTodos() {
        return contratoRepo.findAll();
    }

    public ContratoLocacao buscarPorId(Long id) {
        return contratoRepo.findById(id).orElse(null);
    }

    public ContratoLocacao cadastrar(ContratoLocacaoDTO dto) {
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioLogisticaId()).orElse(null);
        Cliente cliente = clienteRepo.findById(dto.getClienteId()).orElse(null);
        Equipamento equipamento = equipamentoRepo.findById(dto.getEquipamentoId()).orElse(null);
        ContratoLocacao contrato = dto.toEntity(usuario, cliente, equipamento);
        return contratoRepo.save(contrato);
    }

    public void remover(Long id) {
        contratoRepo.deleteById(id);
    }
}