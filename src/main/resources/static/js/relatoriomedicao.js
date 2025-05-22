async function carregarFiltros() {
    // Preencher planejadores e equipamentos
    const planejadores = await fetch('/api/usuarios').then(r => r.json());
    const equipamentos = await fetch('/api/equipamentos').then(r => r.json());

    const plannerSelect = document.getElementById('filtroPlanejador');
    planejadores.forEach(p => {
        const option = document.createElement('option');
        option.value = p.id;
        option.textContent = p.nome;
        plannerSelect.appendChild(option);
    });

    const eqSelect = document.getElementById('filtroEquipamento');
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
        const dentroPeriodo =
            (!filtros.dataInicio || b.dataInicio >= filtros.dataInicio) &&
            (!filtros.dataFim || b.dataFim <= filtros.dataFim);
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
            <td>${b.equipamentos.map(e => e.nomeEquipamento || e.equipamentoId).join(', ')}</td>
            <td><button onclick="verDetalhe(${b.id})">Ver</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function resetarFiltros() {
    document.querySelectorAll('.filtro-group input, .filtro-group select').forEach(el => el.value = '');
    aplicarFiltros();
}

function verDetalhe(id) {
    // Abre modal ou redireciona para página de detalhes
    alert("Ver detalhe do boletim ID: " + id);
}

window.onload = () => {
    carregarFiltros();
    aplicarFiltros();
};
