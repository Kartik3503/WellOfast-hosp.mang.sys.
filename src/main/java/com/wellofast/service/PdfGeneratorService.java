package com.wellofast.service;

import com.wellofast.model.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {
    private static final DeviceRgb HDR = new DeviceRgb(0,51,102);
    private static final DeviceRgb ACC = new DeviceRgb(45,182,141);
    private static final DeviceRgb LG = new DeviceRgb(245,245,245);
    private static final DeviceRgb PURPLE = new DeviceRgb(99,102,241);

    // ══════════════════════════════════════════
    //  BIRTH CERTIFICATE PDF (existing)
    // ══════════════════════════════════════════
    public byte[] generate(BirthCertificate c) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(baos)), PageSize.A4);
        doc.setMargins(30,40,30,40);
        PdfFont b = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont r = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont i = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

        Table outer = new Table(1).useAllAvailableWidth().setBorder(new SolidBorder(HDR,3));
        Cell oc = new Cell().setPadding(15).setBorder(Border.NO_BORDER);
        Table inner = new Table(1).useAllAvailableWidth().setBorder(new SolidBorder(ACC,1));
        Cell ic = new Cell().setPadding(20).setBorder(Border.NO_BORDER);

        ic.add(p("GOVERNMENT OF INDIA",b,10,TextAlignment.CENTER,ACC));
        ic.add(p("OFFICE OF THE REGISTRAR OF BIRTHS & DEATHS",b,9,TextAlignment.CENTER,ACC));

        Table tt = new Table(1).useAllAvailableWidth();
        Cell tc = new Cell().setBackgroundColor(HDR).setPadding(12).setBorder(Border.NO_BORDER);
        tc.add(p("BIRTH CERTIFICATE",b,22,TextAlignment.CENTER,ColorConstants.WHITE));
        tc.add(new Paragraph("(Issued under Registration of Births and Deaths Act, 1969)")
                .setFont(i).setFontSize(7).setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(200,220,240)));
        tt.addCell(tc);
        ic.add(tt).add(new Paragraph("").setMarginBottom(8));

        ic.add(section("ISSUING AUTHORITY",b));
        ic.add(detailTable(b,r,new String[][]{
            {"Hospital Name",s(c.getHospitalName())},{"Reg No.",s(c.getHospitalRegistrationNumber())},
            {"Sanctioned By",s(c.getSanctionedByName())},{"Designation",s(c.getSanctionedByDesignation())}
        }));
        ic.add(section("CHILD DETAILS",b));
        ic.add(detailTable(b,r,new String[][]{
            {"Name",s(c.getChildName())},{"Gender",s(c.getGender())},
            {"Date of Birth",c.getDateOfBirth()!=null?c.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")):"N/A"},
            {"Time of Birth",c.getTimeOfBirth()!=null?c.getTimeOfBirth().format(DateTimeFormatter.ofPattern("hh:mm a")):"N/A"},
            {"Place of Birth",s(c.getPlaceOfBirth())},{"Weight",s(c.getBirthWeight())},
            {"Blood Group",s(c.getBloodGroup())}
        }));
        ic.add(section("MOTHER'S DETAILS",b));
        ic.add(detailTable(b,r,new String[][]{
            {"Name",s(c.getMotherName())},{"Age",s(c.getMotherAge())},
            {"Nationality",s(c.getMotherNationality())},{"Occupation",s(c.getMotherOccupation())}
        }));
        ic.add(section("FATHER'S DETAILS",b));
        ic.add(detailTable(b,r,new String[][]{
            {"Name",s(c.getFatherName())},{"Age",s(c.getFatherAge())},
            {"Nationality",s(c.getFatherNationality())},{"Occupation",s(c.getFatherOccupation())}
        }));

        ic.add(new Paragraph("").setMarginBottom(25));
        Table sig = new Table(UnitValue.createPercentArray(new float[]{50,50})).useAllAvailableWidth();
        sig.addCell(sigCell("Signature of Registrar","",b,r));
        sig.addCell(sigCell(s(c.getSanctionedByName()),s(c.getSanctionedByDesignation()),b,i));
        ic.add(sig);

        ic.add(new Paragraph("Computer-generated certificate. Tampering is punishable under law.")
                .setFont(i).setFontSize(7).setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(128,128,128)).setMarginTop(10));

        inner.addCell(ic); oc.add(inner); outer.addCell(oc); doc.add(outer); doc.close();
        return baos.toByteArray();
    }

    // ══════════════════════════════════════════
    //  MEDICAL RECORD PDF
    // ══════════════════════════════════════════
    public byte[] generateMedicalRecordPdf(MedicalRecord rec, User patient) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(baos)), PageSize.A4);
        doc.setMargins(30,40,30,40);
        PdfFont bf = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont rf = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont it = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

        // Header
        Table header = new Table(1).useAllAvailableWidth();
        Cell hc = new Cell().setBackgroundColor(PURPLE).setPadding(15).setBorder(Border.NO_BORDER);
        hc.add(p("WellOfast Hospital",bf,18,TextAlignment.CENTER,ColorConstants.WHITE));
        hc.add(p("MEDICAL RECORD REPORT",bf,14,TextAlignment.CENTER,new DeviceRgb(200,210,255)));
        header.addCell(hc);
        doc.add(header);
        doc.add(new Paragraph("").setMarginBottom(10));

        // Patient Info
        doc.add(section("PATIENT INFORMATION",bf));
        doc.add(detailTable(bf,rf,new String[][]{
            {"Patient Name", s(patient.getFullName())},
            {"Date of Birth", patient.getDateOfBirth() != null ? patient.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "N/A"},
            {"Gender", s(patient.getGender())},
            {"Phone", s(patient.getPhone())}
        }));

        // Record Details
        doc.add(section("RECORD DETAILS",bf));
        doc.add(detailTable(bf,rf,new String[][]{
            {"Record Type", s(rec.getType())},
            {"Date", rec.getDate() != null ? rec.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "N/A"},
            {"Doctor", s(rec.getDoctorName())},
            {"Department", s(rec.getDepartment())},
            {"Diagnosis", s(rec.getDiagnosis())},
            {"Treatment", s(rec.getTreatment())}
        }));

        // Vitals
        if (rec.getBloodPressure() != null || rec.getHeartRate() != null) {
            doc.add(section("VITALS",bf));
            java.util.List<String[]> vitals = new java.util.ArrayList<>();
            if (rec.getBloodPressure() != null) vitals.add(new String[]{"Blood Pressure", rec.getBloodPressure()});
            if (rec.getHeartRate() != null) vitals.add(new String[]{"Heart Rate", rec.getHeartRate() + " bpm"});
            if (rec.getTemperature() != null) vitals.add(new String[]{"Temperature", rec.getTemperature() + "°F"});
            if (rec.getWeight() != null) vitals.add(new String[]{"Weight", rec.getWeight() + " kg"});
            if (rec.getOxygenSaturation() != null) vitals.add(new String[]{"SpO2", rec.getOxygenSaturation() + "%"});
            doc.add(detailTable(bf,rf,vitals.toArray(new String[0][])));
        }

        // Procedures
        if (rec.getProcedures() != null && !rec.getProcedures().isEmpty()) {
            doc.add(section("PROCEDURES PERFORMED",bf));
            for (int idx = 0; idx < rec.getProcedures().size(); idx++) {
                doc.add(new Paragraph((idx+1) + ". " + rec.getProcedures().get(idx)).setFont(rf).setFontSize(10).setMarginLeft(10));
            }
        }

        // Injections
        if (rec.getInjections() != null && !rec.getInjections().isEmpty()) {
            doc.add(section("INJECTIONS ADMINISTERED",bf));
            for (int idx = 0; idx < rec.getInjections().size(); idx++) {
                doc.add(new Paragraph((idx+1) + ". " + rec.getInjections().get(idx)).setFont(rf).setFontSize(10).setMarginLeft(10));
            }
        }

        // Lab Results
        if (rec.getLabTestName() != null) {
            doc.add(section("LAB RESULTS",bf));
            doc.add(detailTable(bf,rf,new String[][]{
                {"Test Name", s(rec.getLabTestName())},
                {"Result", s(rec.getLabResult())},
                {"Normal Range", s(rec.getLabNormalRange())},
                {"Status", s(rec.getLabStatus())}
            }));
        }

        // Notes
        if (rec.getNotes() != null) {
            doc.add(section("DOCTOR'S NOTES",bf));
            doc.add(new Paragraph(rec.getNotes()).setFont(rf).setFontSize(10).setMarginLeft(5));
        }

        // Footer
        doc.add(new Paragraph("").setMarginBottom(30));
        Table sig = new Table(UnitValue.createPercentArray(new float[]{50,50})).useAllAvailableWidth();
        sig.addCell(sigCell("Patient Signature","",bf,rf));
        sig.addCell(sigCell(s(rec.getDoctorName()),s(rec.getDepartment()),bf,it));
        doc.add(sig);

        doc.add(new Paragraph("This is a computer-generated medical report from WellOfast Hospital.")
                .setFont(it).setFontSize(7).setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(128,128,128)).setMarginTop(15));

        doc.close();
        return baos.toByteArray();
    }

    // ══════════════════════════════════════════
    //  PRESCRIPTION PDF
    // ══════════════════════════════════════════
    public byte[] generatePrescriptionPdf(Prescription presc, User patient) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(baos)), PageSize.A4);
        doc.setMargins(30,40,30,40);
        PdfFont bf = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont rf = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont it = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

        // Header
        Table header = new Table(1).useAllAvailableWidth();
        Cell hc = new Cell().setBackgroundColor(new DeviceRgb(139,92,246)).setPadding(15).setBorder(Border.NO_BORDER);
        hc.add(p("WellOfast Hospital",bf,18,TextAlignment.CENTER,ColorConstants.WHITE));
        hc.add(p("PRESCRIPTION",bf,14,TextAlignment.CENTER,new DeviceRgb(220,210,255)));
        header.addCell(hc);
        doc.add(header);
        doc.add(new Paragraph("").setMarginBottom(10));

        // Doctor & Patient info side by side
        doc.add(section("PRESCRIPTION DETAILS",bf));
        doc.add(detailTable(bf,rf,new String[][]{
            {"Patient Name", s(patient.getFullName())},
            {"Date", presc.getDate() != null ? presc.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "N/A"},
            {"Prescribed By", "Dr. " + s(presc.getDoctorName())},
            {"Department", s(presc.getDepartment())},
            {"Diagnosis", s(presc.getDiagnosis())}
        }));

        // Medicines Table
        doc.add(section("PRESCRIBED MEDICINES",bf));
        if (presc.getMedicines() != null && !presc.getMedicines().isEmpty()) {
            Table medTable = new Table(UnitValue.createPercentArray(new float[]{5,25,12,13,15,15,15})).useAllAvailableWidth();
            // Header row
            String[] headers = {"#","Medicine","Type","Dosage","Frequency","Duration","Instructions"};
            for (String h : headers) {
                medTable.addHeaderCell(new Cell().setBackgroundColor(PURPLE).setPadding(6).setBorder(new SolidBorder(new DeviceRgb(200,200,200),0.5f))
                        .add(new Paragraph(h).setFont(bf).setFontSize(8).setFontColor(ColorConstants.WHITE)));
            }
            int num = 1;
            boolean alt = false;
            for (Medicine med : presc.getMedicines()) {
                DeviceRgb bg = alt ? LG : new DeviceRgb(255,255,255);
                medTable.addCell(mCell(String.valueOf(num++),rf,bg));
                medTable.addCell(mCell(s(med.getName()),bf,bg));
                medTable.addCell(mCell(s(med.getType()),rf,bg));
                medTable.addCell(mCell(s(med.getDosage()),rf,bg));
                medTable.addCell(mCell(s(med.getFrequency()),rf,bg));
                medTable.addCell(mCell(s(med.getDuration()),rf,bg));
                medTable.addCell(mCell(s(med.getInstructions()),rf,bg));
                alt = !alt;
            }
            doc.add(medTable);
        }

        // General Instructions
        if (presc.getInstructions() != null) {
            doc.add(section("GENERAL INSTRUCTIONS",bf));
            doc.add(new Paragraph(presc.getInstructions()).setFont(rf).setFontSize(10).setMarginLeft(5)
                    .setBackgroundColor(new DeviceRgb(254,243,199)).setPadding(8).setBorderRadius(new BorderRadius(4)));
        }

        // Signature
        doc.add(new Paragraph("").setMarginBottom(30));
        Table sig = new Table(UnitValue.createPercentArray(new float[]{50,50})).useAllAvailableWidth();
        sig.addCell(sigCell("Patient: " + s(patient.getFullName()),"",bf,rf));
        sig.addCell(sigCell("Dr. " + s(presc.getDoctorName()),s(presc.getDepartment()),bf,it));
        doc.add(sig);

        doc.add(new Paragraph("This is a computer-generated prescription from WellOfast Hospital. Not valid without doctor's stamp.")
                .setFont(it).setFontSize(7).setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(128,128,128)).setMarginTop(15));

        doc.close();
        return baos.toByteArray();
    }

    // ══════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════
    private Cell mCell(String text, PdfFont font, DeviceRgb bg) {
        return new Cell().setBorder(new SolidBorder(new DeviceRgb(220,220,220),0.5f))
                .setBackgroundColor(bg).setPadding(5)
                .add(new Paragraph(text).setFont(font).setFontSize(8));
    }
    private Paragraph p(String t,PdfFont f,float sz,TextAlignment a,com.itextpdf.kernel.colors.Color col){
        return new Paragraph(t).setFont(f).setFontSize(sz).setTextAlignment(a).setFontColor(col).setMarginBottom(2);
    }
    private Paragraph section(String t,PdfFont f){
        return new Paragraph(t).setFont(f).setFontSize(11).setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(ACC).setPadding(5).setMarginBottom(5).setMarginTop(8);
    }
    private Table detailTable(PdfFont bf,PdfFont rf,String[][] rows){
        Table t = new Table(UnitValue.createPercentArray(new float[]{35,65})).useAllAvailableWidth().setMarginBottom(8);
        boolean alt=false;
        for(String[] row:rows){
            DeviceRgb bg=alt?LG:new DeviceRgb(255,255,255);
            t.addCell(new Cell().setBorder(new SolidBorder(new DeviceRgb(220,220,220),0.5f))
                    .setBackgroundColor(bg).setPadding(5).add(new Paragraph(row[0]).setFont(bf).setFontSize(9).setFontColor(HDR)));
            t.addCell(new Cell().setBorder(new SolidBorder(new DeviceRgb(220,220,220),0.5f))
                    .setBackgroundColor(bg).setPadding(5).add(new Paragraph(row[1]).setFont(rf).setFontSize(9)));
            alt=!alt;
        }
        return t;
    }
    private Cell sigCell(String name,String desg,PdfFont bf,PdfFont sf){
        Cell c=new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        c.add(new Paragraph("_________________________").setFont(sf).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        c.add(new Paragraph(name).setFont(bf).setFontSize(8).setTextAlignment(TextAlignment.CENTER));
        if(!desg.isEmpty()) c.add(new Paragraph(desg).setFont(sf).setFontSize(7).setTextAlignment(TextAlignment.CENTER));
        return c;
    }
    private String s(String v){return v!=null&&!v.isEmpty()?v:"N/A";}
}
