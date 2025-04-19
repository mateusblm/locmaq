package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente cadastrarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente atualizarCliente(Long id, Cliente atualizacao) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        cliente.setNome(atualizacao.getNome());
        cliente.setCnpj(atualizacao.getCnpj());
        cliente.setEndereco(atualizacao.getEndereco());
        cliente.setEmail(atualizacao.getEmail());
        cliente.setTelefone(atualizacao.getTelefone());
        return clienteRepository.save(cliente);
    }

    public void removerCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}
