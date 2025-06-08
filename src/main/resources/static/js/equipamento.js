const apiBase = '/api';

let editingId = null;

window.onload = () => {
    carregarClientes();
    carregarDonos();
    loadEquipamentos();

    document.getElementById('equipamentoForm').onsubmit = handleFormSubmit;
    document.getElementById('reset-btn').onclick = handleFormReset;
};

// Carrega clientes para o select
function carregarClientes() {
    fetch(`${apiBase}/clientes`)
        .then(response => response.json())
        .then(clientes => {
            const select = document.getElementById('cliente');
            select.innerHTML = '<option value="">Selecione...</option>';
            clientes.forEach(cliente => {
                const opt = document.createElement('option');
                opt.value = cliente.id;
                opt.textContent = cliente.nome;
                select.appendChild(opt);
            });
        })
        .catch(() => {
            const select = document.getElementById('cliente');
            select.innerHTML = '<option value="">Erro ao carregar</option>';
        });
}

// Carrega donos para o select
function carregarDonos() {
    fetch(`${apiBase}/donos`)
        .then(response => response.json())
        .then(donos => {
            const select = document.getElementById('dono');
            select.innerHTML = '<option value="">Selecione...</option>';
            donos.forEach(dono => {
                const opt = document.createElement('option');
                opt.value = dono.id;
                opt.textContent = dono.nome;
                select.appendChild(opt);
            });
        })
        .catch(() => {
            const select = document.getElementById('dono');
            select.innerHTML = '<option value="">Erro ao carregar</option>';
        });
}

function loadEquipamentos() {
    fetch(`${apiBase}/equipamentos`)
        .then(res => res.json())
        .then(data => buildEquipamentoTable(data));
}

