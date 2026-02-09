package com.skt.controller;

import com.skt.bean.Sell;
import com.skt.service.SellExcelService;
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
@RequestMapping("/api/sell")
public class SellController {

    @Autowired
    private SellExcelService sellExcelService;

    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadSellExcel(
            @RequestParam("file") MultipartFile file) throws Exception {

        sellExcelService.saveExcelData(file);
        return ResponseEntity.ok("Sell Excel uploaded successfully");
    }

    @GetMapping("/download-template")
    public ResponseEntity<InputStreamResource> downloadTemplate() throws Exception {

        InputStreamResource file =
                new InputStreamResource(sellExcelService.generateTemplate());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Sell_Template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) throws IOException {

        byte[] excel = sellExcelService.generateExcel(fromDate, toDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("Sell_Report.xlsx").build()
        );

        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }
}
