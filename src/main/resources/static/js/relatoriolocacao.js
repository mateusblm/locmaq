function formatarDataBR(dataIso) {
    if (!dataIso) return '-';
    const d = new Date(dataIso);
    if (isNaN(d)) return dataIso;
    const dia = String(d.getDate()).padStart(2, '0');
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const ano = d.getFullYear();
    return `${dia}/${mes}/${ano}`;
}

async function carregarFiltros() {
    // Carregar clientes e equipamentos únicos
    const contratos = await fetch('/api/contrato-locacoes').then(r => r.json());
    const clientesUnicos = [];
    const equipamentosUnicos = [];
    const clientesSet = new Set();
    const equipamentosSet = new Set();

    contratos.forEach(c => {
        if (c.clienteNome && !clientesSet.has(c.clienteNome)) {
            clientesUnicos.push(c.clienteNome);
            clientesSet.add(c.clienteNome);
        }
        if (c.equipamentoNome && !equipamentosSet.has(c.equipamentoNome)) {
            equipamentosUnicos.push(c.equipamentoNome);
            equipamentosSet.add(c.equipamentoNome);
        }
    });

    const clienteSelect = document.getElementById('filtroCliente');
    clienteSelect.innerHTML = '<option value="">Todos</option>';
    clientesUnicos.forEach(nome => {
        const option = document.createElement('option');
        option.value = nome;
        option.textContent = nome;
        clienteSelect.appendChild(option);
    });

    const eqSelect = document.getElementById('filtroEquipamento');
    eqSelect.innerHTML = '<option value="">Todos</option>';
    equipamentosUnicos.forEach(nome => {
        const option = document.createElement('option');
        option.value = nome;
        option.textContent = nome;
        eqSelect.appendChild(option);
    });
}

async function aplicarFiltros() {
    const contratos = await fetch('/api/contrato-locacoes').then(r => r.json());
    const tbody = document.getElementById('tabelaContratos');
    tbody.innerHTML = '';

    const filtros = {
        dataInicio: document.getElementById('filtroDataInicio').value,
        dataFim: document.getElementById('filtroDataFim').value,
        cliente: document.getElementById('filtroCliente').value,
        equipamento: document.getElementById('filtroEquipamento').value,
        status: document.getElementById('filtroStatus').value,
    };

    const contratosFiltrados = contratos.filter(c => {
        const cDataInicio = c.dataInicio ? new Date(c.dataInicio + 'T00:00:00') : null;
        const cDataFim = c.dataFim ? new Date(c.dataFim + 'T00:00:00') : null;
        const filtroDataInicio = filtros.dataInicio ? new Date(filtros.dataInicio + 'T00:00:00') : null;
        const filtroDataFim = filtros.dataFim ? new Date(filtros.dataFim + 'T00:00:00') : null;

        const dentroPeriodo =
            (!filtroDataInicio || (cDataInicio && cDataInicio >= filtroDataInicio)) &&
            (!filtroDataFim || (cDataFim && cDataFim <= filtroDataFim));

        const porCliente = !filtros.cliente || c.clienteNome === filtros.cliente;
        const porEquipamento = !filtros.equipamento || c.equipamentoNome === filtros.equipamento;
        const porStatus = !filtros.status || c.statusPagamento === filtros.status;

        return dentroPeriodo && porCliente && porEquipamento && porStatus;
    });

    contratosFiltrados.forEach(c => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${c.clienteNome}</td>
            <td>${c.equipamentoNome}</td>
            <td>${formatarDataBR(c.dataInicio)}</td>
            <td>${formatarDataBR(c.dataFim)}</td>
            <td>R$ ${c.valorTotal != null ? c.valorTotal.toLocaleString('pt-BR', {minimumFractionDigits:2}) : '0,00'}</td>
            <td>${c.statusPagamento}</td>
            <td><button class="visualizar-btn" onclick="verDetalhe(${c.id})">Ver</button></td>
        `;
        tbody.appendChild(tr);
    });

    if (contratosFiltrados.length === 0) {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td colspan="7" class="sem-dados">
            Nenhum contrato encontrado para os filtros selecionados.
        </td>`;
        tbody.appendChild(tr);
    }
}

function resetarFiltros() {
    document.querySelectorAll('.filtro-group input, .filtro-group select').forEach(el => el.value = '');
    aplicarFiltros();
}

async function verDetalhe(id) {
    const resp = await fetch(`/api/contrato-locacoes/${id}`);
    if (!resp.ok) {
        alert('Erro ao buscar detalhes do contrato.');
        return;
    }
    const c = await resp.json();
    const conteudo = `
        <h3>Detalhes do Contrato</h3>
        <p><b>Cliente:</b> ${c.clienteNome}</p>
        <p><b>Equipamento:</b> ${c.equipamentoNome}</p>
        <p><b>Período:</b> ${formatarDataBR(c.dataInicio)} a ${formatarDataBR(c.dataFim)}</p>
        <p><b>Valor Total:</b> R$ ${c.valorTotal != null ? c.valorTotal.toLocaleString('pt-BR', {minimumFractionDigits:2}) : '0,00'}</p>
        <p><b>Status Pagamento:</b> ${c.statusPagamento}</p>
        <p><b>Outros detalhes:</b> ${c.observacoes || '-'}</p>
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