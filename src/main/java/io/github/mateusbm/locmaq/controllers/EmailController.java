package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/enviar-relatorio")
    public String enviarRelatorio(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String relatorio,
            @RequestParam(required = false) String mensagem,
            RedirectAttributes redirectAttributes
    ) {
        try {
            emailService.enviarRelatorio(nome, email, relatorio, mensagem);
            redirectAttributes.addFlashAttribute("mensagem", "Relatório enviado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Falha ao enviar o relatório. Tente novamente.");
        }
        return "redirect:/html/relatorios_cliente.html";
    }
}