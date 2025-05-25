package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.models.Cliente;
import io.github.mateusbm.locmaq.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> criar(@RequestBody Cliente cliente) {
        try {
            Cliente novoCliente = clienteService.cadastrarCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Já existe um cliente com esse nome.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar cliente.");
        }
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clienteService.atualizarCliente(id, cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        try {
            clienteService.removerCliente(id);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não é possível remover o cliente pois existem equipamentos vinculados.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao remover cliente.");
        }
    }

    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long id) {
        return clienteService.buscarClientePorId(id).orElse(null);
    }
}