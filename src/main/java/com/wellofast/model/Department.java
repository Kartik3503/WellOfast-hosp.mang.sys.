package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "departments")
public class Department {
    @Id
    private String id;
    private String name;
    private String head;          // name of HOD
    private String headUserId;    // link to User
    private String description;
    private int staffCount;
    private boolean active = true;
    private LocalDateTime createdAt;
}
