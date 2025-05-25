let perfilLogado = sessionStorage.getItem('perfilLogado');
let nomeUsuario  = sessionStorage.getItem('nomeUsuario');

// Se não logado, manda pro login
if (!perfilLogado || !nomeUsuario) {
  window.location.href = "/index.html";
}

document.querySelectorAll('.logout-btn').forEach(b => {
    b.addEventListener('click', function(e) {
        e.preventDefault();
        sessionStorage.clear();
        window.location.href = '/index.html';
    });
});

// Mapeamento dos menus por perfil:
const menusPorPerfil = {
  GESTOR: [
    { href: "/html/gestor.html", label: "Painel Gestor" },
    { href: "/html/cadastrarusuario.html", label: "Criar Usuario" },
    { href: "/html/assinar_boletim_medicao.html", label: "Gerenciar Boletim" },
    { href: "/html/cadastrocliente.html", label: "Gerenciar Clientes" },
    { href: "/html/ver_logs.html", label: "Logs do sistema" }
  ],
  PLANEJADOR: [
    { href: "/html/planejador.html", label: "Painel Planejador" },
    { href: "/html/cadastrocliente.html", label: "Gerenciar Clientes" },
    { href: "/html/boletimmedicao.html", label: "Boletim de Medição" },
    { href: "/html/cadastrodono.html", label: "Gerenciar Proprietarios" }
  ],
  LOGISTICA: [
    { href: "/html/logistica.html", label: "Painel Logística" },
    { href: "/html/equipamento.html", label: "Gerenciar Equipamentos" },
    { href: "/html/contratolocacao.html", label: "Contratos de Locação" }
  ]
};

function ativarMenu() {
  const nav = document.querySelector('.nav-links');
  const userRole = document.getElementById('user-role');
  const userName = document.getElementById('user-name');
  if (nav && perfilLogado) {
    nav.innerHTML = menusPorPerfil[perfilLogado]
      .map(item => {
        // Detecta a página atual para adicionar .active
        const isActive = window.location.pathname.endsWith(item.href.substring(item.href.lastIndexOf('/')+1));
        return `<a href="${item.href}" ${isActive?'class="active"':''}>${item.label}</a>`;
      }).join('');
    nav.innerHTML += `<a href="/html/usuario.html"${window.location.pathname.endsWith('usuario.html') ? ' class="active"' : ''}>Perfil</a>`;
  }
  if (userRole) userRole.innerText = perfilLogado.charAt(0) + perfilLogado.slice(1).toLowerCase();
  if (userName) userName.innerText = nomeUsuario;
}
window.addEventListener('DOMContentLoaded', ativarMenu);