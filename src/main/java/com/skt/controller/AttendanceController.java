package com.skt.controller;

import com.skt.bean.EmployeeAttendance;
import com.skt.service.AttendanceExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceExcelService excelService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(
            @RequestParam("file") MultipartFile file) {

        try {
            excelService.saveExcelData(file);
            return ResponseEntity.ok("Excel uploaded and data saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Failed to upload Excel: " + e.getMessage());
        }
    }

    @GetMapping("/download-template")
    public ResponseEntity<InputStreamResource> downloadTemplate() throws Exception {

        InputStreamResource file =
                new InputStreamResource(excelService.generateTemplate());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Attendance_Template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeAttendance>> getAttendance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(
                excelService.getAttendanceByDateRange(fromDate, toDate)
        );
    }

    // Excel download
    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) throws IOException {

        byte[] excel =
                excelService.generateExcel(fromDate, toDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("Attendance_Report.xlsx")
                        .build()
        );

        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }
}

