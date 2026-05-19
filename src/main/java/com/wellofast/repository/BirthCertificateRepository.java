package com.wellofast.repository;

import com.wellofast.model.BirthCertificate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface BirthCertificateRepository extends MongoRepository<BirthCertificate, String> {
    List<BirthCertificate> findByIssuedByUserIdOrderByCreatedAtDesc(String userId);
    Optional<BirthCertificate> findByCertificateNumber(String num);
    List<BirthCertificate> findByChildNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);
    List<BirthCertificate> findAllByOrderByCreatedAtDesc();
}
