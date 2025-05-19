package io.github.mateusbm.locmaq.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RelatorioController {

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
        emailService.enviarRelatorio(nome, email, relatorio, mensagem);
        redirectAttributes.addFlashAttribute("mensagem", "Relatório enviado com sucesso!");
        return "redirect:/formulario-relatorio";
    }
}