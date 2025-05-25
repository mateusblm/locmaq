package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.BoletimMedicaoDTO;
import io.github.mateusbm.locmaq.dto.EquipamentoBoletimMedicaoDTO;
import io.github.mateusbm.locmaq.models.BoletimMedicao;
import io.github.mateusbm.locmaq.models.ContratoLocacao;
import io.github.mateusbm.locmaq.models.Orcamento;
import io.github.mateusbm.locmaq.models.StatusOrcamento;
import io.github.mateusbm.locmaq.repositories.BoletimMedicaoRepository;
import io.github.mateusbm.locmaq.repositories.ContratoLocacaoRepository;
import io.github.mateusbm.locmaq.repositories.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BoletimMedicaoRepository boletimMedicaoRepository;

    @Autowired
    private ContratoLocacaoRepository contratoLocacaoRepository;

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    public boolean isEmailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public void enviarRelatorioCompleto(String nome, String emailDestino, Long boletimId, Long contratoId, String mensagemAdicional) {
        if (!isEmailValido(emailDestino)) {
            throw new IllegalArgumentException("E-mail informado é inválido.");
        }

        BoletimMedicao boletim = boletimMedicaoRepository.findById(boletimId).orElseThrow();
        ContratoLocacao contrato = contratoLocacaoRepository.findById(contratoId).orElseThrow();
        BoletimMedicaoDTO boletimDTO = BoletimMedicaoDTO.fromEntity(boletim);

        // Busca orçamento aprovado ou pendente mais recente
        Optional<Orcamento> orcamentoOpt = orcamentoRepository
                .findFirstByContratoIdAndStatusInOrderByDataCriacaoDesc(
                        contratoId,
                        List.of(StatusOrcamento.APROVADO, StatusOrcamento.PENDENTE)
                );

        String assunto = "Relatório de Locação de Equipamento";

        StringBuilder corpo = new StringBuilder();
        corpo.append("Olá ").append(nome).append(",\n\n")
                .append("Segue o relatório detalhado da sua locação:\n\n")
                .append("Equipamento: ").append(contrato.getEquipamento().getNome()).append("\n")
                .append("Período do Contrato: ").append(contrato.getDataInicio()).append(" a ").append(contrato.getDataFim()).append("\n")
                .append("Valor Diária: R$ ").append(contrato.getValorTotal()).append("\n\n");

        corpo.append("Boletim de Medição:\n")
                .append("Período: ").append(boletimDTO.getDataInicio()).append(" a ").append(boletimDTO.getDataFim()).append("\n")
                .append("Situação: ").append(boletimDTO.getSituacao()).append("\n")
                .append("Planejador Responsável: ").append(boletimDTO.getPlanejadorNome()).append("\n\n");

        // Detalhes do orçamento
        if (orcamentoOpt.isPresent()) {
            Orcamento orc = orcamentoOpt.get();
            corpo.append("Detalhes do Orçamento:\n")
                    .append("- Dias Trabalhados: ").append(orc.getDiasTrabalhados()).append("\n")
                    .append("- Desconto: R$ ").append(String.format("%.2f", orc.getDesconto())).append("\n")
                    .append("- Valor Total do Aluguel: R$ ").append(String.format("%.2f", orc.getValorTotal())).append("\n\n");
        } else {
            corpo.append("Não há orçamento aprovado ou pendente para este contrato.\n\n");
        }



        if (mensagemAdicional != null && !mensagemAdicional.isEmpty()) {
            corpo.append("\nMensagem adicional:\n").append(mensagemAdicional).append("\n");
        }

        corpo.append("\nAtenciosamente,\nEquipe de Locação");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestino);
        message.setSubject(assunto);
        message.setText(corpo.toString());

        mailSender.send(message);
    }
}