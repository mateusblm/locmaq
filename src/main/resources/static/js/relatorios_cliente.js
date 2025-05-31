// Função para exibir mensagem dinâmica
function exibirMensagem(msg, tipo = "erro") {
    let div = document.getElementById("mensagem-status");
    div.innerText = msg;
    div.style.display = "block";
    div.style.background = tipo === "sucesso" ? "#e0ffe0" : "#ffe0e0";
    div.style.color = tipo === "sucesso" ? "#0a0" : "#a00";
    div.style.border = tipo === "sucesso" ? "1px solid #9f9" : "1px solid #f99";
    setTimeout(() => { div.style.display = "none"; }, 5000);
}

// Carregar opções de boletins
fetch('/api/boletins')
    .then(r => r.json())
    .then(lista => {
        const select = document.getElementById('boletimId');
        lista.forEach(b => {
            const opt = document.createElement('option');
            opt.value = b.id;
            opt.text = `Boletim #${b.id} - ${b.dataInicio} a ${b.dataFim}`;
            select.appendChild(opt);
        });
    });

// Carregar opções de contratos
fetch('/api/contrato-locacoes')
    .then(r => r.json())
    .then(lista => {
        const select = document.getElementById('contratoId');
        lista.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.text = `Contrato #${c.id} - ${c.equipamentoNome ?? ''}`;
            select.appendChild(opt);
        });
    });

// --- AUTOCOMPLETE NOME CLIENTE/DONO ---
const nomeInput = document.getElementById('nome');
const emailInput = document.getElementById('email');
let papelInput = document.getElementById('papel');
let sugestoesDiv = document.getElementById('sugestoes-nome');
if (!papelInput) {
    papelInput = document.createElement('input');
    papelInput.type = 'hidden';
    papelInput.id = 'papel';
    papelInput.name = 'papel';
    nomeInput.parentNode.insertBefore(papelInput, nomeInput.nextSibling);
}
if (!sugestoesDiv) {
    sugestoesDiv = document.createElement('div');
    sugestoesDiv.id = 'sugestoes-nome';
    sugestoesDiv.className = 'autocomplete-sugestoes';
    nomeInput.parentNode.insertBefore(sugestoesDiv, nomeInput.nextSibling.nextSibling);
}

nomeInput.addEventListener('input', async function() {
    const termo = nomeInput.value.trim();
    sugestoesDiv.innerHTML = '';
    if (termo.length < 2) return;
    // Busca clientes e donos em paralelo
    const [clientes, donos] = await Promise.all([
        fetch(`/api/clientes/busca?nome=${encodeURIComponent(termo)}`).then(r => r.json()),
        fetch(`/api/donos/busca?nome=${encodeURIComponent(termo)}`).then(r => r.json())
    ]);
    const resultados = [...clientes, ...donos];
    if (resultados.length === 0) {
        sugestoesDiv.innerHTML = '<div class="autocomplete-item">Nenhum resultado encontrado</div>';
        return;
    }
    resultados.forEach(item => {
        const div = document.createElement('div');
        div.className = 'autocomplete-item';
        div.textContent = `${item.nome} (${item.tipo})`;
        div.onclick = () => {
            nomeInput.value = item.nome;
            emailInput.value = item.email;
            papelInput.value = item.tipo;
            sugestoesDiv.innerHTML = '';
        };
        sugestoesDiv.appendChild(div);
    });
});

document.addEventListener('click', function(e) {
    if (!sugestoesDiv.contains(e.target) && e.target !== nomeInput) {
        sugestoesDiv.innerHTML = '';
    }
});

// Intercepta o submit do formulário e envia via fetch
document.getElementById("formRelatorio").onsubmit = function(e) {
    e.preventDefault();
    const form = e.target;
    const dados = {
        nome: form.nome.value,
        email: form.email.value,
        boletimId: form.boletimId.value,
        contratoId: form.contratoId.value,
        mensagem: form.mensagem.value,
        papel: form.papel.value // novo campo
    };
    fetch("/enviar-relatorio", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dados)
    })
    .then(async r => {
        const res = await r.json();
        exibirMensagem(res.mensagem, res.sucesso ? "sucesso" : "erro");
        if (res.sucesso) form.reset();
    })
    .catch(() => exibirMensagem("Erro ao enviar relatório. Tente novamente."));
};

