package com.example.controller;

import com.example.model.ZelleTransaction;
import com.example.dto.FraudDetectionResult;
import com.example.repository.ZelleTransactionRepository;
import com.example.service.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class ZelleTransactionController {
    @Autowired
    private ZelleTransactionRepository repository;
    @Autowired
    private FraudDetectionService fraudDetectionService;

    @PostMapping
    public FraudDetectionResult createTransaction(@RequestBody ZelleTransaction tx) {
        // Assign ID and UTC timestamp if not provided
        if (tx.getId() == null) tx.setId(UUID.randomUUID().toString());
        if (tx.getDate() == null) tx.setDate(Instant.now());
        repository.addTransaction(tx);
        List<ZelleTransaction> history = repository.getTransactionsForCustomer(tx.getCustomerNumber());
        FraudDetectionResult result = fraudDetectionService.analyzeTransaction(tx, history);
        return result;
    }

    @GetMapping("/sample/{customerNumber}")
    public List<ZelleTransaction> getSampleTransactions(@PathVariable String customerNumber) {
        return repository.getTransactionsForCustomer(customerNumber);
    }
}
