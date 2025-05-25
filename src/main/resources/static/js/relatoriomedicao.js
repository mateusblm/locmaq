function parseDateBR(str) {
    if (!str) return null;
    const [dia, mes, ano] = str.split('/');
    if (!dia || !mes || !ano) return null;
    return new Date(`${ano}-${mes}-${dia}T00:00:00`);
}

async function carregarFiltros() {
    // Planejadores únicos dos boletins
    const boletins = await fetch('/api/boletins').then(r => r.json());
    const planejadoresUnicos = [];
    const ids = new Set();
    boletins.forEach(b => {
        if (b.planejadorId && !ids.has(b.planejadorId)) {
            planejadoresUnicos.push({ id: b.planejadorId, nome: b.planejadorNome });
            ids.add(b.planejadorId);
        }
    });

    const plannerSelect = document.getElementById('filtroPlanejador');
    plannerSelect.innerHTML = '<option value="">Todos</option>';
    planejadoresUnicos.forEach(p => {
        const option = document.createElement('option');
        option.value = p.id;
        option.textContent = p.nome;
        plannerSelect.appendChild(option);
    });

    // Equipamentos
    const equipamentos = await fetch('/api/equipamentos').then(r => r.json());
    const eqSelect = document.getElementById('filtroEquipamento');
    eqSelect.innerHTML = '<option value="">Todos</option>';
    equipamentos.forEach(eq => {
        const option = document.createElement('option');
        option.value = eq.id;
        option.textContent = eq.nome;
        eqSelect.appendChild(option);
    });
}

async function aplicarFiltros() {
    const boletins = await fetch('/api/boletins').then(r => r.json());
    const tbody = document.getElementById('tabelaRelatorios');
    tbody.innerHTML = '';

    const filtros = {
        dataInicio: document.getElementById('filtroDataInicio').value,
        dataFim: document.getElementById('filtroDataFim').value,
        valorMin: parseFloat(document.getElementById('filtroValorMin').value),
        valorMax: parseFloat(document.getElementById('filtroValorMax').value),
        equipamentoId: document.getElementById('filtroEquipamento').value,
        planejadorId: document.getElementById('filtroPlanejador').value,
        situacao: document.getElementById('filtroSituacao').value,
    };

    boletins.filter(b => {
        // Corrigir comparação de datas
        const bDataInicio = parseDateBR(b.dataInicio);
        const bDataFim = parseDateBR(b.dataFim);
        const filtroDataInicio = filtros.dataInicio ? new Date(filtros.dataInicio + 'T00:00:00') : null;
        const filtroDataFim = filtros.dataFim ? new Date(filtros.dataFim + 'T00:00:00') : null;

        const dentroPeriodo =
            (!filtroDataInicio || (bDataInicio && bDataInicio >= filtroDataInicio)) &&
            (!filtroDataFim || (bDataFim && bDataFim <= filtroDataFim));

        const porSituacao = !filtros.situacao || b.situacao === filtros.situacao;
        const porPlanejador = !filtros.planejadorId || b.planejadorId == filtros.planejadorId;
        const contemEquipamento = !filtros.equipamentoId || b.equipamentos.some(e => e.equipamentoId == filtros.equipamentoId);
        const dentroValor = b.equipamentos.every(e => {
            const v = e.valorMedido ?? 0;
            return (!filtros.valorMin || v >= filtros.valorMin) && (!filtros.valorMax || v <= filtros.valorMax);
        });
        return dentroPeriodo && porSituacao && porPlanejador && contemEquipamento && dentroValor;
    }).forEach(b => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${b.dataInicio} a ${b.dataFim}</td>
            <td>${b.planejadorNome}</td>
            <td>${b.situacao}</td>
            <td>${b.assinado ? 'Sim' : 'Não'}</td>
            <td>${b.equipamentos.map(e => e.equipamentoNome || e.equipamentoId).join(', ')}</td>
            <td><button onclick="verDetalhe(${b.id})">Ver</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function resetarFiltros() {
    document.querySelectorAll('.filtro-group input, .filtro-group select').forEach(el => el.value = '');
    aplicarFiltros();
}

async function verDetalhe(id) {
    const resp = await fetch(`/api/boletins/${id}`);
    if (!resp.ok) {
        alert('Erro ao buscar detalhes do boletim.');
        return;
    }
    const b = await resp.json();
    const conteudo = `
        <h3>Detalhes do Boletim</h3>
        <p><b>Período:</b> ${b.dataInicio} a ${b.dataFim}</p>
        <p><b>Planejador:</b> ${b.planejadorNome}</p>
        <p><b>Situação:</b> ${b.situacao}</p>
        <p><b>Assinado:</b> ${b.assinado ? 'Sim' : 'Não'}</p>
        <p><b>Equipamentos:</b></p>
        <ul>
            ${b.equipamentos.map(e => `
                <li>
                    <b>Nome:</b> ${e.equipamentoNome || e.equipamentoId} <br>
                    <b>Valor Medido:</b> ${e.valorMedido ?? '-'}
                </li>
            `).join('')}
        </ul>
    `;
    document.getElementById('modalDetalheConteudo').innerHTML = conteudo;
    document.getElementById('modalDetalhe').style.display = 'flex';
}

function fecharModal() {
    document.getElementById('modalDetalhe').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('modalDetalhe');
    if (event.target === modal) fecharModal();
};

window.onload = () => {
    carregarFiltros();
    aplicarFiltros();
};