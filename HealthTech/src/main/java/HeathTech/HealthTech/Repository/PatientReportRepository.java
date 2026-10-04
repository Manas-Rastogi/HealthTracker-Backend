package HeathTech.HealthTech.Repository;

import HeathTech.HealthTech.Entirty.PatientReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientReportRepository extends MongoRepository<PatientReportEntity, String> {

    // Hospital id base fetch
    List<PatientReportEntity> findByHospitalId(String hospitalId);
}
