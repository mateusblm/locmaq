let contratos = [];
let contratosMap = {};
let contratoSelecionado = null;
let orcamentos = [];
let editandoId = null;

function mostrarMensagem(msg, tipo = "erro") {
    const div = document.getElementById('mensagem-erro');
    div.style.display = 'block';
    div.textContent = msg;
    div.style.background = tipo === "sucesso" ? "#e0ffe0" : "#ffe0e0";
    div.style.color = tipo === "sucesso" ? "#0a0" : "#a00";
    div.style.border = tipo === "sucesso" ? "1px solid #9f9" : "1px solid #f99";
    setTimeout(() => { div.style.display = 'none'; }, 5000);
}

function submitPadrao(e) {
    e.preventDefault();
    if (!contratoSelecionado) {
        mostrarMensagem("Selecione um contrato antes de criar o orçamento.");
        return;
    }
    const dias = parseInt(document.getElementById('diasTrabalhados').value) || 0;
    const desconto = parseFloat(document.getElementById('desconto').value) || 0;
    const taxaLucro = parseFloat(document.getElementById('taxaLucro').value) || 0;
    const orcamento = {
        contrato: { id: contratoSelecionado.id },
        diasTrabalhados: dias,
        desconto: desconto,
        taxaLucro: taxaLucro
    };
    fetch('/api/orcamentos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orcamento)
    })
    .then(async r => {
        if (!r.ok) {
            let msg = "Erro ao salvar orçamento.";
            try {
                const data = await r.json();
                msg = data.message || data.mensagem || msg;
            } catch {
                // fallback para texto puro
                msg = await r.text();
            }
            throw new Error(msg);
        }
        mostrarMensagem('Orçamentos (cliente e dono) salvos com sucesso!', "sucesso");
        document.getElementById('orcamentoForm').reset();
        document.getElementById('infoContrato').style.display = 'none';
        document.getElementById('valorTotal').textContent = 'R$ 0,00';
        carregarOrcamentos();
        editandoId = null;
    })
    .catch(err => mostrarMensagem(err.message));
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('orcamentoForm').onsubmit = submitPadrao;
    carregarContratos().then(carregarOrcamentos);
});

function carregarContratos() {
    return fetch('/api/contrato-locacoes')
        .then(r => r.json())
        .then(data => {
            contratos = data;
            contratosMap = {};
            data.forEach(c => contratosMap[c.id] = c);
            const sel = document.getElementById('contratoSelect');
            sel.innerHTML = '<option value="">Selecione...</option>' +
                contratos.map(c => `<option value="${c.id}">${c.clienteNome} - ${c.equipamentoNome}</option>`).join('');
        });
}

document.getElementById('contratoSelect').addEventListener('change', function() {
    const id = this.value;
    contratoSelecionado = contratos.find(c => c.id == id);
    if (contratoSelecionado) {
        document.getElementById('infoContrato').style.display = 'block';
        document.getElementById('clienteNome').textContent = contratoSelecionado.clienteNome;
        document.getElementById('equipamentoNome').textContent = contratoSelecionado.equipamentoNome;
        document.getElementById('valorDiaria').textContent = contratoSelecionado.valorTotal != null
            ? contratoSelecionado.valorTotal.toLocaleString('pt-BR', {minimumFractionDigits:2})
            : '0,00';
    } else {
        document.getElementById('infoContrato').style.display = 'none';
    }
    calcularOrcamento();
});

document.getElementById('diasTrabalhados').addEventListener('input', calcularOrcamento);
document.getElementById('desconto').addEventListener('input', calcularOrcamento);
document.getElementById('taxaLucro').addEventListener('input', calcularOrcamento);

function calcularOrcamento() {
    if (!contratoSelecionado) {
        document.getElementById('valorTotal').textContent = 'R$ 0,00';
        return;
    }
    const diaria = parseFloat(contratoSelecionado.valorTotal) || 0;
    const dias = parseInt(document.getElementById('diasTrabalhados').value) || 0;
    const desconto = parseFloat(document.getElementById('desconto').value) || 0;
    const taxaLucro = parseFloat(document.getElementById('taxaLucro').value) || 0;

    let totalCliente = diaria * dias;
    let totalDono = totalCliente - desconto - (totalCliente * taxaLucro / 100.0);

    if (totalCliente < 0) totalCliente = 0;
    if (totalDono < 0) totalDono = 0;

    document.getElementById('valorTotal').textContent =
        'Cliente: R$ ' + totalCliente.toLocaleString('pt-BR', {minimumFractionDigits:2}) +
        ' | Dono: R$ ' + totalDono.toLocaleString('pt-BR', {minimumFractionDigits:2});
}

