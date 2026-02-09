package com.skt.controller;

import com.skt.bean.Purchase;
import com.skt.service.PurchaseExcelService;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseExcelService purchaseExcelService;

    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadPurchaseExcel(
            @RequestParam("file") MultipartFile file) throws Exception {

        purchaseExcelService.saveExcelData(file);
        return ResponseEntity.ok("Purchase Excel uploaded successfully");
    }

    @GetMapping("/download-template")
    public ResponseEntity<InputStreamResource> downloadTemplate() throws Exception {

        InputStreamResource file =
                new InputStreamResource(purchaseExcelService.generateTemplate());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Purchase_Template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping
    public ResponseEntity<List<Purchase>> getPurchases(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(
                purchaseExcelService.getPurchasesByDateRange(fromDate, toDate)
        );
    }

    // Excel download API
    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) throws IOException {

        byte[] excel = purchaseExcelService.generateExcel(fromDate, toDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("Purchase.xlsx").build()
        );

        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }
}
