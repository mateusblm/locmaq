function exibirMensagem(msg) {
    let div = document.getElementById("mensagem-erro");
    div.innerText = msg;
    div.style.display = "block";
    div.style.background = "#ffe0e0";
    div.style.color = "#a00";
    div.style.border = "1px solid #f99";
    setTimeout(() => { div.style.display = "none"; }, 5000);
}

// Formata data para DD/MM/YYYY HH:mm:ss
function formatarDataHoraBR(iso) {
    if (!iso) return "";
    const d = new Date(iso);
    const dia = String(d.getDate()).padStart(2, '0');
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const ano = d.getFullYear();
    const hora = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    const seg = String(d.getSeconds()).padStart(2, '0');
    return `${dia}/${mes}/${ano} ${hora}:${min}:${seg}`;
}

function carregarLogs() {
    fetch('/api/logs')
        .then(response => response.json())
        .then(logs => {
            const tbody = document.querySelector('#logsTable tbody');
            tbody.innerHTML = "";
            logs.forEach(log => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${log.id}</td>
                    <td>${log.action}</td>
                    <td>${log.user}</td>
                    <td>${formatarDataHoraBR(log.timestamp)}</td>
                    <td>
                      <button class="visualizar-btn" onclick="showDetails(${log.id})">Detalhes</button>
                    </td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(() => exibirMensagem("Erro de conexão: não foi possível acessar o servidor. Verifique sua conexão ou tente novamente mais tarde."));
}

window.showDetails = function(logId) {
    fetch('/api/logs/' + logId)
        .then(response => response.json())
        .then(log => {
            const body = document.getElementById('modalBody');
            body.innerHTML = `
                <strong>ID:</strong> ${log.id}<br>
                <strong>Ação:</strong> ${log.action}<br>
                <strong>Usuário:</strong> ${log.user}<br>
                <strong>Data e Hora:</strong> ${formatarDataHoraBR(log.timestamp)}<br>
                <strong>Detalhes:</strong><br>
                <pre>${log.details || 'Nenhum detalhe extra.'}</pre>
            `;
            document.getElementById('detailsModal').style.display = 'block';
        })
        .catch(() => exibirMensagem("Erro de conexão: não foi possível acessar o servidor. Verifique sua conexão ou tente novamente mais tarde."));
};

document.getElementById('closeModal').onclick = function() {
    document.getElementById('detailsModal').style.display = 'none';
};
window.onclick = function(event) {
    if (event.target === document.getElementById('detailsModal')) {
        document.getElementById('detailsModal').style.display = 'none';
    }
};

document.addEventListener("DOMContentLoaded", carregarLogs);