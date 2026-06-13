const apiBase = '/api';

let editingId = null;

window.onload = () => {
    carregarEquipamentos();
    carregarTags();
    carregarLeituras();

    document.getElementById('rfidForm').onsubmit = handleFormSubmit;
    document.getElementById('reset-btn').onclick = handleFormReset;
};

function carregarEquipamentos() {
    fetch(`${apiBase}/equipamentos`)
        .then(response => response.json())
        .then(equipamentos => {
            const select = document.getElementById('equipamento');
            select.innerHTML = '<option value="">Sem equipamento associado</option>';
            equipamentos.forEach(equipamento => {
                const opt = document.createElement('option');
                opt.value = equipamento.id;
                opt.textContent = equipamento.nome;
                select.appendChild(opt);
            });
        })
        .catch(() => {
            document.getElementById('equipamento').innerHTML = '<option value="">Erro ao carregar</option>';
        });
}

function carregarTags() {
    fetch(`${apiBase}/rfid/tags`)
        .then(response => response.json())
        .then(tags => montarTabelaTags(tags));
}

function carregarLeituras() {
    fetch(`${apiBase}/rfid/leituras`)
        .then(response => response.json())
        .then(leituras => montarTabelaLeituras(leituras));
}

function montarTabelaTags(tags) {
    const tbody = document.querySelector('#rfidTagTable tbody');
    tbody.innerHTML = '';

    if (tags.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">Nenhuma tag RFID cadastrada.</td></tr>';
        return;
    }

    tags.forEach(tag => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${tag.uid}</td>
            <td>${tag.descricao || '-'}</td>
            <td>${tag.ativo ? 'Sim' : 'Nao'}</td>
            <td>${tag.equipamento || '-'}</td>
            <td>${formatarData(tag.createdAt)}</td>
            <td>
                <button class="action-btn" title="Editar" onclick="editarTag(${tag.id})">&#9998;</button>
                <button class="action-btn delete" title="Excluir" onclick="excluirTag(${tag.id})">&#10005;</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function montarTabelaLeituras(leituras) {
    const tbody = document.querySelector('#rfidLeituraTable tbody');
    tbody.innerHTML = '';

    if (leituras.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">Nenhuma leitura RFID registrada.</td></tr>';
        return;
    }

    leituras.forEach(leitura => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${formatarData(leitura.dataHora)}</td>
            <td>${leitura.uid}</td>
            <td>${leitura.origem || '-'}</td>
            <td>${leitura.autorizado ? 'Sim' : 'Nao'}</td>
            <td>${leitura.mensagem || '-'}</td>
            <td>${leitura.equipamento || '-'}</td>
        `;
        tbody.appendChild(tr);
    });
}

function handleFormSubmit(e) {
    e.preventDefault();
    esconderAlerta();

    const uid = document.getElementById('uid').value.trim();
    const descricao = document.getElementById('descricao').value.trim();
    const equipamentoId = document.getElementById('equipamento').value;
    const ativo = document.getElementById('ativo').value === 'true';

    if (!uid) {
        exibirAlerta('Informe o UID RFID.');
        return;
    }

    const tag = {
        uid,
        descricao,
        ativo,
        equipamentoId: equipamentoId ? parseInt(equipamentoId) : null
    };

    let url = `${apiBase}/rfid/tags`;
    let method = 'POST';

    if (editingId) {
        url += `/${editingId}`;
        method = 'PUT';
    }

    fetch(url, {
        method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(tag)
    })
    .then(async response => {
        if (response.ok) {
            exibirAlerta('Tag RFID salva com sucesso!', true);
            handleFormReset();
            carregarTags();
            return;
        }

        let mensagem = 'Erro ao salvar tag RFID.';
        try {
            const erro = await response.json();
            if (erro && erro.mensagem) {
                mensagem = erro.mensagem;
            }
        } catch (e) {}
        exibirAlerta(mensagem);
    });
}

window.editarTag = function(id) {
    fetch(`${apiBase}/rfid/tags`)
        .then(response => response.json())
        .then(tags => {
            const tag = tags.find(item => item.id === id);
            if (!tag) {
                exibirAlerta('Tag RFID nao encontrada.');
                return;
            }

            editingId = id;
            document.getElementById('form-title').textContent = 'Editar Tag RFID';
            document.getElementById('submit-btn').textContent = 'Atualizar';
            document.getElementById('id').value = tag.id;
            document.getElementById('uid').value = tag.uid;
            document.getElementById('descricao').value = tag.descricao || '';
            document.getElementById('ativo').value = tag.ativo ? 'true' : 'false';
            document.getElementById('equipamento').value = tag.equipamentoId || '';
            window.scrollTo({top: 0, behavior: 'smooth'});
        });
};

window.excluirTag = function(id) {
    if (!confirm('Deseja realmente excluir esta tag RFID?')) {
        return;
    }

    fetch(`${apiBase}/rfid/tags/${id}`, { method: 'DELETE' })
        .then(async response => {
            if (response.ok) {
                exibirAlerta('Tag RFID excluida!', true);
                carregarTags();
                if (editingId === id) {
                    handleFormReset();
                }
                return;
            }

            let mensagem = 'Erro ao excluir tag RFID.';
            try {
                const erro = await response.json();
                if (erro && erro.mensagem) {
                    mensagem = erro.mensagem;
                }
            } catch (e) {}
            exibirAlerta(mensagem);
        });
};

function handleFormReset() {
    editingId = null;
    document.getElementById('form-title').textContent = 'Cadastrar Tag RFID';
    document.getElementById('submit-btn').textContent = 'Salvar';
    document.getElementById('rfidForm').reset();
    document.getElementById('id').value = '';
    document.getElementById('equipamento').value = '';
}

function formatarData(valor) {
    if (!valor) {
        return '-';
    }
    return new Date(valor).toLocaleString('pt-BR');
}

function exibirAlerta(msg, positivo = false) {
    const alerta = document.getElementById('alerta-erro');
    alerta.textContent = msg;
    alerta.style.display = 'block';
    alerta.style.background = positivo ? '#4CAF50' : '#d9534f';
    alerta.style.color = '#fff';
}

function esconderAlerta() {
    document.getElementById('alerta-erro').style.display = 'none';
}
