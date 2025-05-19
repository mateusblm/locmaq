document.getElementById('loginForm').onsubmit = function(event) {
  event.preventDefault();

  const nome = document.getElementById('nome').value;
  const senha = document.getElementById('senha').value;
  document.getElementById('loginError').innerText = "";

  fetch('/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: new URLSearchParams({ username: nome, password: senha })
  })
    .then(resp => {
      // Se não autenticou, resp.redirected pode ser true ou resp.url termina com ?error
      if(resp.redirected || !resp.ok || (resp.url && resp.url.includes('?error'))) {
        throw new Error();
      }
      // Agora tenta buscar o usuário autenticado
      return fetch('/api/usuarios/me');
    })
    .then(r => {
      if (!r.ok) throw new Error();
      return r.json();
    })
    .then(user => {
      if (!user || !user.tipoUsuario) throw new Error();

      sessionStorage.setItem('perfilLogado', user.tipoUsuario);
      sessionStorage.setItem('nomeUsuario', user.nome);

      if (user.tipoUsuario === 'GESTOR') window.location.href = '/html/gestor.html';
      else if (user.tipoUsuario === 'PLANEJADOR') window.location.href = '/html/planejador.html';
      else if (user.tipoUsuario === 'LOGISTICA') window.location.href = '/html/logistica.html';
      else window.location.href = '/html/usuario.html';
    })
    .catch(() => {
      document.getElementById('loginError').innerText = "Usuário ou senha inválidos!";
    });
};