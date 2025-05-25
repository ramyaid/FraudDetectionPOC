package com.example.repository;

import com.example.model.ZelleTransaction;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ZelleTransactionRepository {
    // Map<customerNumber, List<ZelleTransaction>>
    private final Map<String, List<ZelleTransaction>> transactions = new ConcurrentHashMap<>();

    public ZelleTransactionRepository() {
        // Initialize with 10 sample transactions per user (for demo, 2 users)
        for (int user = 1; user <= 2; user++) {
            String customerNumber = "CUST" + user;
            List<ZelleTransaction> sampleList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                ZelleTransaction tx = new ZelleTransaction();
                tx.setId(UUID.randomUUID().toString());
                tx.setCustomerNumber(customerNumber);
                tx.setDebtor("Debtor" + user);
                tx.setCreditor("Creditor" + i);
                tx.setAmount(100 + i);
                tx.setDate(Instant.now().minusSeconds(3600 - i * 300)); // spread over last hour
                tx.setDescription("Sample transaction " + i);
                tx.setClientInstanceIP("192.168.1." + (i % 2 == 0 ? "10" : "20"));
                sampleList.add(tx);
            }
            transactions.put(customerNumber, sampleList);
        }
    }

    public List<ZelleTransaction> getTransactionsForCustomer(String customerNumber) {
        return transactions.getOrDefault(customerNumber, new ArrayList<>());
    }

    public void addTransaction(ZelleTransaction tx) {
        transactions.computeIfAbsent(tx.getCustomerNumber(), k -> new ArrayList<>());
        List<ZelleTransaction> list = transactions.get(tx.getCustomerNumber());
        if (list.size() >= 10) {
            list.remove(0); // keep only last 10
        }
        list.add(tx);
    }

    public List<ZelleTransaction> getAllTransactions() {
        return transactions.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
