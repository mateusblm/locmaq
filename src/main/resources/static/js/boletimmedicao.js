document.addEventListener("DOMContentLoaded", () => {
    fetch('/api/usuarios?tipo=PLANEJADOR')
        .then(r => r.json())
        .then(data => {
            const sel = document.getElementById("planejadorSelect");
            sel.innerHTML = data.map(p => `<option value="${p.id}">${p.nome}</option>`).join('');
        });

    fetch('/api/equipamentos')
        .then(r => r.json())
        .then(eqs => window.equipamentos = eqs);

    document.getElementById("boletimForm").onsubmit = handleFormSubmit;

    carregarBoletins();
});

function addEquipamento() {
    const div = document.createElement('div');
    div.className = 'equipamento-item';
    div.innerHTML = `
        <select name="equipamentoId" required>
            ${window.equipamentos.map(eq => `<option value="${eq.id}">${eq.nome}</option>`).join('')}
        </select>
        <input type="number" name="quantidade" required min="1" placeholder="Quantidade">
        <input type="number" name="valorMedido" required step="0.01" min="0" placeholder="Valor medido">
        <input type="text" name="observacao" placeholder="Observação">
        <button type="button" onclick="this.parentElement.remove()">Remover</button>
    `;
    document.getElementById('equipamentos-list').appendChild(div);
}

function handleFormSubmit(e) {
    e.preventDefault();
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
        alert("Adicione ao menos um equipamento para salvar!");
        return;
    }

    const body = {
        dataInicio: form.dataInicio.value,
        dataFim: form.dataFim.value,
        planejadorId: form.planejadorId.value,
        situacao: form.situacao.value,
        equipamentos
    };

    console.log("Payload enviado:", JSON.stringify(body, null, 2));

    fetch('/api/boletins', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    }).then(async r => {
        if (r.ok) {
            alert('Boletim cadastrado!');
            form.reset();
            document.getElementById('equipamentos-list').innerHTML = '';
            carregarBoletins();
        } else {
            const err = await r.text();
            alert('Erro ao cadastrar!\n' + err);
        }
    });
}

function carregarBoletins() {
    fetch('/api/boletins')
        .then(r => r.json())
        .then(boletins => {
            const tbody = document.getElementById("tabelaBoletins");
            if (!Array.isArray(boletins) || boletins.length === 0) {
                tbody.innerHTML = `<tr><td colspan="5" style="text-align:center">Nenhum boletim cadastrado</td></tr>`;
                return;
            }
            tbody.innerHTML = boletins.map(b => `
                <tr>
                    <td>${b.dataInicio} a ${b.dataFim}</td>
                    <td>${b.planejadorNome || "-"}</td>
                    <td>${b.situacao}</td>
                    <td>${b.assinado ? 'Sim' : 'Não'}</td>
                    <td>
                        <button class="visualizar-btn" onclick="abrirDetalhe(${b.id})">Visualizar</button>
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
            document.getElementById('dataInicioDetalhe').value = b.dataInicio;
            document.getElementById('dataFimDetalhe').value = b.dataFim;
            document.getElementById('planejadorDetalhe').value = b.planejadorNome || '-';
            document.getElementById('situacaoDetalhe').value = b.situacao;
            window.boletimDetalheId = b.id;
        });
};

function assinarBoletim(id) {
    fetch(`/api/boletins/${id}/assinar`, { method: 'POST' })
        .then(r => r.ok ? carregarBoletins() : alert('Erro ao assinar!'));
}

function fecharDetalhe() {
    document.getElementById('boletimDetailSection').style.display = "none";
}