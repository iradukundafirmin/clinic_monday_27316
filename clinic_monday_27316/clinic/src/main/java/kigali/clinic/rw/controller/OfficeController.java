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
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.AppointmentService;
import kigali.clinic.rw.service.OfficeService;

@RestController
@RequestMapping(value = {"/api/office", "/api/offices"})
public class OfficeController {

    @Autowired
    private OfficeService offServe;

    @Autowired
    private AppointmentService apptServe;

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveOffice(@RequestBody Office office) {
        String returnedMessage = offServe.saveOffice(office);
        if (returnedMessage.equals("Office is saved successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
    }

    @GetMapping("/free")
    public ResponseEntity<?> getFreeOffices() {
        return new ResponseEntity<>(offServe.getFreeOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllOffices() {
        return new ResponseEntity<>(offServe.getAllOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/busiest")
    public ResponseEntity<?> getBusiestOffice() {
        return new ResponseEntity<>(apptServe.getBusiestOffice(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id) {
        Office office = offServe.getOfficeById(id);
        if (office == null) {
            return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(office, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateOffice(@PathVariable UUID id, @RequestBody Office office) {
        String returnedMessage = offServe.updateOffice(id, office);
        if (returnedMessage.equals("Office is updated successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        } else if (returnedMessage.equals("Office not found")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable UUID id) {
        String returnedMessage = offServe.deleteOffice(id);
        if (returnedMessage.equals("Office is deleted successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        } else if (returnedMessage.equals("Office not found")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
    }
}
