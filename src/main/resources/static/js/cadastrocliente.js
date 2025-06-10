const API = "/api/clientes";
const form = document.getElementById("clienteForm");
const tabela = document.getElementById("tabelaClientes").querySelector("tbody");
const saveBtn = document.getElementById("saveBtn");
const editBtn = document.getElementById("editBtn");
const cancelBtn = document.getElementById("cancelBtn");
const mensagem = document.getElementById("mensagem");
let editandoId = null;

function exibirMensagem(texto, tipo) {
mensagem.innerText = texto;
mensagem.style.display = "block";
mensagem.style.background = tipo === "erro" ? "#ffe0e0" : "#e0ffe0";
mensagem.style.color = tipo === "erro" ? "#b20000" : "#006600";
setTimeout(() => { mensagem.style.display = "none"; }, 4000);
}

function atualizarTabela() {
fetch(API)
  .then(r => r.json())
  .then(clientes => {
    tabela.innerHTML = clientes.map(c => `
      <tr>
        <td>${c.nome}</td>
        <td>${c.endereco}</td>
        <td>${c.email}</td>
        <td>${c.cnpj}</td>
        <td>${c.telefone}</td>
        <td>
          <button class="acao-btn" onclick="editarCliente(${c.id})">Editar</button>
          <button class="acao-btn delete" onclick="removerCliente(${c.id})">Remover</button>
        </td>
      </tr>
    `).join('');
  });
}

form.onsubmit = function(e) {
e.preventDefault();
const cliente = {
  nome: form.nome.value,
  endereco: form.endereco.value,
  email: form.email.value,
  cnpj: form.cnpj.value,
  telefone: form.telefone.value
};
fetch(API, {
  method: "POST",
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(cliente)
})
.then(async resp => {
  if (resp.ok) {
    exibirMensagem("Cliente cadastrado com sucesso!", "sucesso");
    form.reset();
    atualizarTabela();
  } else {
    const erro = await resp.text();
    if (erro.includes("Já existe um cliente com esse CPF ou CNPJ")) {
      exibirMensagem("Já existe um cliente com esse CPF ou CNPJ.", "erro");
    } else {
      exibirMensagem(erro || "Erro ao cadastrar cliente.", "erro");
    }
  }
})
.catch(() => {
  exibirMensagem("Erro de conexão com o servidor.", "erro");
});
};

window.editarCliente = function(id) {
fetch(API + "/" + id)
  .then(r => r.json())
  .then(c => {
    form.id.value = c.id;
    form.nome.value = c.nome;
    form.endereco.value = c.endereco;
    form.email.value = c.email;
    form.cnpj.value = c.cnpj;
    form.telefone.value = c.telefone;
    editandoId = c.id;
    saveBtn.style.display = "none";
    editBtn.style.display = "inline";
    cancelBtn.style.display = "inline";
    document.getElementById("formTitle").innerText = "Editar Cliente";
  });
};

editBtn.onclick = function() {
const cliente = {
  id: form.id.value,
  nome: form.nome.value,
  endereco: form.endereco.value,
  email: form.email.value,
  cnpj: form.cnpj.value,
  telefone: form.telefone.value
};
fetch(API + "/" + editandoId, {
  method: "PUT",
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(cliente)
})
.then(async resp => {
  if (resp.ok) {
    form.reset();
    editandoId = null;
    saveBtn.style.display = "inline";
    editBtn.style.display = "none";
    cancelBtn.style.display = "none";
    document.getElementById("formTitle").innerText = "Cadastrar Cliente";
    atualizarTabela();
    exibirMensagem("Cliente atualizado com sucesso!", "sucesso");
  } else {
    const erro = await resp.text();
    exibirMensagem(erro || "Erro ao atualizar cliente.", "erro");
  }
})
.catch(() => {
  exibirMensagem("Erro ao atualizar cliente.", "erro");
});
};

cancelBtn.onclick = function() {
form.reset();
editandoId = null;
saveBtn.style.display = "inline";
editBtn.style.display = "none";
cancelBtn.style.display = "none";
document.getElementById("formTitle").innerText = "Cadastrar Cliente";
};

window.removerCliente = function(id) {
if (confirm("Deseja remover este cliente?")) {
  fetch(API + "/" + id, { method: "DELETE" })
    .then(async resp => {
      if (resp.ok) {
        atualizarTabela();
        exibirMensagem("Cliente removido com sucesso!", "sucesso");
      } else {
        const erro = await resp.text();
        exibirMensagem(erro || "Erro ao remover cliente.", "erro");
      }
    })
    .catch(() => {
      exibirMensagem("Erro ao remover cliente.", "erro");
    });
}
};

saveBtn.style.display = "inline";
editBtn.style.display = "none";
cancelBtn.style.display = "none";
atualizarTabela();