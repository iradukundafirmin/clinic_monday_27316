package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository apptRepo;

    @Autowired
    private PatientRepository patRepo;

    @Autowired
    private DoctorRepository docRepo;

    private String checkPatientAndDoctor(Appointment appointment) {
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            return "Patient is required";
        }
        if (!patRepo.existsById(appointment.getPatient().getId())) {
            return "Patient not found";
        }

        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return "Doctor is required";
        }
        if (!docRepo.existsById(appointment.getDoctor().getId())) {
            return "Doctor not found";
        }

        return null;
    }

    public String saveAppointment(Appointment appointment) {
        String problem = checkPatientAndDoctor(appointment);
        if (problem != null) {
            return problem;
        }

        boolean alreadyBooked = apptRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);

        if (alreadyBooked) {
            return "Doctor is already booked on that date";
        }

        apptRepo.save(appointment);
        return "Appointment is saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return apptRepo.findAll();
    }

    public Appointment getAppointmentById(UUID id) {
        return apptRepo.findById(id).orElse(null);
    }

    public String updateAppointment(UUID id, Appointment appointment) {
        Optional<Appointment> found = apptRepo.findById(id);
        if (found.isEmpty()) {
            return "Appointment not found";
        }

        String problem = checkPatientAndDoctor(appointment);
        if (problem != null) {
            return problem;
        }

        Appointment existing = found.get();
        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setReason(appointment.getReason());
        existing.setStatus(appointment.getStatus());
        existing.setPatient(appointment.getPatient());
        existing.setDoctor(appointment.getDoctor());
        apptRepo.save(existing);
        return "Appointment is updated successfully";
    }

    public String deleteAppointment(UUID id) {
        if (!apptRepo.existsById(id)) {
            return "Appointment not found";
        }

        apptRepo.deleteById(id);
        return "Appointment is deleted successfully";
    }

    public List<Appointment> findAppointmentsByStatus(AppointmentStatus status) {
        return apptRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> findAppointmentsBetween(LocalDate start, LocalDate end) {
        return apptRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> countAppointmentsByStatus() {
        return apptRepo.countAppointmentsByStatus();
    }

    public Object getBusiestOffice() {
        List<Object[]> results = apptRepo.findBusiestOffice(PageRequest.of(0, 1));
        if (results.isEmpty()) {
            return "No appointments yet";
        }
        return results.get(0);
    }

    public String cancelAppointmentsForDoctorOnDate(UUID doctorId, LocalDate date) {
        int cancelled = apptRepo.cancelAppointmentsForDoctorOnDate(doctorId, date);
        return cancelled + " appointments cancelled";
    }
}
