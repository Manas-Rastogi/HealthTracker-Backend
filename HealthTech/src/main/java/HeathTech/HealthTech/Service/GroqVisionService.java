package HeathTech.HealthTech.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GroqVisionService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.model}")
    private String modelName; // Properties model name 

    public String analyzeReportImage(String base64Image) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

       // Production-level detailed clinical prompt with clean markdown formatting
        String productionPrompt = 
            "Role: You are an expert Senior Consultant Radiologist and Clinical Diagnostician.\n" +
            "Task: Perform a comprehensive, systematic radiological evaluation of the provided medical imaging (X-ray/MRI).\n\n" +
            "Please structure your analysis using clean Markdown format with explicit headings and short bullet points:\n\n" +
            "### 1. MODALITY & TECHNICAL QUALITY\n" +
            "- Identify the imaging type, anatomical region, projection/view, and technical adequacy.\n\n" +
            "### 2. SYSTEMATIC OBSERVATIONS\n" +
            "- Detail normal/abnormal findings (bone density, joint spaces, soft tissues, fractures, or structural anomalies).\n\n" +
            "### 3. RADIOLOGICAL IMPRESSION / DIFFERENTIALS\n" +
            "- Provide a prioritized list of primary findings or potential diagnoses based on visual evidence.\n\n" +
            "### 4. CLINICAL RECOMMENDATIONS & URGENCY\n" +
            "- Indicate severity level (Routine, Urgent, or Emergency) and required specialist consultation or next steps.\n\n" +
            "### 5. DISCLAIMER\n" +
            "- State clearly that this is an AI-assisted preliminary analysis and must be clinically verified by a licensed medical practitioner.\n\n" +
            "Keep the language professional, concise, well-spaced, and easy to read for both the patient and the physician.";

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", productionPrompt);

        Map<String, Object> imageUrl = new HashMap<>();
        imageUrl.put("url", "data:image/jpeg;base64," + base64Image);

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        imageContent.put("image_url", imageUrl);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", Arrays.asList(textContent, imageContent));

        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName); // use variable
        body.put("messages", Arrays.asList(message));
        body.put("max_tokens", 1500); // Increased tokens for detailed breakdown
        body.put("temperature", 0.2); // Lower temperature for high clinical accuracy & less randomness

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                    return (String) msg.get("content");
                }
            }
        } catch (Exception e) {
            return "Error while connecting to Groq API: " + e.getMessage();
        }
        return "Could not analyze the report.";
    }
}
