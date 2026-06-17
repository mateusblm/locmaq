window.addEventListener('DOMContentLoaded', () => {
    const perfilLogado = sessionStorage.getItem('perfilLogado');
    if (perfilLogado !== 'GESTOR' && perfilLogado !== 'LOGISTICA') {
        window.location.href = '/index.html';
        return;
    }

    const host = window.location.hostname || 'localhost';
    const baseUrl = `http://${host}:8081`;
    const urls = {
        principal: `${baseUrl}/0/stream`,
        fallback: baseUrl
    };

    const stream = document.getElementById('cameraStream');
    const abrirCameraLink = document.getElementById('abrirCameraLink');
    const streamPrincipalBtn = document.getElementById('streamPrincipalBtn');
    const streamFallbackBtn = document.getElementById('streamFallbackBtn');
    const ligarCameraBtn = document.getElementById('ligarCameraBtn');
    const desligarCameraBtn = document.getElementById('desligarCameraBtn');
    const reiniciarCameraBtn = document.getElementById('reiniciarCameraBtn');
    const atualizarStatusBtn = document.getElementById('atualizarStatusBtn');
    const cameraStatusTexto = document.getElementById('cameraStatusTexto');
    const cameraMensagem = document.getElementById('cameraMensagem');
    const cameraOutput = document.getElementById('cameraOutput');
    const cameraDesligadaAviso = document.getElementById('cameraDesligadaAviso');
    const botoesAcao = [ligarCameraBtn, desligarCameraBtn, reiniciarCameraBtn, atualizarStatusBtn];
    let streamAtual = urls.principal;

    function carregarStream(url) {
        streamAtual = url;
        const separador = url.includes('?') ? '&' : '?';
        stream.src = `${url}${separador}t=${Date.now()}`;
        abrirCameraLink.href = url;
    }

    function recarregarStream() {
        stream.removeAttribute('src');
        setTimeout(() => carregarStream(streamAtual), 300);
    }

    function setCarregando(carregando) {
        botoesAcao.forEach(botao => botao.disabled = carregando);
        if (carregando) {
            cameraStatusTexto.textContent = 'Processando...';
        }
    }

    function aplicarStatus(data) {
        const ativa = Boolean(data.ativo);
        cameraStatusTexto.textContent = ativa ? 'Ligada' : 'Desligada';
        cameraStatusTexto.style.color = ativa ? '#2e7d32' : '#b71c1c';
        cameraMensagem.textContent = data.mensagem || '';
        cameraDesligadaAviso.style.display = ativa ? 'none' : 'block';
        stream.style.display = ativa ? 'block' : 'none';

        if (data.output) {
            cameraOutput.style.display = 'block';
            cameraOutput.textContent = data.output;
        } else {
            cameraOutput.style.display = 'none';
            cameraOutput.textContent = '';
        }

        if (ativa) {
            recarregarStream();
        } else {
            stream.removeAttribute('src');
        }
    }

    function tratarErro() {
        cameraStatusTexto.textContent = 'Indisponivel';
        cameraStatusTexto.style.color = '#b71c1c';
        cameraMensagem.textContent = 'Nao foi possivel consultar ou controlar a camera.';
        cameraOutput.style.display = 'none';
    }

    function consultarStatus() {
        setCarregando(true);
        return fetch('/api/camera/status')
            .then(response => response.ok ? response.json() : Promise.reject())
            .then(aplicarStatus)
            .catch(tratarErro)
            .finally(() => setCarregando(false));
    }

    function executarAcao(acao) {
        setCarregando(true);
        return fetch(`/api/camera/${acao}`, { method: 'POST' })
            .then(response => response.ok ? response.json() : Promise.reject())
            .then(aplicarStatus)
            .catch(tratarErro)
            .finally(() => setCarregando(false));
    }

    streamPrincipalBtn.addEventListener('click', () => carregarStream(urls.principal));
    streamFallbackBtn.addEventListener('click', () => carregarStream(urls.fallback));
    ligarCameraBtn.addEventListener('click', () => executarAcao('start'));
    desligarCameraBtn.addEventListener('click', () => executarAcao('stop'));
    reiniciarCameraBtn.addEventListener('click', () => executarAcao('restart'));
    atualizarStatusBtn.addEventListener('click', consultarStatus);

    carregarStream(urls.principal);
    consultarStatus();
});
