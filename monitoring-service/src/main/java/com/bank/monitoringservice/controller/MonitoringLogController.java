package com.bank.monitoringservice.controller;

import com.bank.monitoringservice.model.MonitoringLogEntry;
import com.bank.monitoringservice.service.MonitoringLogStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring/logs")
@CrossOrigin(origins = "*")
@Tag(name = "Monitoring Logs", description = "Операции чтения и записи логов мониторинга")
public class MonitoringLogController {

    private final MonitoringLogStorageService storageService;

    public MonitoringLogController(MonitoringLogStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    @Operation(summary = "Получить логи мониторинга")
    public ResponseEntity<List<MonitoringLogEntry>> getLogs() {
        return ResponseEntity.ok(storageService.findAll());
    }

    @PostMapping
    @Operation(summary = "Сохранить лог мониторинга")
    public ResponseEntity<MonitoringLogEntry> createLog(
            @RequestBody @Valid MonitoringLogEntry entry
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(storageService.save(entry));
    }

    @DeleteMapping
    @Operation(summary = "Очистить все логи мониторинга")
    public ResponseEntity<Void> clearLogs() {
        storageService.clear();
        return ResponseEntity.noContent().build();
    }
}