function buildEquipamentoTable(equipamentos) {
    const tbody = document.querySelector('#equipamentoTable tbody');
    tbody.innerHTML = '';
    if (equipamentos.length === 0) {
        const tr = document.createElement('tr');
        const td = document.createElement('td');
        td.colSpan = 7;
        td.textContent = 'Nenhum equipamento cadastrado.';
        tr.appendChild(td);
        tbody.appendChild(tr);
        return;
    }
    equipamentos.forEach(equip => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${equip.nome}</td>
            <td>${equip.descricao || '-'}</td>
            <td>R$ ${equip.valorLocacao?.toFixed(2) ?? '-'}</td>
            <td>${equip.disponibilidade ? 'Sim' : 'Não'}</td>
            <td>${equip.cliente ? equip.cliente.nome : '-'}</td>
            <td>${equip.dono ? equip.dono.nome : '-'}</td>
            <td>
                <button class="action-btn" title="Editar" onclick="editEquipamento(${equip.id})">&#9998;</button>
                <button class="action-btn delete" title="Excluir" onclick="deleteEquipamento(${equip.id})">&#10005;</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function exibirAlerta(msg, positivo = false) {
    const alerta = document.getElementById('alerta-erro');
    alerta.textContent = msg;
    alerta.style.display = 'block';
    alerta.style.background = positivo ? '#4CAF50' : '#d9534f';
    alerta.style.color = '#fff';
    window.scrollTo({top: 0, behavior: 'smooth'});
}

function esconderAlerta() {
    const alerta = document.getElementById('alerta-erro');
    alerta.style.display = 'none';
}

function handleFormSubmit(e) {
    e.preventDefault();
    esconderAlerta();

    const nome = document.getElementById('nome').value.trim();
    const descricao = document.getElementById('descricao').value.trim();
    const valorLocacao = document.getElementById('valorLocacao').value;
    const disponibilidade = document.getElementById('disponibilidade').value === "true";
    const clienteId = document.getElementById('cliente').value;
    const donoId = document.getElementById('dono').value;

    if (!nome || !valorLocacao || !clienteId || !donoId) {
        exibirAlerta('Preencha todos os campos obrigatórios!');
        return;
    }

    const equipamento = {
        nome,
        descricao,
        valorLocacao: parseFloat(valorLocacao),
        disponibilidade,
        clienteId: parseInt(clienteId),
        donoId: parseInt(donoId)
    };

    let url = `${apiBase}/equipamentos`;
    let method = 'POST';

    // Edição
    if (editingId) {
        url += `/${editingId}`;
        method = 'PUT';
    }

    fetch(url, {
        method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(equipamento)
    })
    .then(async resp => {
        esconderAlerta();
        if (resp.ok) {
            exibirAlerta('Equipamento salvo com sucesso!', true);
            setTimeout(() => {
                esconderAlerta();
                handleFormReset();
                loadEquipamentos();
            }, 1500);
        } else {
            let msg = 'Erro ao salvar.';
            try {
                const data = await resp.json();
                if (data && data.mensagem) {
                    // Mensagens técnicas do back-end são traduzidas para o usuário
                    if (data.mensagem.includes('Já existe um equipamento cadastrado com este nome')) {
                        msg = 'Já existe um equipamento com este nome. Escolha outro nome para continuar.';
                    } else if (data.mensagem.includes('Cliente não encontrado')) {
                        msg = 'Cliente não encontrado. Selecione um cliente válido.';
                    } else if (data.mensagem.includes('Dono não encontrado')) {
                        msg = 'Dono não encontrado. Selecione um dono válido.';
                    } else if (data.mensagem.includes('Equipamento não encontrado')) {
                        msg = 'Equipamento não encontrado.';
                    } else {
                        msg = 'Erro ao salvar: ' + data.mensagem;
                    }
                }
            } catch (e) {
                msg = 'Erro ao salvar.';
            }
            exibirAlerta(msg);
        }
    });
}

window.editEquipamento = function(id) {
    fetch(`${apiBase}/equipamentos/${id}`)
        .then(res => res.json())
        .then(equip => {
            editingId = id;
            document.getElementById('form-title').textContent = "Editar Equipamento";
            document.getElementById('submit-btn').textContent = "Atualizar";
            document.getElementById('id').value = equip.id;
            document.getElementById('nome').value = equip.nome;
            document.getElementById('descricao').value = equip.descricao || '';
            document.getElementById('valorLocacao').value = equip.valorLocacao ?? '';
            document.getElementById('disponibilidade').value = equip.disponibilidade ? "true" : "false";
            document.getElementById('cliente').value = equip.cliente ? equip.cliente.id : '';
            document.getElementById('dono').value = equip.dono ? equip.dono.id : '';
            window.scrollTo({top:0, behavior:'smooth'});
        });
};

window.deleteEquipamento = function(id) {
    if (confirm('Deseja realmente excluir este equipamento?')) {
        fetch(`${apiBase}/equipamentos/${id}`, { method: 'DELETE' })
            .then(async resp => {
                esconderAlerta();
                if (resp.ok) {
                    exibirAlerta('Equipamento excluído!', true);
                    setTimeout(() => {
                        esconderAlerta();
                        loadEquipamentos();
                        if (editingId == id) handleFormReset();
                    }, 1500);
                } else {
                    let msg = 'Erro ao excluir equipamento.';
                    try {
                        const data = await resp.json();
                        if (data && data.mensagem) {
                            if (data.mensagem.includes('vinculado a um contrato de locação')) {
                                msg = 'Não é possível excluir este equipamento pois ele está vinculado a um contrato de locação.';
                            } else {
                                msg = data.mensagem;
                            }
                        }
                    } catch (e) {}
                    exibirAlerta(msg);
                }
            });
    }
};

function handleFormReset() {
    editingId = null;
    document.getElementById('form-title').textContent = "Cadastrar Equipamento para Locação";
    document.getElementById('submit-btn').textContent = "Salvar";
    document.getElementById('equipamentoForm').reset();
    document.getElementById('id').value = '';
    esconderAlerta();
}

