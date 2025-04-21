document.addEventListener("DOMContentLoaded", () => {
  carregarUsuarios();
  carregarClientes();
  carregarEquipamentos();
  carregarContratos();

  document.getElementById("contratoForm").onsubmit = handleContratoSubmit;
  document.getElementById("contratoForm").onreset = limparCampos;
});

// Função que formata string de data no padrão do input date
function toInputDate(valor) {
  if (!valor) return "";
  return valor.length > 10 ? valor.substring(0, 10) : valor;
}

function formatarDataBR(data) {
  if (!data) return "-";
  const valor = toInputDate(data); // "YYYY-MM-DD"
  const [ano, mes, dia] = valor.split("-");
  return `${dia}/${mes}/${ano}`;
}

function carregarUsuarios() {
  fetch('/api/usuarios?tipo=LOGISTICA').then(r => r.json()).then(usuarios => {
    const sel = document.getElementById("contratoUsuarioLogistica");
    sel.innerHTML = `<option value="">Selecione...</option>` +
      usuarios.map(u => `<option value="${u.id}">${u.nome}</option>`).join('');
  });
}

function carregarClientes() {
  fetch('/api/clientes').then(r => r.json()).then(clientes => {
    const sel = document.getElementById("contratoCliente");
    sel.innerHTML = `<option value="">Selecione...</option>` +
      clientes.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');
  });
}

function carregarEquipamentos() {
  fetch('/api/equipamentos').then(r => r.json()).then(equipamentos => {
    const sel = document.getElementById("contratoEquipamento");
    sel.innerHTML = `<option value="">Selecione...</option>` +
      equipamentos.map(e => `<option value="${e.id}">${e.nome}</option>`).join('');
  });
}

function carregarContratos() {
  fetch('/api/contrato-locacoes')
    .then(r => r.json())
    .then(contratos => {
      const tb = document.getElementById("contratosTable");
      if (!contratos || !contratos.length) {
        tb.innerHTML = `<tr><td colspan="7" style="text-align:center">Nenhum contrato cadastrado</td></tr>`;
        return;
      }
      tb.innerHTML = contratos.map(c => `
        <tr>
          <td>${c.usuarioLogisticaNome || '-'}</td>
          <td>${c.clienteNome || '-'}</td>
          <td>${c.equipamentoNome || '-'}</td>
          <td>${formatarDataBR(c.dataInicio)}</td>
          <td>${formatarDataBR(c.dataFim)}</td>
          <td>R$ ${c.valorTotal != null ? c.valorTotal.toLocaleString('pt-BR', {minimumFractionDigits:2}) : '-'}</td>
          <td>
            <button class="visualizar-btn" onclick="editarContrato(${c.id})">Editar</button>
            <button class="acao-btn delete" onclick="excluirContrato(${c.id})">Excluir</button>
          </td>
        </tr>
      `).join('');
    });
}

function handleContratoSubmit(e) {
  e.preventDefault();
  const id = document.getElementById("contratoId").value;
  const body = {
    usuarioLogisticaId: document.getElementById("contratoUsuarioLogistica").value,
    clienteId: document.getElementById("contratoCliente").value,
    equipamentoId: document.getElementById("contratoEquipamento").value,
    dataInicio: document.getElementById("contratoDataInicio").value,
    dataFim: document.getElementById("contratoDataFim").value,
    valorTotal: parseFloat(document.getElementById("contratoValor").value.replace(",", ".")) || 0,
  };

  if (id) {
    fetch(`/api/contrato-locacoes/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...body, id })
    }).then(async r => {
      if (r.ok) {
        alert('Contrato atualizado!');
        limparCampos();
        carregarContratos();
      } else {
        alert('Erro ao atualizar!\n' + await r.text());
      }
    });
  } else {
    fetch('/api/contrato-locacoes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    }).then(async r => {
      if (r.ok) {
        alert('Contrato salvo!');
        limparCampos();
        carregarContratos();
      } else {
        alert('Erro ao salvar!\n' + await r.text());
      }
    });
  }
}

window.editarContrato = function(id) {
  fetch(`/api/contrato-locacoes/${id}`)
    .then(r => r.json())
    .then(c => {
      console.log("DATA DEBUG:", {
      dataInicio: c.dataInicio,
      dataFim: c.dataFim,
      tipoInicio: typeof c.dataInicio,
      tipoFim: typeof c.dataFim,
      igual: c.dataInicio === c.dataFim
      });
      document.getElementById("contratoId").value = c.id;
      document.getElementById("contratoUsuarioLogistica").value = c.usuarioLogisticaId;
      document.getElementById("contratoCliente").value = c.clienteId;
      document.getElementById("contratoEquipamento").value = c.equipamentoId;
      document.getElementById("contratoValor").value = c.valorTotal;
      document.getElementById("contratoDataInicio").value = toInputDate(c.dataInicio);
      document.getElementById("contratoDataFim").value = toInputDate(c.dataFim);
      window.scrollTo({top: 0, behavior: 'smooth'});
    });
};

window.excluirContrato = function(id) {
  if (!confirm("Deseja excluir este contrato?")) return;
  fetch(`/api/contrato-locacoes/${id}`, { method: 'DELETE' })
    .then(r => {
      if (r.ok) {
        carregarContratos();
      } else {
        alert('Erro ao excluir!');
      }
    });
};

function limparCampos() {
  document.getElementById("contratoId").value = "";
  document.getElementById("contratoUsuarioLogistica").value = "";
  document.getElementById("contratoCliente").value = "";
  document.getElementById("contratoEquipamento").value = "";
  document.getElementById("contratoValor").value = "";
  document.getElementById("contratoDataInicio").value = "";
  document.getElementById("contratoDataFim").value = "";
}