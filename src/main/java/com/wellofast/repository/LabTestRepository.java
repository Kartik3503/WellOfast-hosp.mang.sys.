package com.wellofast.repository;

import com.wellofast.model.LabTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LabTestRepository extends MongoRepository<LabTest, String> {
    List<LabTest> findByActiveTrueOrderByNameAsc();
    List<LabTest> findByCategoryAndActiveTrueOrderByNameAsc(String category);
    List<LabTest> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
