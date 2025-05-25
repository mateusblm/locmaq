   let contratosMap = {};
    let orcamentos = [];

    function carregarContratos() {
        return fetch('/api/contrato-locacoes')
            .then(r => r.json())
            .then(data => {
                contratosMap = {};
                data.forEach(c => contratosMap[c.id] = c);
            });
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
                            <td>${o.status}</td>
                            <td>${o.aprovadoPor || '-'}</td>
                            <td>
                                <button class="visualizar-btn" onclick="mostrarDetalhes(${o.id})">Detalhes</button>
                                ${o.status === 'PENDENTE' ? `
                                    <button class="visualizar-btn" onclick="aprovarOrcamento(${o.id})">Aprovar</button>
                                    <button class="acao-btn delete" onclick="rejeitarOrcamento(${o.id})">Rejeitar</button>
                                ` : ''}
                            </td>
                        </tr>
                    `;
                });
            });
    }

    function mostrarDetalhes(id) {
        const o = orcamentos.find(x => x.id === id);
        const contrato = contratosMap[o.contrato?.id] || {};
        const div = document.getElementById('detalhesOrcamento');
        div.innerHTML = `
            <b>Cliente:</b> ${contrato.clienteNome || '-'}<br>
            <b>Equipamento:</b> ${contrato.equipamentoNome || '-'}<br>
            <b>Período Contrato:</b> ${contrato.dataInicio || '-'} a ${contrato.dataFim || '-'}<br>
            <b>Dias Trabalhados:</b> ${o.diasTrabalhados}<br>
            <b>Desconto:</b> R$ ${o.desconto != null ? o.desconto.toLocaleString('pt-BR', {minimumFractionDigits:2}) : '0,00'}<br>
            <b>Valor Total:</b> R$ ${o.valorTotal != null ? o.valorTotal.toLocaleString('pt-BR', {minimumFractionDigits:2}) : '0,00'}<br>
            <b>Status:</b> ${o.status}<br>
            <b>Aprovado Por:</b> ${o.aprovadoPor || '-'}<br>
            <b>Data Criação:</b> ${o.dataCriacao || '-'}
        `;
        document.getElementById('modalDetalhes').style.display = 'flex';
    }

    function fecharModal() {
        document.getElementById('modalDetalhes').style.display = 'none';
    }

    function aprovarOrcamento(id) {
        const gestor = prompt("Digite seu nome para aprovar:");
        if (!gestor) return;
        fetch(`/api/orcamentos/${id}/aprovar?gestor=${encodeURIComponent(gestor)}`, { method: 'POST' })
            .then(r => {
                if (!r.ok) throw new Error('Erro ao aprovar orçamento');
                carregarOrcamentos();
            })
            .catch(err => exibirErro(err.message));
    }

    function rejeitarOrcamento(id) {
        const gestor = prompt("Digite seu nome para rejeitar:");
        if (!gestor) return;
        fetch(`/api/orcamentos/${id}/rejeitar?gestor=${encodeURIComponent(gestor)}`, { method: 'POST' })
            .then(r => {
                if (!r.ok) throw new Error('Erro ao rejeitar orçamento');
                carregarOrcamentos();
            })
            .catch(err => exibirErro(err.message));
    }

    function exibirErro(msg) {
        const div = document.getElementById('mensagem-erro');
        div.style.display = 'block';
        div.textContent = msg;
        setTimeout(() => { div.style.display = 'none'; }, 4000);
    }

    document.addEventListener('DOMContentLoaded', () => {
        carregarContratos().then(carregarOrcamentos);
    });

    window.mostrarDetalhes = mostrarDetalhes;
    window.fecharModal = fecharModal;
    window.aprovarOrcamento = aprovarOrcamento;
    window.rejeitarOrcamento = rejeitarOrcamento;