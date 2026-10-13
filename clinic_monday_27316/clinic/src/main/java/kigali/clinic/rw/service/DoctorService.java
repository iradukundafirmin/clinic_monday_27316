package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private OfficeRepository offRepo;

    @Autowired
    private SpecializationRepository specRepo;

    @Autowired
    private AppointmentRepository apptRepo;

    private String checkOffice(Doctor doctor, UUID doctorId) {
        if (doctor.getOffice() == null) {
            return null;
        }

        UUID officeId = doctor.getOffice().getId();
        if (officeId == null || !offRepo.existsById(officeId)) {
            return "Office not found";
        }

        Optional<Doctor> owner = docRepo.findByOfficeId(officeId);
        if (owner.isPresent() && !owner.get().getId().equals(doctorId)) {
            return "Office is already assigned to another doctor";
        }
        return null;
    }

    public String saveDoctor(Doctor doctor) {
        String problem = checkOffice(doctor, null);
        if (problem != null) {
            return problem;
        }

        docRepo.save(doctor);
        return "Doctor is saved successfully";
    }

    public List<Doctor> getAllDoctors() {
        return docRepo.findAll();
    }

    public Doctor getDoctorById(UUID id) {
        return docRepo.findById(id).orElse(null);
    }

    public String updateDoctor(UUID id, Doctor doctor) {
        Optional<Doctor> found = docRepo.findById(id);
        if (found.isEmpty()) {
            return "Doctor not found";
        }

        String problem = checkOffice(doctor, id);
        if (problem != null) {
            return problem;
        }

        Doctor existing = found.get();
        existing.setFirstName(doctor.getFirstName());
        existing.setLastName(doctor.getLastName());
        existing.setDateOfBirth(doctor.getDateOfBirth());
        existing.setOffice(doctor.getOffice());
        docRepo.save(existing);
        return "Doctor is updated successfully";
    }

    public String deleteDoctor(UUID id) {
        if (!docRepo.existsById(id)) {
            return "Doctor not found";
        }

        try {
            docRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return "Doctor cannot be deleted because it is in use";
        }
        return "Doctor is deleted successfully";
    }


    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorFound = docRepo.findById(doctorId);
        if (doctorFound.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specializationFound = specRepo.findById(specializationId);
        if (specializationFound.isEmpty()) {
            return "That specialization does not exist";
        }

        Doctor doctor = doctorFound.get();
        Specialization specialization = specializationFound.get();

        if (doctor.getSpecializations().stream()
                .anyMatch(existing -> existing.getId().equals(specializationId))) {
            return "The doctor already has that specialization";
        }

        doctor.getSpecializations().add(specialization);
        docRepo.save(doctor);
        return "Specialization added to the doctor";
    }

    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorFound = docRepo.findById(doctorId);
        if (doctorFound.isEmpty()) return "The doctor with that id does not exist";
        Optional<Specialization> specializationFound = specRepo.findById(specializationId);
        if (specializationFound.isEmpty()) return "That specialization does not exist";
        Doctor doctor = doctorFound.get();
        boolean removed = doctor.getSpecializations().removeIf(s -> s.getId().equals(specializationId));
        if (!removed) return "The doctor does not have that specialization";
        docRepo.save(doctor);
        return "Specialization removed from the doctor";
    }

    public String moveDoctorToOffice(UUID doctorId, int officeNumber) {
        Optional<Doctor> doctorFound = docRepo.findById(doctorId);
        if (doctorFound.isEmpty()) return "The doctor with that id does not exist";
        Optional<kigali.clinic.rw.domain.Office> officeFound = offRepo.findByOfficeNumber(officeNumber);
        if (officeFound.isEmpty()) return "The office with that office number does not exist";
        if (docRepo.existsByOfficeIdAndIdNot(officeFound.get().getId(), doctorId)) return "That office is taken by other doctor";
        Doctor doctor = doctorFound.get();
        doctor.setOffice(officeFound.get());
        docRepo.save(doctor);
        return "Office changed successfully";
    }

    public Object getAgenda(UUID doctorId, LocalDate date) {
        if (!docRepo.existsById(doctorId)) return "The doctor with that id does not exist";
        return apptRepo.findByDoctorIdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
                doctorId, date, AppointmentStatus.CANCELLED);
    }

    public List<Doctor> searchDoctors(String name) {
        return docRepo.searchByName(name);
    }

    public List<Doctor> findDoctorsBySpecialization(String name) {
        return docRepo.findDoctorsBySpecialization(name);
    }

    public List<Doctor> findDoctorsWithoutOffice() {
        return docRepo.findDoctorsWithoutOffice();
    }
}
