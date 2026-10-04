package HeathTech.HealthTech.Controller;

import HeathTech.HealthTech.Entirty.Hospital;
import HeathTech.HealthTech.Entirty.PatientReportEntity;
import HeathTech.HealthTech.Entirty.Register;
import HeathTech.HealthTech.Repository.PatientReportRepository;
import HeathTech.HealthTech.Repository.registerDB;
import HeathTech.HealthTech.Repository.hospitaldatabase;
import HeathTech.HealthTech.Service.GroqVisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/healthtech/report-ai")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private GroqVisionService groqVisionService;

    @Autowired
    private PatientReportRepository reportRepository;

    @Autowired
    private hospitaldatabase hospitalDatabase;

    @Autowired
    private registerDB registerDb;

    @PostMapping("/analyze-and-save")
    public ResponseEntity<?> analyzeAndSaveReport(
            @RequestParam("username") String username,
            @RequestParam("hospitalId") String hospitalId,
            @RequestParam("base64Image") String base64Image) {

        try {
            // 1. Hospital check
            Hospital hospital = hospitalDatabase.findByUsername(hospitalId);
            if (hospital == null) {
                return new ResponseEntity<>("Hospital not found with ID: " + hospitalId, HttpStatus.NOT_FOUND);
            }

            // 2. Register database fetch user details
            Register user = registerDb.findByUsername(username);
            if (user == null) {
                return new ResponseEntity<>("User not found with username: " + username, HttpStatus.NOT_FOUND);
            }

            // 3. Groq Vision API to image analyze
            String aiAnalysis = groqVisionService.analyzeReportImage(base64Image);

            // 4. Report and user detils save in DB (Sensitive ID omitted)
            PatientReportEntity reportEntity = new PatientReportEntity();
            reportEntity.setUsername(username);
            reportEntity.setHospitalId(hospitalId);
            reportEntity.setName(user.getName());
            reportEntity.setLastname(user.getLastname());
            reportEntity.setContactNumber(user.getContact_number());
            reportEntity.setCity(user.getCity());
            reportEntity.setAiAnalysisResult(aiAnalysis);
            reportRepository.save(reportEntity);

            // 5. Success response return
            return ResponseEntity.ok(Map.of(
                "message", "Report successfully analyzed and saved in database!",
                "aiAnalysis", aiAnalysis,
                "patientName", user.getName() + " " + user.getLastname(),
                "hospitalId", hospitalId
            ));

        } catch (Exception e) {
            return new ResponseEntity<>("Error processing report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     @GetMapping("/analyze-and-save")
     public ResponseEntity<?> getpatientreports( @RequestParam("id") String id){
         PatientReportEntity reportEntity = new PatientReportEntity();

         reportEntity=
         if(reportEntity==null){
             return new ResponseEntity<>("not found with ID: " + hospitalId, HttpStatus.NOT_FOUND);
         }

         reportRepository.;

         
     }

    
}
