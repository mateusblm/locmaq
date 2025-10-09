package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.ClienteBuscaDTO;
import io.github.mateusbm.locmaq.events.LogAction; 
import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.repositories.ClienteRepository;
import io.github.mateusbm.locmaq.utils.ValidadorUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ApplicationEventPublisher eventPublisher; 

    public ClienteService(ClienteRepository clienteRepository, ApplicationEventPublisher eventPublisher) {
        this.clienteRepository = clienteRepository;
        this.eventPublisher = eventPublisher;
    }

    private String getUsuarioAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente cadastrarCliente(Cliente cliente) {
        String doc = cliente.getCnpj().replaceAll("\\D", "");
        if (doc.length() == 11) {
            if (!ValidadorUtil.isCpfValido(doc)) {
                throw new IllegalArgumentException("CPF inválido");
            }
        } else if (doc.length() == 14) {
            if (!ValidadorUtil.isCnpjValido(doc)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }
        } else {
            throw new IllegalArgumentException("CPF/CNPJ deve ter 11 ou 14 dígitos");
        }
        Cliente saved = clienteRepository.save(cliente);
        
        eventPublisher.publishEvent(new LogAction(
                "Cadastro de cliente",
                getUsuarioAutenticado(),
                "Cliente ID: " + saved.getId() + ", Nome: " + saved.getNome()
        ));
        return saved;
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente atualizarCliente(Long id, Cliente atualizacao) {
        String doc = atualizacao.getCnpj().replaceAll("\\D", "");
        if (doc.length() == 11) {
            if (!ValidadorUtil.isCpfValido(doc)) {
                throw new IllegalArgumentException("CPF inválido");
            }
        } else if (doc.length() == 14) {
            if (!ValidadorUtil.isCnpjValido(doc)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }
        } else {
            throw new IllegalArgumentException("CPF/CNPJ deve ter 11 ou 14 dígitos");
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        cliente.setNome(atualizacao.getNome());
        cliente.setCnpj(atualizacao.getCnpj());
        cliente.setEndereco(atualizacao.getEndereco());
        cliente.setEmail(atualizacao.getEmail());
        cliente.setTelefone(atualizacao.getTelefone());
        Cliente saved = clienteRepository.save(cliente);
        
        eventPublisher.publishEvent(new LogAction(
                "Atualização de cliente",
                getUsuarioAutenticado(),
                "Cliente ID: " + saved.getId() + ", Nome: " + saved.getNome()
        ));
        return saved;
    }

    public void removerCliente(Long id) {
        clienteRepository.deleteById(id);
        
        eventPublisher.publishEvent(new LogAction(
                "Remoção de cliente", 
                getUsuarioAutenticado(), 
                "Cliente ID: " + id
        ));
    }

    public List<ClienteBuscaDTO> buscarPorNomeDTO(String nome) {
        return clienteRepository.findAll().stream()
            .filter(c -> c.getNome() != null && c.getNome().toLowerCase().contains(nome.toLowerCase()))
            .map(c -> new ClienteBuscaDTO(c.getId(), c.getNome(), c.getEmail()))
            .collect(Collectors.toList());
    }
}