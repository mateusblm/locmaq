function showSuccessPopup() {
    alert("Usuario cadastrado com sucesso!");
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
            showSuccessPopup();
            form.reset();
            carregarUsuarios();
        } else {
            alert("Erro ao cadastrar o usuario.");
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