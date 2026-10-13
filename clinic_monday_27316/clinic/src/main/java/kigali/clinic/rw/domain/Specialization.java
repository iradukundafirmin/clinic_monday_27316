package kigali.clinic.rw.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity 
public class Specialization {

    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
     private UUID id;

     @Column (name="name")
     private String name;

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public UUID getId() {
         return id;
     }

     @JsonIgnore
     @ManyToMany(mappedBy = "specializations")
     private List<Doctor> doctors = new ArrayList<>();

     public List<Doctor> getDoctors() {
         return doctors;
     }

     public void setDoctors(List<Doctor> doctors) {
         this.doctors = doctors;
     }

}