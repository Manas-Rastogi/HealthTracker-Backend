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

    public String analyzeReportImage(String base64Image) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // Production-level detailed clinical prompt for accurate medical imaging analysis
        String productionPrompt = 
            "Role: You are an expert Senior Consultant Radiologist and Clinical Diagnostician.\n" +
            "Task: Perform a comprehensive, systematic radiological evaluation of the provided medical imaging (X-ray/MRI).\n\n" +
            "Please structure your analysis into the following clear clinical sections:\n" +
            "1. MODALITY & TECHNICAL QUALITY: Identify the imaging type, anatomical region, projection/view, and overall technical adequacy.\n" +
            "2. SYSTEMATIC OBSERVATIONS: Detail all normal and abnormal findings methodically (e.g., bone density, joint spaces, soft tissues, opacities, fractures, effusions, or structural anomalies).\n" +
            "3. RADIOLOGICAL IMPRESSION / DIFFERENTIALS: Provide a prioritized list of primary findings or potential diagnoses based on the visual evidence.\n" +
            "4. CLINICAL RECOMMENDATIONS & URGENCY: Indicate the clinical severity level (Routine, Urgent, or Emergency) and suggest whether immediate specialist consultation or further correlative imaging/labs are required.\n" +
            "5. DISCLAIMER: State clearly that this is an AI-assisted preliminary analysis and must be clinically correlated and verified by a licensed medical practitioner.\n\n" +
            "Keep the language professional, precise, and easily understandable for both the patient and the attending physician.";

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
        body.put("model", "llama-3.2-11b-vision-preview");
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
