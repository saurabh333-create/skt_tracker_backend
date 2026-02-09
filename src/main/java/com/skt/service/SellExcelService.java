package com.skt.service;

import com.skt.bean.Sell;
import com.skt.repository.SellRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
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
public class SellExcelService {

    @Autowired
    private SellRepository repository;

    public void saveExcelData(MultipartFile file) throws Exception {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Sell sell = new Sell();

            sell.setSellDate(
                    row.getCell(0).getLocalDateTimeCellValue().toLocalDate()
            );

            sell.setBillType(getString(row.getCell(1)));
            sell.setBillNo(getString(row.getCell(2)));
            sell.setQuantity(getDecimal(row.getCell(3)));
            sell.setAmount(getDecimal(row.getCell(4)));
            sell.setDescription(getString(row.getCell(5)));

            repository.save(sell);
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
        Sheet sheet = workbook.createSheet("Sell Template");

        // ✅ Header style (yellow + bold + center)
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
                "Sell_Date",
                "Bill_Type",
                "Bill_No",
                "Quantity",
                "Amount",
                "Description"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // ✅ Dropdown list for Bill_Type column
        DataValidationHelper helper = sheet.getDataValidationHelper();

        String[] billTypes = {"Rental", "Construction"};

        DataValidationConstraint constraint =
                helper.createExplicitListConstraint(billTypes);

        CellRangeAddressList addressList =
                new CellRangeAddressList(1, 500, 1, 1);

        DataValidation validation =
                helper.createValidation(constraint, addressList);

        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);

        // Convert workbook to stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }


    public List<Sell> getSalesByDateRange(LocalDate fromDate, LocalDate toDate) {
        DateRangeValidator.validate(fromDate, toDate);
        return repository.findBySellDateBetween(fromDate, toDate);
    }

    // Generate Excel
    public byte[] generateExcel(LocalDate fromDate, LocalDate toDate) throws IOException {

        List<Sell> sales = getSalesByDateRange(fromDate, toDate);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sell_Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Bill No");
        header.createCell(1).setCellValue("Bill Type");
        header.createCell(2).setCellValue("Description");
        header.createCell(3).setCellValue("Quantity");
        header.createCell(4).setCellValue("Amount");
        header.createCell(5).setCellValue("Sell Date");
        header.createCell(6).setCellValue("Created At");

        int rowIndex = 1;

        for (Sell s : sales) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(s.getBillNo());
            row.createCell(1).setCellValue(s.getBillType());
            row.createCell(2).setCellValue(s.getDescription());
            row.createCell(3).setCellValue(s.getQuantity().doubleValue());
            row.createCell(4).setCellValue(s.getAmount().doubleValue());
            row.createCell(5).setCellValue(s.getSellDate().toString());
            row.createCell(6).setCellValue(s.getCreatedAt().toString());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}
