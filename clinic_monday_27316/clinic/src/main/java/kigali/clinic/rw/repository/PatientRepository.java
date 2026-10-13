package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    Optional<Patient> findBySsNumber(String ssNumber);

    @Query("""
        SELECT DISTINCT a.patient
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
    """)
    List<Patient> findPatientsOfDoctor(@Param("doctorId") UUID doctorId);

    @Query("""
        SELECT a.patient
        FROM Appointment a
        GROUP BY a.patient
        HAVING COUNT(a) >= :min
        ORDER BY COUNT(a) DESC
    """)
    List<Patient> findFrequentPatients(@Param("min") long min);
}
