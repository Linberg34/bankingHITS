package com.gautama.bankhitsaccount.controller;

import com.gautama.bankhitsaccount.dto.CreateOperationRequest;
import com.gautama.bankhitsaccount.dto.OperationDTO;
import com.gautama.bankhitsaccount.dto.OperationResponse;
import com.gautama.bankhitsaccount.dto.TransferRequest;
import com.gautama.bankhitsaccount.service.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/internal/operations")
@RequiredArgsConstructor
@Slf4j
public class OperationController {

    private final OperationService operationService;

    @PostMapping("/deposit")
    public ResponseEntity<OperationResponse> deposit(@RequestBody CreateOperationRequest request) {
        log.info("REST request to deposit: {}", request);
        return ResponseEntity.ok(operationService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<OperationResponse> withdraw(@RequestBody CreateOperationRequest request) {
        log.info("REST request to withdraw: {}", request);
        return ResponseEntity.ok(operationService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<OperationResponse> transfer(@RequestBody TransferRequest request) {
        log.info("REST request to transfer: {}", request);
        return ResponseEntity.ok(operationService.transfer(request));
    }

    @GetMapping("/account/{accountNumber}/page")
    public ResponseEntity<List<OperationDTO>> getAccountOperationsPage(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("REST request to get operations for account: {}", accountNumber);
        return ResponseEntity.ok(operationService.getAccountOperations(accountNumber, page, size));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<OperationDTO>> getAccountOperations(
            @PathVariable String accountNumber) {
        log.info("REST request to get operations for account: {}", accountNumber);
        return ResponseEntity.ok(operationService.getAccountOperations(accountNumber));
    }

    @GetMapping("/account/{accountNumber}/range")
    public ResponseEntity<List<OperationDTO>> getAccountOperationsByDateRange(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to get operations for account {} between {} and {}", accountNumber, start, end);
        return ResponseEntity.ok(operationService.getAccountOperationsByDateRange(accountNumber, start, end));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationDTO> getOperationById(@PathVariable Long id) {
        log.info("REST request to get operation by id: {}", id);
        return ResponseEntity.ok(operationService.getOperationById(id));
    }

//    @GetMapping("/transaction/{transactionId}")
//    public ResponseEntity<List<OperationDTO>> getOperationsByTransactionId(
//            @PathVariable String transactionId) {
//        log.info("REST request to get operations by transactionId: {}", transactionId);
//        return ResponseEntity.ok(operationService.getOperationsByTransactionId(transactionId));
//    }

//    @GetMapping("/account/{accountId}/last")
//    public ResponseEntity<OperationDTO> getLastOperation(@PathVariable Long accountId) {
//        log.info("REST request to get last operation for account: {}", accountId);
//        OperationDTO operation = operationService.getLastOperation(accountId);
//        return operation != null ? ResponseEntity.ok(operation) : ResponseEntity.noContent().build();
//    }

//    @GetMapping("/account/{accountId}/turnover")
//    public ResponseEntity<BigDecimal> getAccountTurnover(
//            @PathVariable Long accountId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
//        log.info("REST request to get turnover for account {} between {} and {}", accountId, start, end);
//        return ResponseEntity.ok(operationService.getAccountTurnover(accountId, start, end));
//    }
//
//    @GetMapping("/account/{accountId}/summary")
//    public ResponseEntity<AccountOperationsSummaryDTO> getAccountOperationsSummary(
//            @PathVariable Long accountId,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
//        log.info("REST request to get operations summary for account: {}", accountId);
//
//        if (start == null) {
//            start = LocalDateTime.now().minusMonths(1);
//        }
//        if (end == null) {
//            end = LocalDateTime.now();
//        }
//
//        return ResponseEntity.ok(operationService.getAccountOperationsSummary(accountId, start, end));
//    }

//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<OperationDTO>> getUserOperations(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        log.info("REST request to get operations for user: {}", userId);
//        return ResponseEntity.ok(operationService.getUserOperations(userId, page, size));
//    }
//
//    @GetMapping("/user/{userId}/recent")
//    public ResponseEntity<List<OperationDTO>> getRecentUserOperations(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "10") int limit) {
//        log.info("REST request to get recent {} operations for user: {}", limit, userId);
//        return ResponseEntity.ok(operationService.getRecentUserOperations(userId, limit));
//    }
//
//    @GetMapping("/statistics/daily")
//    public ResponseEntity<DailyOperationsStatisticsDTO> getDailyStatistics(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        log.info("REST request to get daily statistics for: {}", date);
//        return ResponseEntity.ok(operationService.getDailyStatistics(date));
//    }
//
//    @GetMapping("/statistics/account/{accountId}/monthly")
//    public ResponseEntity<MonthlyStatisticsDTO> getMonthlyStatistics(
//            @PathVariable Long accountId,
//            @RequestParam int year,
//            @RequestParam int month) {
//        log.info("REST request to get monthly statistics for account: {}, {}-{}", accountId, year, month);
//        return ResponseEntity.ok(operationService.getMonthlyStatistics(accountId, year, month));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
//        log.info("REST request to delete operation: {}", id);
//        operationService.deleteOperation(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PutMapping("/{id}/cancel")
//    public ResponseEntity<OperationResponse> cancelOperation(@PathVariable Long id) {
//        log.info("REST request to cancel operation: {}", id);
//        return ResponseEntity.ok(operationService.cancelOperation(id));
//    }
//
//    @GetMapping("/export/account/{accountId}")
//    public ResponseEntity<Resource> exportOperations(
//            @PathVariable Long accountId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @RequestParam(defaultValue = "CSV") String format) {
//        log.info("REST request to export operations for account: {}", accountId);
//
//        byte[] data = operationService.exportOperations(accountId, start, end, format);
//        ByteArrayResource resource = new ByteArrayResource(data);
//
//        String filename = String.format("operations_%d_%s_to_%s.%s",
//                accountId, start.toLocalDate(), end.toLocalDate(), format.toLowerCase());
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                .contentType(format.equals("CSV") ? MediaType.TEXT_PLAIN : MediaType.APPLICATION_JSON)
//                .body(resource);
//    }
//
//    @PostMapping("/{id}/receipt")
//    public ResponseEntity<ReceiptDTO> generateReceipt(@PathVariable Long id) {
//        log.info("REST request to generate receipt for operation: {}", id);
//        return ResponseEntity.ok(operationService.generateReceipt(id));
//    }
//
//    @GetMapping("/pending")
//    public ResponseEntity<List<OperationDTO>> getPendingOperations(
//            @RequestParam(required = false) Long accountId) {
//        log.info("REST request to get pending operations" + (accountId != null ? " for account: " + accountId : ""));
//        return ResponseEntity.ok(operationService.getPendingOperations(accountId));
//    }
//
//    @PostMapping("/{id}/retry")
//    public ResponseEntity<OperationResponse> retryOperation(@PathVariable Long id) {
//        log.info("REST request to retry failed operation: {}", id);
//        return ResponseEntity.ok(operationService.retryOperation(id));
//    }
}
