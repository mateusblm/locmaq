// Exibe mensagem de acesso restrito, se houver parâmetro na URL
window.addEventListener('DOMContentLoaded', function() {
  const params = new URLSearchParams(window.location.search);
  const msg = params.get('mensagem');
  if (msg) {
    const div = document.getElementById('mensagem-login');
    if (div) {
      div.textContent = decodeURIComponent(msg);
      div.style.display = 'block';
    }
  }
});

document.getElementById('loginForm').onsubmit = function(event) {
  event.preventDefault();

  const nome = document.getElementById('nome').value;
  const senha = document.getElementById('senha').value;
  document.getElementById('loginError').innerText = "";

  fetch('/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: new URLSearchParams({ username: nome, password: senha }),
    credentials: 'include'
  })
    .then(resp => {
      if (!resp.ok) {
        return resp.text().then(text => { throw new Error(text); });
      }
      return fetch('/api/usuarios/me', { credentials: 'include' });
    })
    .then(r => {
      if (!r.ok) throw new Error();
      return r.json();
    })
    .then(user => {
      sessionStorage.setItem('perfilLogado', user.tipoUsuario);
      sessionStorage.setItem('nomeUsuario', user.nome);

      if (user.tipoUsuario === 'GESTOR') window.location.href = '/html/gestor.html';
      else if (user.tipoUsuario === 'PLANEJADOR') window.location.href = '/html/planejador.html';
      else if (user.tipoUsuario === 'LOGISTICA') window.location.href = '/html/logistica.html';
      else window.location.href = '/html/usuario.html';
    })
    .catch((err) => {
      if (err.message && err.message.toLowerCase().includes("desativado")) {
        document.getElementById('loginError').innerText = "Sua conta está desativada. Procure o gestor para reativação.";
      } else {
        document.getElementById('loginError').innerText = "Usuário ou senha inválidos!";
      }
    });
};