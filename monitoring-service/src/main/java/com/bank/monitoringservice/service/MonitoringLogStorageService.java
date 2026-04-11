package com.bank.monitoringservice.service;

import com.bank.monitoringservice.model.MonitoringLogEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class MonitoringLogStorageService {

    private final ConcurrentLinkedDeque<MonitoringLogEntry> entries = new ConcurrentLinkedDeque<>();
    private final int maxSize;

    public MonitoringLogStorageService(
            @Value("${monitoring.logs.max-size:1000}") int maxSize
    ) {
        this.maxSize = maxSize;
    }

    public MonitoringLogEntry save(MonitoringLogEntry entry) {
        MonitoringLogEntry normalized = normalize(entry);
        entries.addFirst(normalized);
        trimToLimit();
        return normalized;
    }

    public List<MonitoringLogEntry> findAll() {
        return new ArrayList<>(entries);
    }

    public void clear() {
        entries.clear();
    }

    private MonitoringLogEntry normalize(MonitoringLogEntry entry) {
        String id = (entry.id() == null || entry.id().isBlank())
                ? UUID.randomUUID().toString()
                : entry.id();
        long timestamp = entry.timestamp() > 0 ? entry.timestamp() : System.currentTimeMillis();

        return new MonitoringLogEntry(
                id,
                timestamp,
                entry.app(),
                entry.service(),
                entry.level(),
                entry.method(),
                entry.path(),
                entry.status(),
                entry.latencyMs(),
                entry.retries(),
                entry.blockedByCircuit(),
                entry.circuitState(),
                entry.traceId(),
                entry.message()
        );
    }

    private void trimToLimit() {
        while (entries.size() > maxSize) {
            entries.pollLast();
        }
    }
}
