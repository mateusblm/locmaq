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

    public void enviarRelatorioCompleto(String nome, String emailDestino, Long boletimId, Long contratoId, String mensagemAdicional, String papel) {
        if (!isEmailValido(emailDestino)) {
            throw new IllegalArgumentException("E-mail informado é inválido.");
        }

        BoletimMedicao boletim = boletimMedicaoRepository.findById(boletimId).orElseThrow();
        ContratoLocacao contrato = contratoLocacaoRepository.findById(contratoId).orElseThrow();
        BoletimMedicaoDTO boletimDTO = BoletimMedicaoDTO.fromEntity(boletim);

        String assunto;
        StringBuilder corpo = new StringBuilder();

        if ("PROPRIETARIO".equalsIgnoreCase(papel)) {
            assunto = "Relatório de Repasse ao Proprietário";
            // Busca orçamento DONO
            Optional<Orcamento> orcamentoOpt = orcamentoRepository
                    .findFirstByContratoIdAndTipoOrcamentoAndStatusInOrderByDataCriacaoDesc(
                            contratoId,
                            io.github.mateusbm.locmaq.models.TipoOrcamento.DONO,
                            List.of(StatusOrcamento.APROVADO, StatusOrcamento.PENDENTE)
                    );
            corpo.append("Olá ").append(nome).append(",\n\n")
                    .append("Segue o relatório de repasse referente ao seu equipamento alugado:\n\n")
                    .append("Equipamento: ").append(contrato.getEquipamento().getNome()).append("\n")
                    .append("Período do Contrato: ").append(contrato.getDataInicio()).append(" a ").append(contrato.getDataFim()).append("\n\n")
                    .append("Boletim de Medição:\n")
                    .append("Período: ").append(boletimDTO.getDataInicio()).append(" a ").append(boletimDTO.getDataFim()).append("\n")
                    .append("Situação: ").append(boletimDTO.getSituacao()).append("\n")
                    .append("Planejador Responsável: ").append(boletimDTO.getPlanejadorNome()).append("\n\n");

            if (orcamentoOpt.isPresent()) {
                Orcamento orc = orcamentoOpt.get();
                corpo.append("Detalhes do Repasse:\n")
                        .append("- Dias Trabalhados: ").append(orc.getDiasTrabalhados()).append("\n")
                        .append("- Valor Total a Receber: R$ ").append(String.format("%.2f", orc.getValorTotal())).append("\n\n");
            } else {
                corpo.append("Não há orçamento de repasse aprovado ou pendente para este contrato.\n\n");
            }
        } else {
            assunto = "Relatório de Locação de Equipamento";
            // Busca orçamento CLIENTE
            Optional<Orcamento> orcamentoOpt = orcamentoRepository
                    .findFirstByContratoIdAndTipoOrcamentoAndStatusInOrderByDataCriacaoDesc(
                            contratoId,
                            io.github.mateusbm.locmaq.models.TipoOrcamento.CLIENTE,
                            List.of(StatusOrcamento.APROVADO, StatusOrcamento.PENDENTE)
                    );
            corpo.append("Olá ").append(nome).append(",\n\n")
                    .append("Segue o relatório detalhado da sua locação:\n\n")
                    .append("Equipamento: ").append(contrato.getEquipamento().getNome()).append("\n")
                    .append("Período do Contrato: ").append(contrato.getDataInicio()).append(" a ").append(contrato.getDataFim()).append("\n\n")
                    .append("Boletim de Medição:\n")
                    .append("Período: ").append(boletimDTO.getDataInicio()).append(" a ").append(boletimDTO.getDataFim()).append("\n")
                    .append("Situação: ").append(boletimDTO.getSituacao()).append("\n")
                    .append("Planejador Responsável: ").append(boletimDTO.getPlanejadorNome()).append("\n\n");

            if (orcamentoOpt.isPresent()) {
                Orcamento orc = orcamentoOpt.get();
                corpo.append("Detalhes do Orçamento:\n")
                        .append("- Dias Trabalhados: ").append(orc.getDiasTrabalhados()).append("\n")
                        .append("- Desconto: R$ ").append(String.format("%.2f", orc.getDesconto())).append("\n")
                        .append("- Valor Total do Aluguel: R$ ").append(String.format("%.2f", orc.getValorTotal())).append("\n\n");
            } else {
                corpo.append("Não há orçamento aprovado ou pendente para este contrato.\n\n");
            }
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