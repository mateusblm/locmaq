function exibirMensagemUsuario(msg, tipo) {
    const div = document.getElementById('mensagem-usuario');
    div.innerText = msg;
    div.style.display = 'block';
    div.style.background = tipo === 'sucesso' ? '#e6f9ec' : '#fdeaea';
    div.style.color = tipo === 'sucesso' ? '#207245' : '#c00';
    div.style.border = tipo === 'sucesso' ? '1px solid #b2e2c5' : '1px solid #f5c2c7';
    setTimeout(() => { div.style.display = 'none'; }, 4000);
}

function handleFormSubmit(event) {
    event.preventDefault();
    const form = event.target;

    fetch(form.action, {
        method: form.method,
        body: new URLSearchParams(new FormData(form)),
        credentials: 'include'
    })
    .then(response => {
        if (response.ok) {
            exibirMensagemUsuario('Usuario cadastrado com sucesso!', 'sucesso');
            form.reset();
            carregarUsuarios();
        } else {
            exibirMensagemUsuario('Erro ao cadastrar o usuario.', 'erro');
        }
    });
}

function carregarUsuarios() {
    fetch('/api/usuarios', { credentials: 'include' })
        .then(resp => resp.json())
        .then(usuarios => {
            const tbody = document.querySelector('#usuariosTable tbody');
            tbody.innerHTML = '';
            usuarios.forEach(usuario => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${usuario.nome}</td>
                    <td>${usuario.tipoUsuario}</td>
                    <td>${usuario.ativo ? 'Ativo' : 'Desativado'}</td>
                    <td>
                        <button onclick="alterarStatusUsuario(${usuario.id}, ${usuario.ativo})"
                            style="background:${usuario.ativo ? '#c00' : '#090'};color:#fff;border:none;padding:6px 12px;border-radius:4px;cursor:pointer;">
                            ${usuario.ativo ? 'Desativar' : 'Ativar'}
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        });
}

function alterarStatusUsuario(id, ativo) {
    const url = `/${id}/${ativo ? 'desativar' : 'ativar'}`;
    fetch(url, { method: 'POST', credentials: 'include' })
        .then(() => carregarUsuarios());
}

window.addEventListener('DOMContentLoaded', function() {
    document.getElementById('usuarioForm').onsubmit = handleFormSubmit;
    carregarUsuarios();
});

window.alterarStatusUsuario = alterarStatusUsuario;