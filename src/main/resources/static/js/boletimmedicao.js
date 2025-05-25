// Converte yyyy-MM-dd para dd/MM/yyyy
function toDDMMYYYY(dateStr) {
    if (!dateStr) return "";
    if (dateStr.includes("/")) return dateStr;
    const [y, m, d] = dateStr.split("-");
    return `${d}/${m}/${y}`;
}

// Converte dd/MM/yyyy para yyyy-MM-dd
function toYYYYMMDD(dateStr) {
    if (!dateStr) return "";
    if (dateStr.includes("-")) return dateStr;
    const [d, m, y] = dateStr.split("/");
    return `${y}-${m.padStart(2, "0")}-${d.padStart(2, "0")}`;
}

// Exibe dd/MM/yyyy para tabelas e detalhes
function exibirDDMMYYYY(dateStr) {
    return toDDMMYYYY(dateStr);
}

document.addEventListener("DOMContentLoaded", () => {
    fetch('/api/usuarios?tipo=PLANEJADOR')
        .then(r => r.json())
        .then(data => {
            const sel = document.getElementById("planejadorSelect");
            sel.innerHTML = '<option value="">Selecione...</option>' +
                data.map(p => `<option value="${p.id}">${p.nome}</option>`).join('');
        });

    fetch('/api/equipamentos')
        .then(r => r.json())
        .then(eqs => window.equipamentos = eqs);

    document.getElementById("boletimForm").onsubmit = handleFormSubmit;
    document.getElementById("editBtn").onclick = handleEditSubmit;
    document.getElementById("cancelBtn").onclick = resetForm;

    carregarBoletins();
});

let editandoId = null;

function addEquipamento(equip = {}) {
    const div = document.createElement('div');
    div.className = 'equipamento-item';
    div.innerHTML = `
        <select name="equipamentoId" required>
            ${window.equipamentos.map(eq => `<option value="${eq.id}" ${eq.id == equip.equipamentoId ? 'selected' : ''}>${eq.nome}</option>`).join('')}
        </select>
        <input type="number" name="quantidade" required min="1" placeholder="Quantidade" value="${equip.quantidade || ''}">
        <input type="number" name="valorMedido" required step="0.01" min="0" placeholder="Valor medido" value="${equip.valorMedido || ''}">
        <input type="text" name="observacao" placeholder="Observação" value="${equip.observacao || ''}">
        <button type="button" onclick="this.parentElement.remove()">Remover</button>
    `;
    document.getElementById('equipamentos-list').appendChild(div);
}

// Exibe mensagem padronizada na tela
function exibirMensagem(msg, tipo = "erro") {
    const div = document.getElementById("mensagem-erro");
    if (!div) return;
    div.innerText = msg;
    div.style.display = "block";
    div.style.background = tipo === "sucesso" ? "#e0ffe0" : "#ffe0e0";
    div.style.color = tipo === "sucesso" ? "#0a0" : "#a00";
    div.style.border = tipo === "sucesso" ? "1px solid #9f9" : "1px solid #f99";
    setTimeout(() => { div.style.display = "none"; }, 5000);
}

function handleFormSubmit(e) {
    e.preventDefault();
    if (editandoId) return; // Evita submit duplo

    const form = e.target;
    const equipamentos = Array.from(form.querySelectorAll('.equipamento-item')).map(node => {
        let valorBruto = node.querySelector('[name="valorMedido"]').value;
        let valorMedido = parseFloat(valorBruto);
        if (!valorBruto || isNaN(valorMedido)) valorMedido = 0;

        return {
            equipamentoId: node.querySelector('[name="equipamentoId"]').value,
            quantidade: parseInt(node.querySelector('[name="quantidade"]').value) || 1,
            valorMedido,
            observacao: node.querySelector('[name="observacao"]').value
        };
    });

    if (equipamentos.length === 0) {
        exibirMensagem("Adicione ao menos um equipamento para salvar!");
        return;
    }

    // Converte datas para dd/MM/yyyy antes de enviar
    const body = {
        dataInicio: toDDMMYYYY(form.dataInicio.value),
        dataFim: toDDMMYYYY(form.dataFim.value),
        planejadorId: form.planejadorId.value,
        situacao: form.situacao.value,
        equipamentos
    };

    fetch('/api/boletins', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    }).then(async r => {
        if (r.ok) {
            exibirMensagem('Boletim cadastrado!', "sucesso");
            resetForm();
            carregarBoletins();
        } else {
            const err = await r.text();
            exibirMensagem('Erro ao cadastrar!\n' + err);
        }
    });
}

