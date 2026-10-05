package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping(value = {"/api/appointment", "/api/appointments"})
public class AppointmentController {

    @Autowired
    private AppointmentService apptServe;

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveAppointment(@RequestBody Appointment appointment) {
        String returnedMessage = apptServe.saveAppointment(appointment);
        if (returnedMessage.equals("Appointment is saved successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        } else if (returnedMessage.equals("Patient not found") || returnedMessage.equals("Doctor not found")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.equals("Doctor is already booked on that date")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllAppointments() {
        return new ResponseEntity<>(apptServe.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/by-status")
    public ResponseEntity<?> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return new ResponseEntity<>(apptServe.findAppointmentsByStatus(status), HttpStatus.OK);
    }

    @GetMapping(value = "/between")
    public ResponseEntity<?> getAppointmentsBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(apptServe.findAppointmentsBetween(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping(value = "/stats/by-status")
    public ResponseEntity<?> getAppointmentsByStatusStats() {
        return new ResponseEntity<>(apptServe.countAppointmentsByStatus(), HttpStatus.OK);
    }

    @PatchMapping(value = "/cancel-day")
    public ResponseEntity<?> cancelAppointmentsForDay(
            @RequestParam UUID doctorId,
            @RequestParam String date) {
        LocalDate appointmentDate = LocalDate.parse(date);
        return new ResponseEntity<>(
                apptServe.cancelAppointmentsForDoctorOnDate(doctorId, appointmentDate),
                HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id) {
        Appointment appointment = apptServe.getAppointmentById(id);
        if (appointment == null) {
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment) {
        String returnedMessage = apptServe.updateAppointment(id, appointment);
        if (returnedMessage.equals("Appointment is updated successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        } else if (returnedMessage.equals("Appointment not found")
                || returnedMessage.equals("Patient not found")
                || returnedMessage.equals("Doctor not found")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        String returnedMessage = apptServe.deleteAppointment(id);
        if (returnedMessage.equals("Appointment is deleted successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }
}
