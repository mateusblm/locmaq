package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.RfidLeituraRequestDTO;
import io.github.mateusbm.locmaq.dto.RfidLeituraResponseDTO;
import io.github.mateusbm.locmaq.dto.RfidTagRequestDTO;
import io.github.mateusbm.locmaq.dto.RfidTagResponseDTO;
import io.github.mateusbm.locmaq.services.RfidService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rfid")
public class RfidController {

    private final RfidService rfidService;

    public RfidController(RfidService rfidService) {
        this.rfidService = rfidService;
    }

    @PostMapping("/read")
    public ResponseEntity<?> receberLeitura(@RequestBody RfidLeituraRequestDTO dto) {
        try {
            RfidLeituraResponseDTO resposta = rfidService.registrarLeitura(dto);
            return ResponseEntity.ok(resposta);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ErroResponse(ex.getMessage()));
        }
    }

    @GetMapping("/tags")
    public List<RfidTagResponseDTO> listarTags() {
        return rfidService.listarTags();
    }

    @PostMapping("/tags")
    public ResponseEntity<?> cadastrarTag(@RequestBody RfidTagRequestDTO dto) {
        try {
            return ResponseEntity.ok(rfidService.cadastrarTag(dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ErroResponse(ex.getMessage()));
        }
    }

    @PutMapping("/tags/{id}")
    public ResponseEntity<?> editarTag(@PathVariable Long id, @RequestBody RfidTagRequestDTO dto) {
        try {
            return ResponseEntity.ok(rfidService.editarTag(id, dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ErroResponse(ex.getMessage()));
        }
    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<?> removerTag(@PathVariable Long id) {
        try {
            rfidService.removerTag(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ErroResponse(ex.getMessage()));
        }
    }

    @GetMapping("/leituras")
    public ResponseEntity<?> listarUltimasLeituras() {
        return ResponseEntity.ok(rfidService.listarUltimasLeituras());
    }

    @GetMapping("/historico")
    public ResponseEntity<?> listarHistoricoMovimentos() {
        return ResponseEntity.ok(rfidService.listarHistoricoMovimentos());
    }

    static class ErroResponse {
        private String mensagem;

        public ErroResponse(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }
    }
}