function handleEditSubmit() {
    const form = document.getElementById("boletimForm");
    const equipamentos = Array.from(form.querySelectorAll('.equipamento-item')).map(node => {
        let valorBruto = node.querySelector('[name="valorMedido"]').value;
        let valorMedido = parseFloat(valorBruto);
        if (!valorBruto || isNaN(valorMedido)) valorMedido = 0;

        return {
            equipamentoId: node.querySelector('[name="equipamentoId"]').value,
            quantidade: parseInt(node.querySelector('[name="quantidade"]').value) || 1,
            valorMedido,
            observacao: node.querySelector('[name="observacao"]').value
        };
    });

    if (equipamentos.length === 0) {
        exibirMensagem("Adicione ao menos um equipamento para salvar!");
        return;
    }

    // Converte datas para dd/MM/yyyy antes de enviar
    const body = {
        dataInicio: toDDMMYYYY(form.dataInicio.value),
        dataFim: toDDMMYYYY(form.dataFim.value),
        planejadorId: form.planejadorId.value,
        situacao: form.situacao.value,
        equipamentos
    };

    fetch(`/api/boletins/${editandoId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    }).then(async r => {
        if (r.ok) {
            exibirMensagem('Boletim atualizado!', "sucesso");
            resetForm();
            carregarBoletins();
        } else {
            const err = await r.text();
            exibirMensagem('Erro ao atualizar!\n' + err);
        }
    });
}

function carregarBoletins() {
    fetch('/api/boletins')
        .then(r => r.json())
        .then(boletins => {
            const tbody = document.getElementById("tabelaBoletins");
            if (!Array.isArray(boletins) || boletins.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" style="text-align:center">Nenhum boletim cadastrado</td></tr>`;
                return;
            }
            tbody.innerHTML = boletins.map(b => `
                <tr>
                    <td>${exibirDDMMYYYY(b.dataInicio)} a ${exibirDDMMYYYY(b.dataFim)}</td>
                    <td>${b.planejadorNome || "-"}</td>
                    <td>${b.situacao}</td>
                    <td>${b.assinado ? 'Sim' : 'Não'}</td>
                    <td>
                        <button class="visualizar-btn" onclick="abrirDetalhe(${b.id})">Visualizar</button>
                        <button class="visualizar-btn" onclick="editarBoletim(${b.id})">Editar</button>
                    </td>
                </tr>
            `).join('');
        });
}

window.abrirDetalhe = function(id) {
    fetch(`/api/boletins/${id}`)
        .then(r => r.json())
        .then(b => {
            document.getElementById('boletimDetailSection').style.display = "block";
            document.getElementById('boletimIdDetalhe').value = b.id;

            // Troca o input para texto e exibe dd/MM/yyyy
            const dataInicioInput = document.getElementById('dataInicioDetalhe');
            const dataFimInput = document.getElementById('dataFimDetalhe');
            dataInicioInput.type = "text";
            dataFimInput.type = "text";
            dataInicioInput.value = exibirDDMMYYYY(b.dataInicio);
            dataFimInput.value = exibirDDMMYYYY(b.dataFim);

            document.getElementById('planejadorDetalhe').value = b.planejadorNome || '-';
            document.getElementById('situacaoDetalhe').value = b.situacao;
            window.boletimDetalheId = b.id;
        });
};

window.editarBoletim = function(id) {
    fetch(`/api/boletins/${id}`)
        .then(r => r.json())
        .then(b => {
            const form = document.getElementById("boletimForm");
            form.dataInicio.value = toYYYYMMDD(b.dataInicio);
            form.dataFim.value = toYYYYMMDD(b.dataFim);
            form.planejadorId.value = b.planejadorId;
            form.situacao.value = b.situacao;
            document.getElementById('equipamentos-list').innerHTML = '';
            (b.equipamentos || []).forEach(eq => addEquipamento(eq));
            editandoId = b.id;
            document.getElementById("saveBtn").style.display = "none";
            document.getElementById("editBtn").style.display = "inline";
            document.getElementById("cancelBtn").style.display = "inline";
        });
};

function resetForm() {
    const form = document.getElementById("boletimForm");
    form.reset();
    document.getElementById('equipamentos-list').innerHTML = '';
    editandoId = null;
    document.getElementById("saveBtn").style.display = "inline";
    document.getElementById("editBtn").style.display = "none";
    document.getElementById("cancelBtn").style.display = "none";
    // Restaura os inputs de data para type="date" nos detalhes
    const dataInicioInput = document.getElementById('dataInicioDetalhe');
    const dataFimInput = document.getElementById('dataFimDetalhe');
    if (dataInicioInput) dataInicioInput.type = "date";
    if (dataFimInput) dataFimInput.type = "date";
}

window.assinarBoletim = function(id) {
    fetch(`/api/boletins/${id}/assinar`, { method: 'POST' })
        .then(async r => {
            if (r.ok) {
                carregarBoletins();
            } else {
                const err = await r.text();
                exibirMensagem('Erro ao assinar!\n' + err);
            }
        });
}

function fecharDetalhe() {
    document.getElementById('boletimDetailSection').style.display = "none";
}