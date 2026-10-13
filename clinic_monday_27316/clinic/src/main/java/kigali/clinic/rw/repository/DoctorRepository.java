package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByOfficeId(UUID officeId);

    boolean existsByOfficeIdAndIdNot(UUID officeId, UUID id);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.lastName ASC")
    List<Doctor> searchByName(@Param("name") String name);

    @Query("""
        SELECT DISTINCT d
        FROM Doctor d
        JOIN d.specializations s
        WHERE LOWER(s.name) = LOWER(:name)
    """)
    List<Doctor> findDoctorsBySpecialization(@Param("name") String name);

    @Query("""
        SELECT d
        FROM Doctor d
        WHERE d.office IS NULL
        ORDER BY d.lastName ASC
    """)
    List<Doctor> findDoctorsWithoutOffice();
}
