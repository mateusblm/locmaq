package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.RfidLeituraAdminDTO;
import io.github.mateusbm.locmaq.dto.RfidLeituraRequestDTO;
import io.github.mateusbm.locmaq.dto.RfidLeituraResponseDTO;
import io.github.mateusbm.locmaq.dto.RfidTagRequestDTO;
import io.github.mateusbm.locmaq.dto.RfidTagResponseDTO;
import io.github.mateusbm.locmaq.models.Equipamento;
import io.github.mateusbm.locmaq.models.RfidLeitura;
import io.github.mateusbm.locmaq.models.RfidTag;
import io.github.mateusbm.locmaq.repositories.EquipamentoRepository;
import io.github.mateusbm.locmaq.repositories.RfidLeituraRepository;
import io.github.mateusbm.locmaq.repositories.RfidTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RfidService {

    private final RfidTagRepository rfidTagRepository;
    private final RfidLeituraRepository rfidLeituraRepository;
    private final EquipamentoRepository equipamentoRepository;

    public RfidService(
            RfidTagRepository rfidTagRepository,
            RfidLeituraRepository rfidLeituraRepository,
            EquipamentoRepository equipamentoRepository) {
        this.rfidTagRepository = rfidTagRepository;
        this.rfidLeituraRepository = rfidLeituraRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    public RfidLeituraResponseDTO registrarLeitura(RfidLeituraRequestDTO dto) {
        String uid = normalizarUid(dto.getUid());
        if (uid.isBlank()) {
            throw new RuntimeException("UID RFID obrigatorio.");
        }

        String origem = dto.getOrigem() == null ? null : dto.getOrigem().trim();
        String tipo = dto.getTipo() == null ? "EQUIPAMENTO" : dto.getTipo().trim().toUpperCase();
        Optional<RfidTag> tagOpt = rfidTagRepository.findByUidAndAtivoTrue(uid);
        Equipamento equipamento = tagOpt.map(RfidTag::getEquipamento).orElse(null);
        boolean autorizado = equipamento != null;
        String movimento = null;
        String mensagem;

        if (equipamento == null) {
            mensagem = "Tag nao cadastrada";
        } else if (equipamento.isDisponibilidade()) {
            movimento = "SAIDA";
            mensagem = "Saida registrada";
            equipamento.setDisponibilidade(false);
            equipamentoRepository.save(equipamento);
        } else {
            movimento = "ENTRADA";
            mensagem = "Entrada registrada";
            equipamento.setDisponibilidade(true);
            equipamentoRepository.save(equipamento);
        }

        RfidLeitura leitura = new RfidLeitura();
        leitura.setUid(uid);
        leitura.setOrigem(origem);
        leitura.setTipo(tipo);
        leitura.setMovimento(movimento);
        leitura.setAutorizado(autorizado);
        leitura.setMensagem(mensagem);
        leitura.setEquipamento(equipamento);
        rfidLeituraRepository.save(leitura);

        return new RfidLeituraResponseDTO(
                autorizado,
                mensagem,
                uid,
                equipamento != null ? equipamento.getNome() : null,
                movimento,
                equipamento != null ? statusEquipamento(equipamento) : null
        );
    }

    public List<RfidTagResponseDTO> listarTags() {
        return rfidTagRepository.findAll().stream()
                .map(this::toTagResponse)
                .toList();
    }

    public RfidTagResponseDTO cadastrarTag(RfidTagRequestDTO dto) {
        String uid = normalizarUid(dto.getUid());
        if (uid.isBlank()) {
            throw new RuntimeException("UID RFID obrigatorio.");
        }
        if (rfidTagRepository.existsByUid(uid)) {
            throw new RuntimeException("Ja existe uma tag RFID cadastrada com este UID.");
        }

        RfidTag tag = new RfidTag();
        tag.setUid(uid);
        preencherTag(tag, dto);
        return toTagResponse(rfidTagRepository.save(tag));
    }

    public RfidTagResponseDTO editarTag(Long id, RfidTagRequestDTO dto) {
        RfidTag tag = rfidTagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag RFID nao encontrada."));

        String uid = normalizarUid(dto.getUid());
        if (uid.isBlank()) {
            throw new RuntimeException("UID RFID obrigatorio.");
        }

        Optional<RfidTag> existente = rfidTagRepository.findByUid(uid);
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new RuntimeException("Ja existe uma tag RFID cadastrada com este UID.");
        }

        tag.setUid(uid);
        preencherTag(tag, dto);
        return toTagResponse(rfidTagRepository.save(tag));
    }

    public void removerTag(Long id) {
        rfidTagRepository.deleteById(id);
    }

    public List<RfidLeituraAdminDTO> listarUltimasLeituras() {
        return rfidLeituraRepository.findTop20ByOrderByDataHoraDesc().stream()
                .map(this::toLeituraAdmin)
                .toList();
    }

    public List<RfidLeituraAdminDTO> listarHistoricoMovimentos() {
        return rfidLeituraRepository.findTop100ByOrderByDataHoraDesc().stream()
                .map(this::toLeituraAdmin)
                .toList();
    }

    private void preencherTag(RfidTag tag, RfidTagRequestDTO dto) {
        tag.setDescricao(dto.getDescricao());
        tag.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());

        if (dto.getEquipamentoId() == null) {
            tag.setEquipamento(null);
            return;
        }

        Equipamento equipamento = equipamentoRepository.findById(dto.getEquipamentoId())
                .orElseThrow(() -> new RuntimeException("Equipamento nao encontrado."));
        tag.setEquipamento(equipamento);
    }

    private String normalizarUid(String uid) {
        if (uid == null) {
            return "";
        }
        return uid.replaceAll("\\s+", "").toUpperCase();
    }

    private RfidTagResponseDTO toTagResponse(RfidTag tag) {
        Equipamento equipamento = tag.getEquipamento();
        return new RfidTagResponseDTO(
                tag.getId(),
                tag.getUid(),
                tag.getDescricao(),
                tag.getAtivo(),
                equipamento != null ? equipamento.getId() : null,
                equipamento != null ? equipamento.getNome() : null,
                equipamento != null ? statusEquipamento(equipamento) : null,
                tag.getCreatedAt()
        );
    }

    private RfidLeituraAdminDTO toLeituraAdmin(RfidLeitura leitura) {
        Equipamento equipamento = leitura.getEquipamento();
        return new RfidLeituraAdminDTO(
                leitura.getId(),
                leitura.getUid(),
                leitura.getOrigem(),
                leitura.getTipo(),
                leitura.getMovimento(),
                leitura.getAutorizado(),
                leitura.getMensagem(),
                leitura.getDataHora(),
                equipamento != null ? equipamento.getNome() : null,
                equipamento != null ? statusEquipamento(equipamento) : null
        );
    }

    private String statusEquipamento(Equipamento equipamento) {
        return equipamento.isDisponibilidade() ? "NO_ESTOQUE" : "FORA_EM_USO";
    }
}
