package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.dto.CameraStatusDTO;
import io.github.mateusbm.locmaq.services.CameraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/camera")
public class CameraController {

    private final CameraService cameraService;

    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping("/status")
    public ResponseEntity<CameraStatusDTO> consultarStatus() {
        return ResponseEntity.ok(cameraService.consultarStatus());
    }

    @PostMapping("/start")
    public ResponseEntity<CameraStatusDTO> ligarCamera() {
        return ResponseEntity.ok(cameraService.ligarCamera());
    }

    @PostMapping("/stop")
    public ResponseEntity<CameraStatusDTO> desligarCamera() {
        return ResponseEntity.ok(cameraService.desligarCamera());
    }

    @PostMapping("/restart")
    public ResponseEntity<CameraStatusDTO> reiniciarCamera() {
        return ResponseEntity.ok(cameraService.reiniciarCamera());
    }
}
