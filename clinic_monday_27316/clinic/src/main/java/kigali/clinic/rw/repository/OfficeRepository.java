package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Office;

@Repository 
public interface OfficeRepository extends JpaRepository<Office,UUID> {
    
    List<Office> findByName(String name);
    Optional<Office> findByOfficeNumber(int officeN);

    List<Office> findByOfficeNumberStartsWith(int startNumber);

  
    List<Office> findByOfficeNumberEndsWith(int endNumber);
}
