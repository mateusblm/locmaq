document.addEventListener("DOMContentLoaded", () => {
    carregarEquipamentos();
});

const form = document.getElementById("equipamentoForm");
const tabela = document.querySelector("#equipamentoTable tbody");

form.addEventListener("submit", async function(event) {
    event.preventDefault();
    
    const equipamento = {
        id: form.id.value || null, 
        nome: form.nome.value,
        categoria: form.categoria.value,
        ano: form.ano.value,
        valor: form.valor.value,
        status: form.status.value
    };

    const metodo = equipamento.id ? "PUT" : "POST";
    const url = equipamento.id ? `/api/equipamentos/${equipamento.id}` : "/api/equipamentos";

    const resposta = await fetch(url, {
        method: metodo,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(equipamento)
    });

    if (resposta.ok) {
        carregarEquipamentos();
        form.reset();
    } else {
        alert("Erro ao salvar equipamento");
    }
});

async function carregarEquipamentos() {
    const resposta = await fetch("/api/equipamentos");
    const equipamentos = await resposta.json();

    tabela.innerHTML = "";
    equipamentos.forEach(equip => {
        const row = tabela.insertRow();

        row.innerHTML = `
            <td>${equip.id}</td>
            <td>${equip.nome}</td>
            <td>${equip.categoria}</td>
            <td>${equip.ano}</td>
            <td>${equip.valor}</td>
            <td>${equip.status}</td>
            <td>
                <button class="edit" onclick="editarEquipamento(${equip.id})">Editar</button>
                <button class="delete" onclick="removerEquipamento(${equip.id})">Remover</button>
            </td>
        `;
    });
}

async function editarEquipamento(id) {
    const resposta = await fetch(`/api/equipamentos/${id}`);
    const equip = await resposta.json();

    form.id.value = equip.id;
    form.nome.value = equip.nome;
    form.categoria.value = equip.categoria;
    form.ano.value = equip.ano;
    form.valor.value = equip.valor;
    form.status.value = equip.status;
}

async function removerEquipamento(id) {
    if (confirm("Tem certeza que deseja remover este equipamento?")) {
        await fetch(`/api/equipamentos/${id}`, { method: "DELETE" });
        carregarEquipamentos();
    }
}
