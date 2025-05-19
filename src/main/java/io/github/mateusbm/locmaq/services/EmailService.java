package io.github.mateusbm.locmaq.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarRelatorio(String nome, String emailDestino, String relatorio, String mensagemAdicional) {
        String assunto = "Relatório: " + relatorio;

        String corpo = "Olá " + nome + ",\n\n"
                + "Segue o relatório solicitado: " + relatorio + "\n\n"
                + (mensagemAdicional != null && !mensagemAdicional.isEmpty() ? mensagemAdicional + "\n\n" : "")
                + "Atenciosamente,\nEquipe de Locação";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestino);
        message.setSubject(assunto);
        message.setText(corpo);

        mailSender.send(message);
    }
}