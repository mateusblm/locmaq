const API = "/api/donos";
const form = document.getElementById("donoForm");
const tabela = document.getElementById("tabelaDonos").querySelector("tbody");
const saveBtn = document.getElementById("saveBtn");
const editBtn = document.getElementById("editBtn");
const cancelBtn = document.getElementById("cancelBtn");
let editandoId = null;

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
  }).then(() => {
    form.reset();
    atualizarTabela();
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
  }).then(() => {
    form.reset();
    editandoId = null;
    saveBtn.style.display = "inline";
    editBtn.style.display = "none";
    cancelBtn.style.display = "none";
    document.getElementById("formTitle").innerText = "Cadastrar Dono";
    atualizarTabela();
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
      .then(() => atualizarTabela());
  }
};

saveBtn.style.display = "inline";
editBtn.style.display = "none";
cancelBtn.style.display = "none";
atualizarTabela();