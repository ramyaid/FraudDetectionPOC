<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

This project is a Java 17 Spring Boot REST API for Zelle transaction fraud detection. The data model includes id, customerNumber, debtor, creditor, amount, date (UTC timestamp), description, clientInstanceIP. Fraud detection logic should flag transactions from different IPs within an hour for the same user.
