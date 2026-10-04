package HeathTech.HealthTech.Entirty;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "patient_reports")
public class PatientReportEntity {
    @Id
    private String id;
    private String username;
    private String hospitalId;
    
    // User details fetched from Register collection
    private String name;
    private String lastname;
    private String contactNumber;
    private String city;
    
    private String aiAnalysisResult;
    private LocalDateTime timestamp = LocalDateTime.now();
}