function carregarOrcamentos() {
    fetch('/api/orcamentos')
        .then(r => r.json())
        .then(data => {
            orcamentos = data;
            const tbody = document.getElementById('tabelaOrcamentos');
            tbody.innerHTML = '';
            data.forEach(o => {
                const contrato = contratosMap[o.contrato?.id] || {};
                const cliente = contrato.clienteNome || '-';
                const equipamento = contrato.equipamentoNome || '-';
                tbody.innerHTML += `
                    <tr>
                        <td>${cliente}</td>
                        <td>${equipamento}</td>
                        <td>${o.diasTrabalhados}</td>
                        <td>R$ ${o.desconto != null ? o.desconto.toLocaleString('pt-BR', {minimumFractionDigits:2}) : '0,00'}</td>
                        <td>R$ ${o.valorTotal != null ? o.valorTotal.toLocaleString('pt-BR', {minimumFractionDigits:2}) : '0,00'}</td>
                        <td>${o.tipoOrcamento}</td>
                        <td>${o.status}</td>
                        <td>${o.aprovadoPor || '-'}</td>
                        <td>
                            ${o.status === 'PENDENTE' ? `
                                <button class="visualizar-btn" onclick="editarOrcamento(${o.id})">Editar</button>
                                <button class="acao-btn delete" onclick="excluirOrcamento(${o.id})">Excluir</button>
                            ` : ''}
                        </td>
                    </tr>
                `;
            });
        });
}

function editarOrcamento(id) {
    const o = orcamentos.find(x => x.id === id);
    if (!o) return;
    editandoId = id;
    document.getElementById('contratoSelect').value = o.contrato.id;
    document.getElementById('contratoSelect').dispatchEvent(new Event('change'));
    document.getElementById('diasTrabalhados').value = o.diasTrabalhados;
    document.getElementById('desconto').value = o.desconto;
    document.getElementById('taxaLucro').value = o.taxaLucro;
    calcularOrcamento();

    document.getElementById('orcamentoForm').onsubmit = function(e) {
        e.preventDefault();
        const dias = parseInt(document.getElementById('diasTrabalhados').value) || 0;
        const desconto = parseFloat(document.getElementById('desconto').value) || 0;
        const taxaLucro = parseFloat(document.getElementById('taxaLucro').value) || 0;
        const diaria = parseFloat(contratosMap[o.contrato.id].valorTotal) || 0;
        let total = o.tipoOrcamento === 'CLIENTE'
            ? diaria * dias
            : (diaria * dias) - desconto - ((diaria * dias) * taxaLucro / 100.0);
        const novo = {
            contrato: { id: o.contrato.id },
            diasTrabalhados: dias,
            desconto: desconto,
            valorTotal: total,
            taxaLucro: taxaLucro
        };
        fetch(`/api/orcamentos/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novo)
        })
        .then(async r => {
            if (!r.ok) {
                const msg = await r.text();
                throw new Error(msg || "Erro ao editar orçamento.");
            }
            mostrarMensagem('Orçamento editado com sucesso!', "sucesso");
            document.getElementById('orcamentoForm').reset();
            document.getElementById('orcamentoForm').onsubmit = submitPadrao;
            document.getElementById('infoContrato').style.display = 'none';
            document.getElementById('valorTotal').textContent = 'R$ 0,00';
            carregarOrcamentos();
            editandoId = null;
        })
        .catch(err => mostrarMensagem(err.message));
    };
}

function excluirOrcamento(id) {
    if (!confirm('Deseja realmente excluir este orçamento?')) return;
    fetch(`/api/orcamentos/${id}`, { method: 'DELETE' })
        .then(async r => {
            if (!r.ok) {
                const msg = await r.text();
                throw new Error(msg || "Erro ao excluir orçamento.");
            }
            mostrarMensagem('Orçamento excluído com sucesso!', "sucesso");
            carregarOrcamentos();
        })
        .catch(err => mostrarMensagem(err.message));
}

window.editarOrcamento = editarOrcamento;
window.excluirOrcamento = excluirOrcamento;