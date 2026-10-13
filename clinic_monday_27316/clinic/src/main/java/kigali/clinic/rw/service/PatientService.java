package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import java.time.LocalDate;
import java.util.Arrays;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patRepo;

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private kigali.clinic.rw.repository.AppointmentRepository apptRepo;

    public String savePatient(Patient patient) {
        patRepo.save(patient);
        return "Patient is saved successfully";
    }

    public List<Patient> getAllPatients() {
        return patRepo.findAll();
    }

    public Patient getPatientById(UUID id) {
        return patRepo.findById(id).orElse(null);
    }

    public String updatePatient(UUID id, Patient patient) {
        Optional<Patient> found = patRepo.findById(id);
        if (found.isEmpty()) {
            return "Patient not found";
        }

        Patient existing = found.get();
        existing.setFirstName(patient.getFirstName());
        existing.setLastName(patient.getLastName());
        existing.setDateOfBirth(patient.getDateOfBirth());
        existing.setSsNumber(patient.getSsNumber());
        patRepo.save(existing);
        return "Patient is updated successfully";
    }

    public String deletePatient(UUID id) {
        if (!patRepo.existsById(id)) {
            return "Patient not found";
        }

        try {
            patRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return "Patient cannot be deleted because it is in use";
        }
        return "Patient is deleted successfully";
    }

    public Object findUpcomingAppointments(String ssNumber) {
        if (patRepo.findBySsNumber(ssNumber).isEmpty()) return "That patient with that ssnumber does not exist";
        return apptRepo.findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                ssNumber, LocalDate.now(), Arrays.asList(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED));
    }

    public List<Patient> findPatientsByLastName(String lastName) {
        return patRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public Object findPatientsOfDoctor(UUID doctorId) {
        if (!docRepo.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return patRepo.findPatientsOfDoctor(doctorId);
    }

    public List<Patient> findFrequentPatients(long min) {
        return patRepo.findFrequentPatients(min);
    }
}
