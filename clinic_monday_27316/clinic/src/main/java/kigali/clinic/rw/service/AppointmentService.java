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

    @Autowired
    private kigali.clinic.rw.repository.PrescriptionRepository prescriptionRepo;

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
        if (appointment.getAppointmentDate() == null || appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        String problem = checkPatientAndDoctor(appointment);
        if (problem != null) {
            return problem;
        }

        boolean alreadyBooked = apptRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED);
        if (alreadyBooked) return "Doctor is already booked on that date";

        appointment.setStatus(AppointmentStatus.SCHEDULED);
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
        existing.setPatient(appointment.getPatient());
        existing.setDoctor(appointment.getDoctor());
        apptRepo.save(existing);
        return "Appointment is updated successfully";
    }

    public String deleteAppointment(UUID id) {
        if (!apptRepo.existsById(id)) {
            return "Appointment not found";
        }

        if (prescriptionRepo.existsByAppointmentId(id)) return "That appointment has prescriptions, delete them first";
        apptRepo.deleteById(id);
        return "Appointment is deleted successfully";
    }

    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> found = apptRepo.findById(id);
        if (found.isEmpty()) return "The appointment with that id does not exist";
        Appointment appointment = found.get();
        AppointmentStatus current = appointment.getStatus();
        boolean allowed = (current == AppointmentStatus.SCHEDULED && (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED))
                || (current == AppointmentStatus.CONFIRMED && (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED));
        if (!allowed) return "Cannot change status from " + current + " to " + newStatus;
        appointment.setStatus(newStatus);
        apptRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    public String reschedule(UUID id, LocalDate newDate) {
        Optional<Appointment> found = apptRepo.findById(id);
        if (found.isEmpty()) return "The appointment with that id does not exist";
        Appointment appointment = found.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) return "A COMPLETED appointment cannot be rescheduled";
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) return "A CANCELLED appointment cannot be rescheduled";
        if (newDate == null || newDate.isBefore(LocalDate.now())) return "Appointment date cannot be in the past";
        boolean booked = apptRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED);
        if (booked) return "Doctor is already booked on that date";
        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        apptRepo.save(appointment);
        return "Appointment rescheduled successfully";
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
