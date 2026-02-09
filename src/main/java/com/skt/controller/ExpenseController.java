package com.skt.controller;

import com.skt.bean.Expense;
import com.skt.service.ExpenseExcelService;
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
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseExcelService expenseExcelService;

    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExpenseExcel(
            @RequestParam("file") MultipartFile file) throws Exception {

        expenseExcelService.saveExcelData(file);
        return ResponseEntity.ok("Expense Excel uploaded successfully");
    }

    @GetMapping("/download-template")
    public ResponseEntity<InputStreamResource> downloadTemplate() throws Exception {

        InputStreamResource file =
                new InputStreamResource(expenseExcelService.generateTemplate());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Expense_Template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return ResponseEntity.ok(
                expenseExcelService.getByDateRange(fromDate, toDate)
        );
    }

    // Excel download API
    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) throws IOException {

        byte[] excel = expenseExcelService.generateExcel(fromDate, toDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("Expenses.xlsx").build()
        );

        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }
}
