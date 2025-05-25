const API = "/api/donos";
const form = document.getElementById("donoForm");
const tabela = document.getElementById("tabelaDonos").querySelector("tbody");
const saveBtn = document.getElementById("saveBtn");
const editBtn = document.getElementById("editBtn");
const cancelBtn = document.getElementById("cancelBtn");
let editandoId = null;

// Exibe mensagem padronizada na tela
function exibirMensagem(msg, tipo = "erro") {
    const div = document.getElementById("mensagem-erro");
    if (!div) return;
    div.innerText = msg;
    div.style.display = "block";
    div.style.background = tipo === "sucesso" ? "#e0ffe0" : "#ffe0e0";
    div.style.color = tipo === "sucesso" ? "#0a0" : "#a00";
    div.style.border = tipo === "sucesso" ? "1px solid #9f9" : "1px solid #f99";
    setTimeout(() => { div.style.display = "none"; }, 5000);
}

function validarCpfCnpj(valor) {
    valor = valor.replace(/\D/g, '');
    if (valor.length === 11) {
        let soma = 0, resto;
        if (/^(\d)\1+$/.test(valor)) return false;
        for (let i = 1; i <= 9; i++) soma += parseInt(valor.substring(i-1, i)) * (11 - i);
        resto = (soma * 10) % 11;
        if (resto === 10 || resto === 11) resto = 0;
        if (resto !== parseInt(valor.substring(9, 10))) return false;
        soma = 0;
        for (let i = 1; i <= 10; i++) soma += parseInt(valor.substring(i-1, i)) * (12 - i);
        resto = (soma * 10) % 11;
        if (resto === 10 || resto === 11) resto = 0;
        return resto === parseInt(valor.substring(10, 11));
    } else if (valor.length === 14) {
        if (/^(\d)\1+$/.test(valor)) return false;
        let tamanho = valor.length - 2;
        let numeros = valor.substring(0, tamanho);
        let digitos = valor.substring(tamanho);
        let soma = 0;
        let pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }
        let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado !== parseInt(digitos.charAt(0))) return false;
        tamanho = tamanho + 1;
        numeros = valor.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }
        resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        return resultado === parseInt(digitos.charAt(1));
    }
    return false;
}

function validarAgencia(valor) {
    return /^\d{4}$/.test(valor);
}

function validarNumeroConta(valor) {
    return /^\d{6}-\d{1}$/.test(valor);
}

document.getElementById('donoForm').addEventListener('submit', function(e) {
    const cnpj = document.getElementById('cnpj').value;
    const agencia = document.getElementById('agencia').value;
    const numeroConta = document.getElementById('numeroConta').value;

    if (!validarCpfCnpj(cnpj)) {
        exibirMensagem('CPF ou CNPJ inválido!');
        e.preventDefault();
        return;
    }
    if (!validarAgencia(agencia)) {
        exibirMensagem('Agência inválida! Deve conter 4 dígitos.');
        e.preventDefault();
        return;
    }
    if (!validarNumeroConta(numeroConta)) {
        exibirMensagem('Número da conta inválido! Formato: 123456-7');
        e.preventDefault();
        return;
    }
});

function atualizarTabela() {
  fetch(API)
    .then(r => r.json())
    .then(donos => {
      tabela.innerHTML = donos.map(d => `
        <tr>
          <td>${d.nome}</td>
          <td>${d.endereco}</td>
          <td>${d.email}</td>
          <td>${d.cnpj}</td>
          <td>${d.telefone}</td>
          <td>${d.banco}</td>
          <td>${d.agencia}</td>
          <td>${d.numeroConta}</td>
          <td>
            <button class="acao-btn" onclick="editarDono(${d.id})">Editar</button>
            <button class="acao-btn delete" onclick="removerDono(${d.id})">Remover</button>
          </td>
        </tr>
      `).join('');
    });
}

form.onsubmit = function(e) {
  e.preventDefault();
  const dono = {
    nome: form.nome.value,
    endereco: form.endereco.value,
    email: form.email.value,
    cnpj: form.cnpj.value,
    telefone: form.telefone.value,
    banco: form.banco.value,
    agencia: form.agencia.value,
    numeroConta: form.numeroConta.value
  };
  fetch(API, {
    method: "POST",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dono)
  }).then(async response => {
    const msg = await response.text();
    if (response.ok) {
      exibirMensagem(msg || "Proprietário cadastrado com sucesso!", "sucesso");
      form.reset();
      atualizarTabela();
    } else {
      exibirMensagem(msg || "Erro ao cadastrar proprietário.");
    }
  });
};

window.editarDono = function(id) {
  fetch(API + "/" + id)
    .then(r => r.json())
    .then(d => {
      form.id.value = d.id;
      form.nome.value = d.nome;
      form.endereco.value = d.endereco;
      form.email.value = d.email;
      form.cnpj.value = d.cnpj;
      form.telefone.value = d.telefone;
      form.banco.value = d.banco;
      form.agencia.value = d.agencia;
      form.numeroConta.value = d.numeroConta;
      editandoId = d.id;
      saveBtn.style.display = "none";
      editBtn.style.display = "inline";
      cancelBtn.style.display = "inline";
      document.getElementById("formTitle").innerText = "Editar Dono";
    });
};

editBtn.onclick = function() {
  const dono = {
    id: form.id.value,
    nome: form.nome.value,
    endereco: form.endereco.value,
    email: form.email.value,
    cnpj: form.cnpj.value,
    telefone: form.telefone.value,
    banco: form.banco.value,
    agencia: form.agencia.value,
    numeroConta: form.numeroConta.value
  };
  fetch(API + "/" + editandoId, {
    method: "PUT",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dono)
  }).then(async response => {
    const msg = await response.text();
    if (response.ok) {
      exibirMensagem(msg || "Proprietário atualizado com sucesso!", "sucesso");
      form.reset();
      editandoId = null;
      saveBtn.style.display = "inline";
      editBtn.style.display = "none";
      cancelBtn.style.display = "none";
      document.getElementById("formTitle").innerText = "Cadastrar Dono";
      atualizarTabela();
    } else {
      exibirMensagem(msg || "Erro ao atualizar proprietário.");
    }
  });
};

cancelBtn.onclick = function() {
  form.reset();
  editandoId = null;
  saveBtn.style.display = "inline";
  editBtn.style.display = "none";
  cancelBtn.style.display = "none";
  document.getElementById("formTitle").innerText = "Cadastrar Dono";
};

window.removerDono = function(id) {
  if (confirm("Deseja remover este dono?")) {
    fetch(API + "/" + id, { method: "DELETE" })
      .then(async response => {
        if (response.ok) {
          exibirMensagem("Proprietário removido com sucesso!", "sucesso");
          atualizarTabela();
        } else {
          const msg = await response.text();
          exibirMensagem(msg || "Erro ao remover proprietário.");
        }
      });
  }
}
saveBtn.style.display = "inline";
editBtn.style.display = "none";
cancelBtn.style.display = "none";
atualizarTabela();