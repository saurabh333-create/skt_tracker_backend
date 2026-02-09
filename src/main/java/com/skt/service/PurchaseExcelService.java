package com.skt.service;

import com.skt.bean.Purchase;
import com.skt.repository.PurchaseRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseExcelService {

    @Autowired
    private PurchaseRepository repository;

    public void saveExcelData(MultipartFile file) throws Exception {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {   // skip header
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Purchase purchase = new Purchase();
            purchase.setPurchaseDate(
                    row.getCell(0).getLocalDateTimeCellValue().toLocalDate()
            );
            purchase.setPartyName(row.getCell(1).getStringCellValue());
            purchase.setMaterialName(row.getCell(2).getStringCellValue());
            purchase.setQuantity(
                    BigDecimal.valueOf(row.getCell(3).getNumericCellValue())
            );
            purchase.setAmount(
                    BigDecimal.valueOf(row.getCell(4).getNumericCellValue())
            );
            purchase.setReceiverName(row.getCell(5).getStringCellValue());
            purchase.setDescription(row.getCell(6).getStringCellValue());

            repository.save(purchase);
        }
        workbook.close();
    }

    private String getString(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private BigDecimal getDecimal(Cell cell) {
        if (cell == null) return null;

        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    public ByteArrayInputStream generateTemplate() throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Purchase Template");

        // ✅ Header style
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // Header row
        Row header = sheet.createRow(0);

        String[] headers = {
                "Purchase_Date",
                "Party_Name",
                "Material_Name",
                "Quantity",
                "Amount",
                "Receiver_Name",
                "Description"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public List<Purchase> getPurchasesByDateRange(LocalDate fromDate, LocalDate toDate) {
        DateRangeValidator.validate(fromDate, toDate);
        return repository.findByPurchaseDateBetween(fromDate, toDate);
    }

    // Generate Excel
    public byte[] generateExcel(LocalDate fromDate, LocalDate toDate) throws IOException {

        List<Purchase> purchases = getPurchasesByDateRange(fromDate, toDate);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Purchase_Report");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Party");
        header.createCell(2).setCellValue("Material");
        header.createCell(3).setCellValue("Quantity");
        header.createCell(4).setCellValue("Amount");
        header.createCell(5).setCellValue("Receiver");
        header.createCell(6).setCellValue("Description");
        header.createCell(7).setCellValue("Created At");

        int rowIndex = 1;

        for (Purchase p : purchases) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(p.getPurchaseDate().toString());
            row.createCell(1).setCellValue(p.getPartyName());
            row.createCell(2).setCellValue(p.getMaterialName());
            row.createCell(3).setCellValue(p.getQuantity().doubleValue());
            row.createCell(4).setCellValue(p.getAmount().doubleValue());
            row.createCell(5).setCellValue(p.getReceiverName());
            row.createCell(6).setCellValue(p.getDescription());
            row.createCell(7).setCellValue(p.getCreatedAt().toString());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}
