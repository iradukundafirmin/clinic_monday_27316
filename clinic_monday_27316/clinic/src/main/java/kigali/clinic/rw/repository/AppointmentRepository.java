package kigali.clinic.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate start,
            LocalDate end);

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(
            UUID doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status);

    @Query("""
        SELECT a.status, COUNT(a)
        FROM Appointment a
        GROUP BY a.status
    """)
    List<Object[]> countAppointmentsByStatus();

    @Query("""
        SELECT o.name, o.officeNumber, COUNT(a)
        FROM Appointment a
        JOIN a.doctor d
        JOIN d.office o
        GROUP BY o.name, o.officeNumber
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> findBusiestOffice(Pageable pageable);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Appointment a
        SET a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED
        WHERE a.doctor.id = :doctorId
          AND a.appointmentDate = :date
          AND a.status <> kigali.clinic.rw.domain.AppointmentStatus.COMPLETED
    """)
    int cancelAppointmentsForDoctorOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date);
}
