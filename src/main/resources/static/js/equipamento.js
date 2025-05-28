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

function handleFormSubmit(e) {
    e.preventDefault();

    const nome = document.getElementById('nome').value.trim();
    const descricao = document.getElementById('descricao').value.trim();
    const valorLocacao = document.getElementById('valorLocacao').value;
    const disponibilidade = document.getElementById('disponibilidade').value === "true";
    const clienteId = document.getElementById('cliente').value;
    const donoId = document.getElementById('dono').value;

    if (!nome || !valorLocacao || !clienteId || !donoId) {
        alert('Preencha todos os campos obrigatórios!');
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
    .then(resp => {
        if (resp.ok) {
            alert('Equipamento salvo com sucesso!');
            handleFormReset();
            loadEquipamentos();
        } else {
            resp.text().then(t => alert('Erro ao salvar: ' + t));
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
            .then(resp => {
                if (resp.ok) {
                    alert('Equipamento excluído!');
                    loadEquipamentos();
                    if (editingId == id) handleFormReset();
                }
                else alert('Erro ao excluir equipamento!');
            });
    }
};

function handleFormReset() {
    editingId = null;
    document.getElementById('form-title').textContent = "Cadastrar Equipamento para Locação";
    document.getElementById('submit-btn').textContent = "Salvar";
    document.getElementById('equipamentoForm').reset();
    document.getElementById('id').value = '';
}

//document.getElementById('deletarTratoresBtn').addEventListener('click', function() {
//    if (!confirm('Deseja realmente deletar todos os equipamentos com nome "trator"?')) return;
//    fetch('/api/equipamentos/deletar-trator', { method: 'DELETE' })
//        .then(r => r.text())
//        .then(msg => {
//            alert(msg);
//            // Atualize a lista de equipamentos, se necessário
//            carregarEquipamentos && carregarEquipamentos();
//        })
//        .catch(() => alert('Erro ao deletar tratores.'));
//});