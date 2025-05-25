function exibirMensagem(msg, tipo = "erro") {
    let div = document.getElementById("mensagem-erro");
    div.innerText = msg;
    div.style.display = "block";
    div.style.background = tipo === "sucesso" ? "#e0ffe0" : "#ffe0e0";
    div.style.color = tipo === "sucesso" ? "#0a0" : "#a00";
    div.style.border = tipo === "sucesso" ? "1px solid #9f9" : "1px solid #f99";
    setTimeout(() => { div.style.display = "none"; }, 5000);
}

// Converte yyyy-MM-dd para dd/MM/yyyy
function formatarDataParaBR(dataISO) {
    if (!dataISO) return "";
    const [ano, mes, dia] = dataISO.split("-");
    return `${dia}/${mes}/${ano}`;
}

// Converte dd/MM/yyyy para yyyy-MM-dd
function formatarDataParaISO(dataBR) {
    if (!dataBR) return "";
    const [dia, mes, ano] = dataBR.split("/");
    return `${ano}-${mes}-${dia}`;
}

function formatarPeriodo(dataInicio, dataFim) {
    if (!dataInicio && !dataFim) return "-";
    if (dataInicio && dataFim) return `${dataInicio} a ${dataFim}`;
    return dataInicio || dataFim || "-";
}

function carregarBoletins() {
    fetch('/api/boletins')
        .then(r => r.json())
        .then(lista => {
            const tb = document.getElementById("tabelaAssinaturaBoletins");
            tb.innerHTML = lista.map(b => `
                <tr>
                    <td>${b.id}</td>
                    <td>${formatarPeriodo(b.dataInicio, b.dataFim)}</td>
                    <td>${b.planejadorNome || '-'}</td>
                    <td>${b.situacao}</td>
                    <td>
                        <button class="visualizar-btn" onclick="abrirDetalhe(${b.id}, false)">Visualizar</button>
                        ${!b.assinado ? `
                            <button class="visualizar-btn delete" onclick="removerBoletim(${b.id})">Remover</button>
                            <button class="visualizar-btn" onclick="assinar(${b.id})">Assinar</button>
                        ` : ''}
                    </td>
                </tr>
            `).join('');
        })
        .catch(() => exibirMensagem("Erro de conexão: não foi possível acessar o servidor. Verifique sua conexão ou tente novamente mais tarde."));
}

window.abrirDetalhe = function(id) {
    fetch(`/api/boletins/${id}`)
        .then(r => r.json())
        .then(b => {
            document.getElementById('boletimDetailSection').style.display = "block";
            document.getElementById('boletimIdDetalhe').value = b.id;
            document.getElementById('dataInicioDetalhe').value = formatarDataParaISO(b.dataInicio);
            document.getElementById('dataFimDetalhe').value = formatarDataParaISO(b.dataFim);
            document.getElementById('planejadorDetalhe').value = b.planejadorNome || '-';
            document.getElementById('situacaoDetalhe').value = b.situacao || "";

            document.getElementById('dataInicioDetalhe').readOnly = true;
            document.getElementById('dataFimDetalhe').readOnly = true;
            document.getElementById('situacaoDetalhe').readOnly = true;
            document.getElementById('dataInicioDetalhe').disabled = true;
            document.getElementById('dataFimDetalhe').disabled = true;
            document.getElementById('situacaoDetalhe').disabled = true;

            // Renderiza equipamentos
            const eqDiv = document.getElementById('equipamentosDetalhe');
            eqDiv.innerHTML = "<h4>Equipamentos</h4>";
            if (b.equipamentos && b.equipamentos.length) {
                eqDiv.innerHTML += b.equipamentos.map((eq, idx) => `
                    <div style="margin-bottom:8px;display:flex;gap:8px;align-items:center;">
                        <label style="min-width:100px;">${eq.equipamentoNome}</label>
                        <input type="number" id="valorMedido_${idx}" value="${eq.valorMedido || 0}" readonly style="width:120px;">
                        <input type="hidden" id="equipamentoId_${idx}" value="${eq.equipamentoId}">
                    </div>
                `).join('');
            } else {
                eqDiv.innerHTML += "<div>Nenhum equipamento vinculado.</div>";
            }

            document.getElementById('assinarBtn').style.display = !b.assinado ? "inline-block" : "none";
            window.boletimDetalheId = b.id;
            window.equipamentosDetalhe = b.equipamentos || [];
        })
        .catch(() => exibirMensagem("Erro de conexão: não foi possível acessar o servidor. Verifique sua conexão ou tente novamente mais tarde."));
};

window.removerBoletim = function(id) {
    if (confirm("Deseja remover este boletim?")) {
        fetch(`/api/boletins/${id}`, { method: "DELETE" })
            .then(async response => {
                const msg = await response.text();
                if (response.ok) {
                    exibirMensagem(msg || "Boletim removido com sucesso!", "sucesso");
                    carregarBoletins();
                } else {
                    exibirMensagem(msg || "Erro ao remover boletim.");
                }
            })
            .catch(() => exibirMensagem("Erro de conexão: não foi possível acessar o servidor. Verifique sua conexão ou tente novamente mais tarde."));
    }
};

window.assinar = function(id) {
    fetch(`/api/boletins/${id}/assinar`, { method: 'POST' })
        .then(async r => {
            if (r.ok) {
                exibirMensagem('Boletim assinado com sucesso!', "sucesso");
                carregarBoletins();
                document.getElementById('boletimDetailSection').style.display = "none";
            } else {
                const msg = await r.text();
                exibirMensagem(msg || 'Erro ao assinar boletim.');
            }
        })
        .catch(() => exibirMensagem("Erro de conexão: não foi possível acessar o servidor. Verifique sua conexão ou tente novamente mais tarde."));
};

document.getElementById("assinarBtn").onclick = function () {
    if (window.boletimDetalheId) assinar(window.boletimDetalheId);
};

document.getElementById("cancelarDetalheBtn").onclick = function () {
    document.getElementById('boletimDetailSection').style.display = "none";
    window.boletimDetalheId = null;
};

//document.getElementById("salvarEdicaoBtn").onclick = function () {
//    const id = window.boletimDetalheId;
//    // Converte para dd/MM/yyyy antes de enviar
//    const dataInicio = formatarDataParaBR(document.getElementById('dataInicioDetalhe').value);
//    const dataFim = formatarDataParaBR(document.getElementById('dataFimDetalhe').value);
//    const situacao = document.getElementById('situacaoDetalhe').value;
//    const equipamentos = (window.equipamentosDetalhe || []).map((eq, idx) => ({
//    id: eq.id, // adicione esta linha
//    equipamentoId: document.getElementById(`equipamentoId_${idx}`).value,
//    valorMedido: parseFloat(document.getElementById(`valorMedido_${idx}`).value)
//    }));
//
//    fetch(`/api/boletins/${id}`, {
//        method: "PUT",
//        headers: { "Content-Type": "application/json" },
//        body: JSON.stringify({ dataInicio, dataFim, situacao, equipamentos })
//    })
//    .then(async r => {
//        const msg = await r.text();
//        if (r.ok) {
//            exibirMensagem("Boletim editado com sucesso!", "sucesso");
//            carregarBoletins();
//            document.getElementById('boletimDetailSection').style.display = "none";
//        } else {
//            exibirMensagem(msg || "Erro ao editar boletim.");
//        }
//    });
//};

document.addEventListener("DOMContentLoaded", carregarBoletins);