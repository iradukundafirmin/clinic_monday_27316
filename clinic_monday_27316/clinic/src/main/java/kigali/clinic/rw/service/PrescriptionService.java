package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {
    @Autowired private PrescriptionRepository prescriptionRepo;
    @Autowired private AppointmentRepository appointmentRepo;

    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> found = appointmentRepo.findById(appointmentId);
        if (found.isEmpty()) return "The appointment with that id does not exist";
        if (found.get().getStatus() != AppointmentStatus.COMPLETED) return "Prescriptions are only for COMPLETED appointments";
        prescription.setAppointment(found.get());
        prescription.setIssuedDate(LocalDate.now());
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    public Object getByAppointment(UUID appointmentId) {
        if (!appointmentRepo.existsById(appointmentId)) return "The appointment with that id does not exist";
        return prescriptionRepo.findByAppointmentId(appointmentId);
    }

    public List<Prescription> getPatientHistory(String ssNumber) {
        return prescriptionRepo.findPatientPrescriptionHistory(ssNumber);
    }

    public String updatePrescription(UUID id, Prescription data) {
        Optional<Prescription> found = prescriptionRepo.findById(id);
        if (found.isEmpty()) return "The prescription with that id does not exist";
        Prescription prescription = found.get();
        prescription.setMedicineName(data.getMedicineName());
        prescription.setDosage(data.getDosage());
        prescription.setDurationDays(data.getDurationDays());
        prescriptionRepo.save(prescription);
        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        if (!prescriptionRepo.existsById(id)) return "The prescription with that id does not exist";
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    public long countPrescriptions() { return prescriptionRepo.count(); }
}
