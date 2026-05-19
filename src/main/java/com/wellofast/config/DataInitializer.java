package com.wellofast.config;

import com.wellofast.model.*;
import com.wellofast.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(UserRepository ur, DepartmentRepository dr,
                                   DoctorScheduleRepository schedRepo,
                                   AppointmentRepository apptRepo,
                                   PrescriptionRepository prescRepo,
                                   MedicalRecordRepository recordRepo,
                                   NotificationRepository notifRepo,
                                   ChatMessageRepository chatRepo,
                                   LabTestRepository labTestRepo,
                                   PasswordEncoder enc) {
        return args -> {
            if (ur.count() == 0) {
                // Departments
                String[] depts = {"Cardiology","Neurology","Orthopedics","Pediatrics","General Medicine","Emergency","Radiology","Pathology"};
                for (String d : depts) {
                    Department dept = new Department();
                    dept.setName(d); dept.setDescription(d + " Department"); dept.setStaffCount(0);
                    dept.setActive(true); dept.setCreatedAt(LocalDateTime.now());
                    dr.save(dept);
                }

                // Admin
                User admin = new User();
                admin.setFullName("Dr. Rajesh Kumar"); admin.setUsername("admin"); admin.setPassword(enc.encode("admin123"));
                admin.setEmail("admin@wellofast.com"); admin.setRole("ADMIN"); admin.setDepartment("General Medicine");
                admin.setDesignation("Chief Medical Officer"); admin.setPhone("+91-9876543210");
                admin.setGender("Male"); admin.setJoiningDate(LocalDate.of(2020,1,15));
                admin.setSalary(150000); admin.setEmployeeId("WO-001"); admin.setLicenseNumber("MCI-98765");
                admin.setHospitalName("WellOfast Hospital"); admin.setHospitalRegNumber("HOSP-WO-2024-001");
                admin.setActive(true); admin.setCreatedAt(LocalDateTime.now()); admin.setUpdatedAt(LocalDateTime.now());
                admin.setSpecialization("General Medicine");
                ur.save(admin);

                // Doctor 1 - Cardiology
                User doc1 = new User();
                doc1.setFullName("Dr. Anita Desai"); doc1.setUsername("doctor"); doc1.setPassword(enc.encode("doctor123"));
                doc1.setEmail("anita@wellofast.com"); doc1.setRole("DOCTOR"); doc1.setDepartment("Cardiology");
                doc1.setDesignation("Senior Cardiologist"); doc1.setSpecialization("Cardiology");
                doc1.setPhone("+91-9876543211"); doc1.setGender("Female");
                doc1.setJoiningDate(LocalDate.of(2021,6,1)); doc1.setSalary(120000);
                doc1.setEmployeeId("WO-002"); doc1.setLicenseNumber("MCI-56789");
                doc1.setHospitalName("WellOfast Hospital"); doc1.setHospitalRegNumber("HOSP-WO-2024-001");
                doc1.setActive(true); doc1.setCreatedAt(LocalDateTime.now()); doc1.setUpdatedAt(LocalDateTime.now());
                doc1 = ur.save(doc1);

                // Doctor 2 - Neurology
                User doc2 = new User();
                doc2.setFullName("Dr. Vikram Patel"); doc2.setUsername("doctor2"); doc2.setPassword(enc.encode("doctor123"));
                doc2.setEmail("vikram@wellofast.com"); doc2.setRole("DOCTOR"); doc2.setDepartment("Neurology");
                doc2.setDesignation("Neurologist"); doc2.setSpecialization("Neurology");
                doc2.setPhone("+91-9876543213"); doc2.setGender("Male");
                doc2.setJoiningDate(LocalDate.of(2022,1,10)); doc2.setSalary(130000);
                doc2.setEmployeeId("WO-004"); doc2.setLicenseNumber("MCI-34567");
                doc2.setHospitalName("WellOfast Hospital"); doc2.setHospitalRegNumber("HOSP-WO-2024-001");
                doc2.setActive(true); doc2.setCreatedAt(LocalDateTime.now()); doc2.setUpdatedAt(LocalDateTime.now());
                doc2 = ur.save(doc2);

                // Doctor 3 - Orthopedics
                User doc3 = new User();
                doc3.setFullName("Dr. Meena Sharma"); doc3.setUsername("doctor3"); doc3.setPassword(enc.encode("doctor123"));
                doc3.setEmail("meena@wellofast.com"); doc3.setRole("DOCTOR"); doc3.setDepartment("Orthopedics");
                doc3.setDesignation("Orthopedic Surgeon"); doc3.setSpecialization("Orthopedics");
                doc3.setPhone("+91-9876543214"); doc3.setGender("Female");
                doc3.setJoiningDate(LocalDate.of(2020,9,15)); doc3.setSalary(140000);
                doc3.setEmployeeId("WO-005"); doc3.setLicenseNumber("MCI-67890");
                doc3.setHospitalName("WellOfast Hospital"); doc3.setHospitalRegNumber("HOSP-WO-2024-001");
                doc3.setActive(true); doc3.setCreatedAt(LocalDateTime.now()); doc3.setUpdatedAt(LocalDateTime.now());
                doc3 = ur.save(doc3);

                // Employee
                User emp = new User();
                emp.setFullName("Priya Sharma"); emp.setUsername("employee"); emp.setPassword(enc.encode("emp123"));
                emp.setEmail("priya@wellofast.com"); emp.setRole("EMPLOYEE"); emp.setDepartment("General Medicine");
                emp.setDesignation("Front Desk Executive"); emp.setPhone("+91-9876543212"); emp.setGender("Female");
                emp.setJoiningDate(LocalDate.of(2023,3,10)); emp.setSalary(35000);
                emp.setEmployeeId("WO-003");
                emp.setActive(true); emp.setCreatedAt(LocalDateTime.now()); emp.setUpdatedAt(LocalDateTime.now());
                ur.save(emp);

                // ── Patient User ──
                User patient = new User();
                patient.setFullName("Rahul Verma"); patient.setUsername("patient"); patient.setPassword(enc.encode("patient123"));
                patient.setEmail("rahul@gmail.com"); patient.setRole("PATIENT");
                patient.setPhone("+91-9988776655"); patient.setGender("Male");
                patient.setDateOfBirth(LocalDate.of(1995,3,20));
                patient.setAddress("42 MG Road"); patient.setCity("Mumbai"); patient.setState("Maharashtra");
                patient.setActive(true); patient.setCreatedAt(LocalDateTime.now()); patient.setUpdatedAt(LocalDateTime.now());
                patient = ur.save(patient);

                // ── Doctor Schedules ──
                DoctorSchedule sched1 = new DoctorSchedule();
                sched1.setDoctorId(doc1.getId()); sched1.setDoctorName(doc1.getFullName());
                sched1.setSpecialization("Cardiology"); sched1.setDepartment("Cardiology");
                sched1.setQualification("MBBS, MD (Cardiology), DM");
                sched1.setConsultationFee(800);
                sched1.setAvailableDays(Arrays.asList("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"));
                sched1.setStartTime(LocalTime.of(9,0)); sched1.setEndTime(LocalTime.of(16,0));
                sched1.setMaxPatientsPerDay(15); sched1.setActive(true);
                schedRepo.save(sched1);

                DoctorSchedule sched2 = new DoctorSchedule();
                sched2.setDoctorId(doc2.getId()); sched2.setDoctorName(doc2.getFullName());
                sched2.setSpecialization("Neurology"); sched2.setDepartment("Neurology");
                sched2.setQualification("MBBS, MD (Neurology)");
                sched2.setConsultationFee(1000);
                sched2.setAvailableDays(Arrays.asList("MONDAY","WEDNESDAY","FRIDAY"));
                sched2.setStartTime(LocalTime.of(10,0)); sched2.setEndTime(LocalTime.of(15,0));
                sched2.setMaxPatientsPerDay(12); sched2.setActive(true);
                schedRepo.save(sched2);

                DoctorSchedule sched3 = new DoctorSchedule();
                sched3.setDoctorId(doc3.getId()); sched3.setDoctorName(doc3.getFullName());
                sched3.setSpecialization("Orthopedics"); sched3.setDepartment("Orthopedics");
                sched3.setQualification("MBBS, MS (Ortho), Fellowship Joint Replacement");
                sched3.setConsultationFee(700);
                sched3.setAvailableDays(Arrays.asList("TUESDAY","THURSDAY","SATURDAY"));
                sched3.setStartTime(LocalTime.of(9,30)); sched3.setEndTime(LocalTime.of(14,30));
                sched3.setMaxPatientsPerDay(18); sched3.setActive(true);
                schedRepo.save(sched3);

                DoctorSchedule schedAdmin = new DoctorSchedule();
                schedAdmin.setDoctorId(admin.getId()); schedAdmin.setDoctorName(admin.getFullName());
                schedAdmin.setSpecialization("General Medicine"); schedAdmin.setDepartment("General Medicine");
                schedAdmin.setQualification("MBBS, MD (Medicine)");
                schedAdmin.setConsultationFee(500);
                schedAdmin.setAvailableDays(Arrays.asList("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"));
                schedAdmin.setStartTime(LocalTime.of(8,0)); schedAdmin.setEndTime(LocalTime.of(17,0));
                schedAdmin.setMaxPatientsPerDay(25); schedAdmin.setActive(true);
                schedRepo.save(schedAdmin);

                // ── Sample Appointments for Patient ──
                Appointment appt1 = new Appointment();
                appt1.setPatientName(patient.getFullName()); appt1.setBookedByPatientUserId(patient.getId());
                appt1.setDoctorId(doc1.getId()); appt1.setDoctorName(doc1.getFullName());
                appt1.setDepartment("Cardiology"); appt1.setDate(LocalDate.now().plusDays(3));
                appt1.setTime(LocalTime.of(10,0)); appt1.setType("Checkup");
                appt1.setFee(800); appt1.setStatus("SCHEDULED"); appt1.setCreatedAt(LocalDateTime.now());
                apptRepo.save(appt1);

                Appointment appt2 = new Appointment();
                appt2.setPatientName(patient.getFullName()); appt2.setBookedByPatientUserId(patient.getId());
                appt2.setDoctorId(doc2.getId()); appt2.setDoctorName(doc2.getFullName());
                appt2.setDepartment("Neurology"); appt2.setDate(LocalDate.now().minusDays(10));
                appt2.setTime(LocalTime.of(11,30)); appt2.setType("Consultation");
                appt2.setFee(1000); appt2.setStatus("COMPLETED"); appt2.setCreatedAt(LocalDateTime.now().minusDays(10));
                apptRepo.save(appt2);

                Appointment appt3 = new Appointment();
                appt3.setPatientName(patient.getFullName()); appt3.setBookedByPatientUserId(patient.getId());
                appt3.setDoctorId(doc3.getId()); appt3.setDoctorName(doc3.getFullName());
                appt3.setDepartment("Orthopedics"); appt3.setDate(LocalDate.now().minusDays(30));
                appt3.setTime(LocalTime.of(9,30)); appt3.setType("Follow-up");
                appt3.setFee(700); appt3.setStatus("COMPLETED"); appt3.setCreatedAt(LocalDateTime.now().minusDays(30));
                apptRepo.save(appt3);

                // ── Sample Medical Records ──
                MedicalRecord rec1 = new MedicalRecord();
                rec1.setPatientUserId(patient.getId()); rec1.setPatientName(patient.getFullName());
                rec1.setDoctorId(doc1.getId()); rec1.setDoctorName(doc1.getFullName());
                rec1.setDepartment("Cardiology"); rec1.setType("Checkup");
                rec1.setDiagnosis("Mild hypertension detected");
                rec1.setTreatment("Lifestyle changes, low-sodium diet, regular exercise");
                rec1.setNotes("Patient advised to monitor BP daily. Follow-up in 2 weeks.");
                rec1.setBloodPressure("140/90"); rec1.setHeartRate("78");
                rec1.setTemperature("98.4"); rec1.setWeight("75");
                rec1.setOxygenSaturation("97");
                rec1.setDate(LocalDate.now().minusDays(10));
                rec1.setCreatedAt(LocalDateTime.now().minusDays(10));
                recordRepo.save(rec1);

                MedicalRecord rec2 = new MedicalRecord();
                rec2.setPatientUserId(patient.getId()); rec2.setPatientName(patient.getFullName());
                rec2.setDoctorId(doc3.getId()); rec2.setDoctorName(doc3.getFullName());
                rec2.setDepartment("Orthopedics"); rec2.setType("Procedure");
                rec2.setDiagnosis("Right knee ligament sprain (Grade II)");
                rec2.setTreatment("Knee brace, physiotherapy sessions, pain management");
                rec2.setNotes("MRI confirmed partial ACL tear. Conservative treatment recommended.");
                rec2.setBloodPressure("120/80"); rec2.setHeartRate("72");
                rec2.setTemperature("98.6"); rec2.setWeight("75");
                rec2.setProcedures(Arrays.asList("Knee X-Ray", "MRI Scan", "Joint Aspiration"));
                rec2.setInjections(Arrays.asList("Corticosteroid Injection (Triamcinolone 40mg)", "Hyaluronic Acid Injection"));
                rec2.setDate(LocalDate.now().minusDays(30));
                rec2.setCreatedAt(LocalDateTime.now().minusDays(30));
                recordRepo.save(rec2);

                MedicalRecord rec3 = new MedicalRecord();
                rec3.setPatientUserId(patient.getId()); rec3.setPatientName(patient.getFullName());
                rec3.setDoctorId(doc2.getId()); rec3.setDoctorName(doc2.getFullName());
                rec3.setDepartment("Neurology"); rec3.setType("Lab Test");
                rec3.setDiagnosis("Migraine evaluation");
                rec3.setTreatment("Prescribed sumatriptan for acute attacks");
                rec3.setLabTestName("Complete Blood Count (CBC)"); rec3.setLabResult("WBC: 7.2, RBC: 4.8, Hemoglobin: 14.2");
                rec3.setLabNormalRange("WBC: 4.5-11.0, RBC: 4.5-5.5, Hb: 13.5-17.5");
                rec3.setLabStatus("Normal");
                rec3.setDate(LocalDate.now().minusDays(15));
                rec3.setCreatedAt(LocalDateTime.now().minusDays(15));
                recordRepo.save(rec3);

                MedicalRecord rec4 = new MedicalRecord();
                rec4.setPatientUserId(patient.getId()); rec4.setPatientName(patient.getFullName());
                rec4.setDoctorId(doc1.getId()); rec4.setDoctorName(doc1.getFullName());
                rec4.setDepartment("Cardiology"); rec4.setType("Lab Test");
                rec4.setDiagnosis("Lipid profile screening");
                rec4.setLabTestName("Lipid Profile");
                rec4.setLabResult("Total Cholesterol: 245, LDL: 160, HDL: 38, Triglycerides: 210");
                rec4.setLabNormalRange("TC: <200, LDL: <100, HDL: >40, TG: <150");
                rec4.setLabStatus("Abnormal");
                rec4.setNotes("Elevated LDL and triglycerides. Statin therapy initiated.");
                rec4.setDate(LocalDate.now().minusDays(10));
                rec4.setCreatedAt(LocalDateTime.now().minusDays(10));
                recordRepo.save(rec4);

                // ── Sample Prescriptions ──
                Prescription presc1 = new Prescription();
                presc1.setPatientUserId(patient.getId()); presc1.setPatientName(patient.getFullName());
                presc1.setDoctorId(doc1.getId()); presc1.setDoctorName(doc1.getFullName());
                presc1.setDepartment("Cardiology"); presc1.setDiagnosis("Hypertension & High Cholesterol");
                presc1.setInstructions("Regular BP monitoring. Low sodium diet. Exercise 30 min daily. Follow-up after 2 weeks.");
                presc1.setDate(LocalDate.now().minusDays(10));
                presc1.setCreatedAt(LocalDateTime.now().minusDays(10));

                Medicine med1 = new Medicine();
                med1.setName("Amlodipine"); med1.setType("Tablet"); med1.setDosage("5mg");
                med1.setFrequency("Once daily"); med1.setDuration("30 days");
                med1.setInstructions("Take in the morning with water");

                Medicine med2 = new Medicine();
                med2.setName("Atorvastatin"); med2.setType("Tablet"); med2.setDosage("10mg");
                med2.setFrequency("Once daily"); med2.setDuration("30 days");
                med2.setInstructions("Take at bedtime");

                Medicine med3 = new Medicine();
                med3.setName("Ecosprin"); med3.setType("Tablet"); med3.setDosage("75mg");
                med3.setFrequency("Once daily"); med3.setDuration("30 days");
                med3.setInstructions("After lunch");

                presc1.setMedicines(Arrays.asList(med1, med2, med3));
                prescRepo.save(presc1);

                Prescription presc2 = new Prescription();
                presc2.setPatientUserId(patient.getId()); presc2.setPatientName(patient.getFullName());
                presc2.setDoctorId(doc3.getId()); presc2.setDoctorName(doc3.getFullName());
                presc2.setDepartment("Orthopedics"); presc2.setDiagnosis("Knee Ligament Sprain");
                presc2.setInstructions("Apply ice pack for 20 min every 4 hours. Use knee brace during walking. Avoid stairs.");
                presc2.setDate(LocalDate.now().minusDays(30));
                presc2.setCreatedAt(LocalDateTime.now().minusDays(30));

                Medicine med4 = new Medicine();
                med4.setName("Diclofenac Sodium"); med4.setType("Tablet"); med4.setDosage("50mg");
                med4.setFrequency("Twice daily"); med4.setDuration("10 days");
                med4.setInstructions("After meals");

                Medicine med5 = new Medicine();
                med5.setName("Pantoprazole"); med5.setType("Capsule"); med5.setDosage("40mg");
                med5.setFrequency("Once daily"); med5.setDuration("10 days");
                med5.setInstructions("Before breakfast (for stomach protection)");

                Medicine med6 = new Medicine();
                med6.setName("Volini Gel"); med6.setType("Ointment"); med6.setDosage("Apply locally");
                med6.setFrequency("Three times daily"); med6.setDuration("14 days");
                med6.setInstructions("Apply on knee, do not bandage");

                Medicine med7 = new Medicine();
                med7.setName("Methylcobalamin"); med7.setType("Injection"); med7.setDosage("1500mcg");
                med7.setFrequency("Alternate days"); med7.setDuration("5 doses");
                med7.setInstructions("Intramuscular injection at hospital");

                presc2.setMedicines(Arrays.asList(med4, med5, med6, med7));
                prescRepo.save(presc2);

                Prescription presc3 = new Prescription();
                presc3.setPatientUserId(patient.getId()); presc3.setPatientName(patient.getFullName());
                presc3.setDoctorId(doc2.getId()); presc3.setDoctorName(doc2.getFullName());
                presc3.setDepartment("Neurology"); presc3.setDiagnosis("Migraine");
                presc3.setInstructions("Avoid screen time. Rest in dark room during attacks. Stay hydrated.");
                presc3.setDate(LocalDate.now().minusDays(15));
                presc3.setCreatedAt(LocalDateTime.now().minusDays(15));

                Medicine med8 = new Medicine();
                med8.setName("Sumatriptan"); med8.setType("Tablet"); med8.setDosage("50mg");
                med8.setFrequency("As needed (max 2/day)"); med8.setDuration("SOS");
                med8.setInstructions("Take at onset of migraine");

                Medicine med9 = new Medicine();
                med9.setName("Amitriptyline"); med9.setType("Tablet"); med9.setDosage("10mg");
                med9.setFrequency("Once at bedtime"); med9.setDuration("30 days");
                med9.setInstructions("For migraine prevention");

                Medicine med10 = new Medicine();
                med10.setName("Ondansetron"); med10.setType("Syrup"); med10.setDosage("5ml");
                med10.setFrequency("As needed"); med10.setDuration("SOS");
                med10.setInstructions("For nausea during migraine attacks");

                presc3.setMedicines(Arrays.asList(med8, med9, med10));
                prescRepo.save(presc3);

                // ══════════════════════════════════════════
                //  SEED NOTIFICATIONS
                // ══════════════════════════════════════════
                Notification n1 = new Notification();
                n1.setUserId(patient.getId()); n1.setType("APPOINTMENT"); n1.setTitle("Appointment Confirmed");
                n1.setMessage("Your appointment with Dr. Anita Desai on " + LocalDate.now().plusDays(3) + " has been confirmed.");
                n1.setLink("/portal/appointments"); n1.setIcon("✅"); n1.setRead(false); n1.setCreatedAt(LocalDateTime.now().minusHours(2));
                notifRepo.save(n1);

                Notification n2 = new Notification();
                n2.setUserId(patient.getId()); n2.setType("PRESCRIPTION"); n2.setTitle("New Prescription Added");
                n2.setMessage("Dr. Vikram Patel has prescribed medications for Migraine. View your prescription details.");
                n2.setLink("/portal/prescriptions"); n2.setIcon("💊"); n2.setRead(false); n2.setCreatedAt(LocalDateTime.now().minusDays(1));
                notifRepo.save(n2);

                Notification n3 = new Notification();
                n3.setUserId(patient.getId()); n3.setType("LAB_RESULT"); n3.setTitle("Lab Results Ready");
                n3.setMessage("Your Lipid Profile test results are ready. Status: Abnormal — please consult your cardiologist.");
                n3.setLink("/portal/records"); n3.setIcon("🔬"); n3.setRead(true); n3.setCreatedAt(LocalDateTime.now().minusDays(3));
                notifRepo.save(n3);

                Notification n4 = new Notification();
                n4.setUserId(doc1.getId()); n4.setType("APPOINTMENT"); n4.setTitle("New Appointment");
                n4.setMessage("Rahul Verma booked an appointment for " + LocalDate.now().plusDays(3) + " at 10:00");
                n4.setLink("/doctor/dashboard"); n4.setIcon("📅"); n4.setRead(false); n4.setCreatedAt(LocalDateTime.now().minusHours(2));
                notifRepo.save(n4);

                Notification n5 = new Notification();
                n5.setUserId(doc1.getId()); n5.setType("SYSTEM"); n5.setTitle("Welcome to WellOfast");
                n5.setMessage("Your doctor dashboard is now live. View your schedule and manage appointments.");
                n5.setLink("/doctor/dashboard"); n5.setIcon("🎉"); n5.setRead(true); n5.setCreatedAt(LocalDateTime.now().minusDays(7));
                notifRepo.save(n5);

                // ══════════════════════════════════════════
                //  SEED CHAT MESSAGES
                // ══════════════════════════════════════════
                String convId = patient.getId().compareTo(doc1.getId()) < 0
                        ? patient.getId() + "_" + doc1.getId() : doc1.getId() + "_" + patient.getId();

                ChatMessage c1 = new ChatMessage();
                c1.setSenderId(patient.getId()); c1.setSenderName("Rahul Verma"); c1.setSenderRole("PATIENT");
                c1.setReceiverId(doc1.getId()); c1.setReceiverName("Dr. Anita Desai");
                c1.setConversationId(convId); c1.setMessage("Hello Dr. Desai, I wanted to ask about my blood pressure medications. Is it okay to take Amlodipine with food?");
                c1.setRead(true); c1.setTimestamp(LocalDateTime.now().minusDays(2).withHour(10).withMinute(30));
                chatRepo.save(c1);

                ChatMessage c2 = new ChatMessage();
                c2.setSenderId(doc1.getId()); c2.setSenderName("Dr. Anita Desai"); c2.setSenderRole("DOCTOR");
                c2.setReceiverId(patient.getId()); c2.setReceiverName("Rahul Verma");
                c2.setConversationId(convId); c2.setMessage("Hello Rahul! Yes, Amlodipine can be taken with or without food. Take it in the morning at the same time daily for best results. Also, continue monitoring your BP at home.");
                c2.setRead(true); c2.setTimestamp(LocalDateTime.now().minusDays(2).withHour(11).withMinute(15));
                chatRepo.save(c2);

                ChatMessage c3 = new ChatMessage();
                c3.setSenderId(patient.getId()); c3.setSenderName("Rahul Verma"); c3.setSenderRole("PATIENT");
                c3.setReceiverId(doc1.getId()); c3.setReceiverName("Dr. Anita Desai");
                c3.setConversationId(convId); c3.setMessage("Thank you doctor! My BP readings this week have been around 130/85. Should I be concerned?");
                c3.setRead(true); c3.setTimestamp(LocalDateTime.now().minusDays(1).withHour(9).withMinute(45));
                chatRepo.save(c3);

                ChatMessage c4 = new ChatMessage();
                c4.setSenderId(doc1.getId()); c4.setSenderName("Dr. Anita Desai"); c4.setSenderRole("DOCTOR");
                c4.setReceiverId(patient.getId()); c4.setReceiverName("Rahul Verma");
                c4.setConversationId(convId); c4.setMessage("130/85 is slightly elevated but improving. Continue the medication and low-sodium diet. We'll review in your next appointment. Keep tracking daily! 📊");
                c4.setRead(false); c4.setTimestamp(LocalDateTime.now().minusHours(5));
                chatRepo.save(c4);

                System.out.println("╔══════════════════════════════════════════╗");
                // ── Sample Lab Tests ──
                if (labTestRepo.count() == 0) {
                    LabTest t1 = new LabTest(); t1.setName("Complete Blood Count (CBC)"); t1.setCategory("BLOOD"); t1.setPrice(350); t1.setSampleType("Blood"); t1.setTurnaroundTime("24 hrs"); t1.setDescription("Measures RBC, WBC, and platelets. Good for checking overall health."); t1.setCreatedAt(LocalDateTime.now()); labTestRepo.save(t1);
                    LabTest t2 = new LabTest(); t2.setName("Lipid Profile"); t2.setCategory("CARDIAC"); t2.setPrice(650); t2.setSampleType("Blood"); t2.setTurnaroundTime("24 hrs"); t2.setFasting(true); t2.setDescription("Checks cholesterol levels to assess heart disease risk."); t2.setCreatedAt(LocalDateTime.now()); labTestRepo.save(t2);
                    LabTest t3 = new LabTest(); t3.setName("Thyroid Profile (T3, T4, TSH)"); t3.setCategory("THYROID"); t3.setPrice(700); t3.setSampleType("Blood"); t3.setTurnaroundTime("24 hrs"); t3.setDescription("Measures thyroid hormones to check for hypo/hyperthyroidism."); t3.setCreatedAt(LocalDateTime.now()); labTestRepo.save(t3);
                    LabTest t4 = new LabTest(); t4.setName("Liver Function Test (LFT)"); t4.setCategory("LIVER"); t4.setPrice(800); t4.setSampleType("Blood"); t4.setTurnaroundTime("24 hrs"); t4.setFasting(true); t4.setDescription("Assesses the health of the liver by measuring proteins, enzymes, etc."); t4.setCreatedAt(LocalDateTime.now()); labTestRepo.save(t4);
                    LabTest t5 = new LabTest(); t5.setName("Routine Urine Analysis"); t5.setCategory("URINE"); t5.setPrice(200); t5.setSampleType("Urine"); t5.setTurnaroundTime("12 hrs"); t5.setCreatedAt(LocalDateTime.now()); labTestRepo.save(t5);
                }

                System.out.println("╔══════════════════════════════════════════╗");
                System.out.println("║     WellOfast — Demo Accounts Created    ║");
                System.out.println("║  Admin:    admin    / admin123           ║");
                System.out.println("║  Doctor:   doctor   / doctor123          ║");
                System.out.println("║  Doctor2:  doctor2  / doctor123          ║");
                System.out.println("║  Doctor3:  doctor3  / doctor123          ║");
                System.out.println("║  Employee: employee / emp123             ║");
                System.out.println("║  Patient:  patient  / patient123         ║");
                System.out.println("╚══════════════════════════════════════════╝");
            }
        };
    }
}
