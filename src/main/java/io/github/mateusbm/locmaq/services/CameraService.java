package io.github.mateusbm.locmaq.services;

import io.github.mateusbm.locmaq.dto.CameraStatusDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CameraService {

    private static final long TIMEOUT_SEGUNDOS = 10;

    public CameraStatusDTO consultarStatus() {
        ResultadoComando resultado = executarSystemctl("status");
        boolean ativo = cameraAtiva(resultado);
        return new CameraStatusDTO(
                ativo,
                ativo ? "Camera ligada" : "Camera desligada",
                resumirOutput(resultado.output())
        );
    }

    public CameraStatusDTO ligarCamera() {
        ResultadoComando resultado = executarSystemctl("start");
        CameraStatusDTO status = consultarStatus();
        return new CameraStatusDTO(
                status.isAtivo(),
                status.isAtivo() ? "Camera ligada com sucesso" : "Nao foi possivel ligar a camera",
                resumirOutput(resultado.output() + "\n" + status.getOutput())
        );
    }

    public CameraStatusDTO desligarCamera() {
        ResultadoComando resultado = executarSystemctl("stop");
        CameraStatusDTO status = consultarStatus();
        return new CameraStatusDTO(
                status.isAtivo(),
                status.isAtivo() ? "Nao foi possivel desligar a camera" : "Camera desligada pelo painel de monitoramento.",
                resumirOutput(resultado.output() + "\n" + status.getOutput())
        );
    }

    public CameraStatusDTO reiniciarCamera() {
        ResultadoComando resultado = executarSystemctl("restart");
        CameraStatusDTO status = consultarStatus();
        return new CameraStatusDTO(
                status.isAtivo(),
                status.isAtivo() ? "Camera reiniciada com sucesso" : "Nao foi possivel reiniciar a camera",
                resumirOutput(resultado.output() + "\n" + status.getOutput())
        );
    }

    private ResultadoComando executarSystemctl(String acao) {
        List<String> comando = List.of("sudo", "systemctl", acao, "motion");
        ProcessBuilder processBuilder = new ProcessBuilder(comando);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            boolean finalizado = process.waitFor(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
            if (!finalizado) {
                process.destroyForcibly();
                return new ResultadoComando(-1, "Tempo limite excedido ao executar: " + String.join(" ", comando));
            }

            String output = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            return new ResultadoComando(process.exitValue(), output);
        } catch (IOException ex) {
            return new ResultadoComando(-1, "Erro ao executar systemctl: " + ex.getMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return new ResultadoComando(-1, "Execucao interrompida ao controlar a camera.");
        }
    }

    private boolean cameraAtiva(ResultadoComando resultado) {
        String output = resultado.output().toLowerCase();
        return resultado.exitCode() == 0 && output.contains("active: active");
    }

    private String resumirOutput(String output) {
        if (output == null || output.isBlank()) {
            return "Sem saida do comando.";
        }

        return output.lines()
                .map(String::trim)
                .filter(linha -> !linha.isBlank())
                .limit(12)
                .collect(Collectors.joining("\n"));
    }

    private record ResultadoComando(int exitCode, String output) {
    }
}
