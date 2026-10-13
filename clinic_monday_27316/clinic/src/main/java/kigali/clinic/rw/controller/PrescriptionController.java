package kigali.clinic.rw.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping({"/api/prescription", "/api/prescriptions"})
public class PrescriptionController {
    @Autowired private PrescriptionService prescriptionService;

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestParam UUID appointmentId, @RequestBody Prescription prescription) {
        String result = prescriptionService.savePrescription(appointmentId, prescription);
        if (result.equals("Prescription saved successfully")) return new ResponseEntity<>(result, HttpStatus.CREATED);
        if (result.equals("The appointment with that id does not exist")) return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> byAppointment(@PathVariable UUID appointmentId) {
        Object result = prescriptionService.getByAppointment(appointmentId);
        if (result instanceof String) return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<?> byPatient(@PathVariable String ssNumber) {
        return new ResponseEntity<>(prescriptionService.getPatientHistory(ssNumber), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Prescription prescription) {
        String result = prescriptionService.updatePrescription(id, prescription);
        return new ResponseEntity<>(result, result.equals("The prescription with that id does not exist") ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        String result = prescriptionService.deletePrescription(id);
        return new ResponseEntity<>(result, result.equals("The prescription with that id does not exist") ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }
}
