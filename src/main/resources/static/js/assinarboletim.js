document.addEventListener("DOMContentLoaded", carregarBoletins);

function carregarBoletins() {
    fetch('/api/boletins')
        .then(r => r.json())
        .then(lista => {
            const tb = document.getElementById("tabelaAssinaturaBoletins");
            tb.innerHTML = lista.map(b => `
                <tr>
                    <td>${b.id}</td>
                    <td>${b.periodo}</td>
                    <td>${b.planejadorNome || '-'}</td>
                    <td>${b.situacao}</td>
                    <td>
                        <button class="visualizar-btn"onclick="abrirDetalhe(${b.id})">Visualizar</button>
                        ${!b.assinado ? `<button class="visualizar-btn" onclick="assinar(${b.id})">Assinar</button>` : ''}
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
            document.getElementById('periodoDetalhe').value = b.periodo;
            document.getElementById('planejadorDetalhe').value = b.planejadorNome || '-';
            document.getElementById('situacaoDetalhe').value = b.situacao;
            window.boletimDetalheId = b.id;
        });
};

document.getElementById("assinarBtn").onclick = function () {
    if (window.boletimDetalheId) assinar(window.boletimDetalheId);
};

document.getElementById("cancelarDetalheBtn").onclick = function () {
    document.getElementById('boletimDetailSection').style.display = "none";
    window.boletimDetalheId = null;
};

window.assinar = function(id) {
    fetch(`/api/boletins/${id}/assinar`, { method: 'POST' })
        .then(r => {
            if (r.ok) {
                alert('Assinado!');
                carregarBoletins();
                document.getElementById('boletimDetailSection').style.display = "none";
            } else {
                alert('Erro ao assinar!');
            }
        });
}