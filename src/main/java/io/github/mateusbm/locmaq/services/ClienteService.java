package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.adapter.DocumentValidator;
import io.github.mateusbm.locmaq.dto.ClienteBuscaDTO;
import io.github.mateusbm.locmaq.events.LogAction; 
import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.repositories.ClienteRepository;
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
    private final DocumentValidator documentValidator; 

    public ClienteService(ClienteRepository clienteRepository, ApplicationEventPublisher eventPublisher, DocumentValidator documentValidator) {
        this.clienteRepository = clienteRepository;
        this.eventPublisher = eventPublisher;
        this.documentValidator = documentValidator; 
    }

    private String getUsuarioAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente cadastrarCliente(Cliente cliente) {
        documentValidator.validateCpfCnpj(cliente.getCnpj());
        
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
        documentValidator.validateCpfCnpj(atualizacao.getCnpj());

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