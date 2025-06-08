document.addEventListener("DOMContentLoaded", () => {
  carregarUsuarios();
  carregarClientes();
  carregarEquipamentos();
  carregarContratos();

  document.getElementById("contratoForm").onsubmit = handleContratoSubmit;
  document.getElementById("contratoForm").onreset = limparCampos;

  // Carrega o gráfico de Gantt
  google.charts.load('current', {packages: ['gantt']});
  google.charts.setOnLoadCallback(drawGanttChart);
});

function exibirMensagemContrato(msg, tipo) {
  const div = document.getElementById('mensagem-contrato');
  div.innerText = msg;
  div.style.display = 'block';
  div.style.background = tipo === 'sucesso' ? '#e6f9ec' : '#fdeaea';
  div.style.color = tipo === 'sucesso' ? '#207245' : '#c00';
  div.style.border = tipo === 'sucesso' ? '1px solid #b2e2c5' : '1px solid #f5c2c7';
  setTimeout(() => { div.style.display = 'none'; }, 4000);
}

// Função para desenhar o gráfico de Gantt
function drawGanttChart() {
  fetch('/api/contrato-locacoes')
    .then(response => response.json())
    .then(data => {
      const dataTable = new google.visualization.DataTable();
      dataTable.addColumn('string', 'ID');
      dataTable.addColumn('string', 'Tarefa');
      dataTable.addColumn('string', 'Recurso');
      dataTable.addColumn('date', 'Início');
      dataTable.addColumn('date', 'Fim');
      dataTable.addColumn('number', 'Duração');
      dataTable.addColumn('number', 'Percentual Completo');
      dataTable.addColumn('string', 'Dependências');

      data.forEach(contrato => {
        dataTable.addRow([
          contrato.id.toString(),
          `Locação de ${contrato.equipamentoNome}`,
          contrato.clienteNome,
          new Date(contrato.dataInicio),
          new Date(contrato.dataFim),
          null,
          0,
          null
        ]);
      });

      const options = {
        height: 400,
        gantt: {
          trackHeight: 30
        }
      };

      const chart = new google.visualization.Gantt(document.getElementById('ganttChart'));
      chart.draw(dataTable, options);
    })
    .catch(error => exibirMensagemContrato('Erro ao carregar dados do gráfico.', 'erro'));
}

function toInputDate(valor) {
  if (!valor) return "";
  return valor.length > 10 ? valor.substring(0, 10) : valor;
}

function formatarDataBR(data) {
  if (!data) return "-";
  const valor = toInputDate(data);
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
        tb.innerHTML = `<tr><td colspan="8" style="text-align:center">Nenhum contrato cadastrado</td></tr>`;
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
          <td>${c.statusPagamento || '-'}</td>
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

  const dataInicio = document.getElementById("contratoDataInicio").value;
  const dataFim = document.getElementById("contratoDataFim").value;

  if (dataInicio && dataFim && dataFim < dataInicio) {
    exibirMensagemContrato('A data de término não pode ser anterior à data de início.', 'erro');
    return;
  }

  const id = document.getElementById("contratoId").value;
  const body = {
    usuarioLogisticaId: document.getElementById("contratoUsuarioLogistica").value,
    clienteId: document.getElementById("contratoCliente").value,
    equipamentoId: document.getElementById("contratoEquipamento").value,
    dataInicio,
    dataFim,
    valorTotal: parseFloat(document.getElementById("contratoValor").value.replace(",", ".")) || 0,
    statusPagamento: document.getElementById("contratoStatusPagamento").value
  };

  const url = id ? `/api/contrato-locacoes/${id}` : '/api/contrato-locacoes';
  const method = id ? 'PUT' : 'POST';

  fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  }).then(async r => {
    if (r.ok) {
      exibirMensagemContrato('Contrato salvo ou atualizado com sucesso!', 'sucesso');
      limparCampos();
      carregarContratos();
      drawGanttChart();
    } else if (r.status === 409) {
      const errorMsg = await r.text();
      exibirMensagemContrato('Erro: ' + errorMsg, 'erro');
    } else {
      exibirMensagemContrato('Erro inesperado ao salvar o contrato.', 'erro');
    }
  });
}

window.editarContrato = function(id) {
  fetch(`/api/contrato-locacoes/${id}`)
    .then(r => r.json())
    .then(c => {
      document.getElementById("contratoId").value = c.id;
      document.getElementById("contratoUsuarioLogistica").value = c.usuarioLogisticaId;
      document.getElementById("contratoCliente").value = c.clienteId;
      document.getElementById("contratoEquipamento").value = c.equipamentoId;
      document.getElementById("contratoValor").value = c.valorTotal;
      document.getElementById("contratoDataInicio").value = toInputDate(c.dataInicio);
      document.getElementById("contratoDataFim").value = toInputDate(c.dataFim);
      document.getElementById("contratoStatusPagamento").value = c.statusPagamento || "PENDENTE";
      window.scrollTo({top: 0, behavior: 'smooth'});
    });
};

window.excluirContrato = function(id) {
  if (!confirm("Deseja excluir este contrato?")) return;
  fetch(`/api/contrato-locacoes/${id}`, { method: 'DELETE' })
    .then(async r => {
      if (r.ok) {
        exibirMensagemContrato('Contrato excluído com sucesso!', 'sucesso');
        carregarContratos();
        drawGanttChart();
      } else if (r.status === 409 || r.status === 500) {
        exibirMensagemContrato(
          'Não é possível excluir o contrato pois ele está vinculado a um orçamento. Exclua primeiro o orçamento relacionado.',
          'erro'
        );
      } else {
        exibirMensagemContrato('Erro ao excluir!', 'erro');
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
  document.getElementById("contratoStatusPagamento").value = "PENDENTE";
}