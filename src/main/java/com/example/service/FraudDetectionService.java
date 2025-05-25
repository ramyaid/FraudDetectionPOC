package com.example.service;

import com.example.model.ZelleTransaction;
import com.example.dto.FraudDetectionResult;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class FraudDetectionService {
    private static final String COPILOT_LLM_API_URL = "https://api.githubcopilot.com/v1/chat/completions";

    @Value("${copilot.api.key}")
    private String copilotApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FraudDetectionResult analyzeTransaction(ZelleTransaction newTx, List<ZelleTransaction> history) {
        try {
            // Prepare request payload for Copilot LLM API (OpenAI-compatible)
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", "gpt-4");
            ArrayNode messages = objectMapper.createArrayNode();
            messages.add(objectMapper.createObjectNode()
                .put("role", "system")
                .put("content", "You are an expert AI system specializing in fraud detection within financial transactions. Your primary goal is to identify potentially fraudulent activities by analyzing transactional data and user behavior patterns. You will receive input data containing details of financial transactions, user information, and potentially other relevant context. Based on this data, you must: 1. Analyze: Examine transaction details, user information, and historical data for patterns and anomalies. Look for deviations from normal user behavior and identify potential fraud indicators. Look for location change and transaction from different location within an hour. 2. Evaluate Risk: Assign a risk score to each transaction based on your analysis. A higher score indicates a higher probability of fraud. 3. Provide Explanation: For each transaction flagged as potentially fraudulent (above a defined risk threshold), provide a clear and concise explanation of the factors that contributed to the elevated risk score. This explanation should be easily understandable by a human analyst. 4. Adapt and Learn: Continuously Learn from new data and feedback to improve the accuracy of your fraud detection models. Adapt to evolving fraud tactics and minimize false positives and false negatives. Ensure your analysis is fair, transparent, and unbiased, avoiding discriminatory outcomes. Comply with all relevant data privacy regulations and ethical principles. You will output your findings in a structured JSON format containing: Transaction ID, Risk Score (0-100), Explanation of Risk (if applicable), Recommended Action (e.g. Review, Block, Approve)"));
            ObjectNode userContent = objectMapper.createObjectNode();
            userContent.set("newTransaction", objectMapper.valueToTree(newTx));
            userContent.set("sampleTransactions", objectMapper.valueToTree(history));
            messages.add(objectMapper.createObjectNode()
                .put("role", "user")
                .put("content", userContent.toString()));
            payload.set("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + copilotApiKey);
            HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(COPILOT_LLM_API_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode json = objectMapper.readTree(response.getBody());
                // Parse OpenAI-style response
                String content = json.path("choices").get(0).path("message").path("content").asText();
                JsonNode resultJson = objectMapper.readTree(content);
                FraudDetectionResult result = new FraudDetectionResult();
                result.setTransactionId(resultJson.path("transactionId").asText());
                result.setRiskScore(resultJson.path("riskScore").asInt());
                result.setExplanation(resultJson.path("explanation").asText());
                result.setRecommendedAction(resultJson.path("recommendedAction").asText());
                return result;
            } else {
                throw new RuntimeException("Copilot LLM API error: " + response.getStatusCode());
            }
        } catch (Exception e) {
            FraudDetectionResult fallback = new FraudDetectionResult();
            fallback.setTransactionId(newTx.getId());
            fallback.setRiskScore(0);
            fallback.setExplanation("LLM API error: " + e.getMessage());
            fallback.setRecommendedAction("Review");
            return fallback;
        }
    }
}
