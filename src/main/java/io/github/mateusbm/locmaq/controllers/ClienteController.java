package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> listarTodosClientes() {
        return clienteService.listarTodosClientes();
    }

    @PostMapping
    public Cliente criar(@RequestBody Cliente cliente) {
        return clienteService.cadastrarCliente(cliente);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clienteService.atualizarCliente(id, cliente);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        clienteService.removerCliente(id);
    }

    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long id) {
        return clienteService.buscarClientePorId(id).orElse(null);
    }
}
