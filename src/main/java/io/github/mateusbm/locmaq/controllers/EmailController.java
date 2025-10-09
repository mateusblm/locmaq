package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/enviar-relatorio")
    public Map<String, Object> enviarRelatorio(@RequestBody Map<String, Object> dados) {
        Map<String, Object> resp = new HashMap<>();
        try {
            emailService.enviarRelatorioCompleto(
                    (String) dados.get("email"),
                    Long.valueOf(dados.get("boletimId").toString()),
                    Long.valueOf(dados.get("contratoId").toString()),
                    (String) dados.get("mensagem"),
                    (String) dados.get("papel") // Adicione este argumento
            );
            resp.put("sucesso", true);
            resp.put("mensagem", "✅ E-mail enviado com sucesso!");
        } catch (IllegalArgumentException e) {
            resp.put("sucesso", false);
            resp.put("mensagem", "❌ " + e.getMessage());
        } catch (Exception e) {
            resp.put("sucesso", false);
            resp.put("mensagem", "❌ Falha ao enviar o e-mail. Tente novamente.");
        }
        return resp;
    }
}