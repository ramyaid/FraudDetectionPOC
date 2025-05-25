package com.example.model;

import java.time.Instant;

public class ZelleTransaction {
    private String id;
    private String customerNumber;
    private String debtor;
    private String creditor;
    private double amount;
    private Instant date; // UTC timestamp
    private String description;
    private String clientInstanceIP;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
    public String getDebtor() { return debtor; }
    public void setDebtor(String debtor) { this.debtor = debtor; }
    public String getCreditor() { return creditor; }
    public void setCreditor(String creditor) { this.creditor = creditor; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Instant getDate() { return date; }
    public void setDate(Instant date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getClientInstanceIP() { return clientInstanceIP; }
    public void setClientInstanceIP(String clientInstanceIP) { this.clientInstanceIP = clientInstanceIP; }
}
