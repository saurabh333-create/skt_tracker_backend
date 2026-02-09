package com.skt.service;

import com.skt.bean.Expense;
import com.skt.bean.Purchase;
import com.skt.repository.ExpenseRepository;
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
public class ExpenseExcelService {

    @Autowired
    private ExpenseRepository repository;

    public void saveExcelData(MultipartFile file) throws Exception {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {   // skip header
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Expense expense = new Expense();
            expense.setExpenseDate(
                    row.getCell(0).getLocalDateTimeCellValue().toLocalDate()
            );
            expense.setExpenseType(row.getCell(1).getStringCellValue());
            expense.setAmount(
                    BigDecimal.valueOf(row.getCell(2).getNumericCellValue())
            );
            expense.setRemark(row.getCell(3).getStringCellValue());

            repository.save(expense);
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
        Sheet sheet = workbook.createSheet("Expense Template");

        // Header style
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
                "Expense_Date",
                "Expense_Type",
                "Amount",
                "Remark"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // ✅ Dropdown for Expense_Type column
        DataValidationHelper helper = sheet.getDataValidationHelper();

        String[] expenseTypes = {"Mess", "Machinery", "Labour"};

        DataValidationConstraint constraint =
                helper.createExplicitListConstraint(expenseTypes);

        // Apply dropdown from row 1 to 500 in column 1
        CellRangeAddressList addressList =
                new CellRangeAddressList(1, 500, 1, 1);

        DataValidation validation =
                helper.createValidation(constraint, addressList);

        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public List<Expense> getByDateRange(LocalDate from, LocalDate to) {
        DateRangeValidator.validate(from, to);
        return repository.findByExpenseDateBetween(from, to);
    }

    // Generate Excel
    public byte[] generateExcel(LocalDate fromDate, LocalDate toDate) throws IOException {

        List<Expense> expenses = getExpensesByDateRange(fromDate, toDate);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Expenses");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Type");
        header.createCell(2).setCellValue("Amount");
        header.createCell(3).setCellValue("Remark");
        header.createCell(4).setCellValue("Created At");

        int rowIndex = 1;

        for (Expense e : expenses) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(e.getExpenseDate().toString());
            row.createCell(1).setCellValue(e.getExpenseType());
            row.createCell(2).setCellValue(e.getAmount().doubleValue());
            row.createCell(3).setCellValue(e.getRemark());
            row.createCell(4).setCellValue(e.getCreatedAt().toString());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    private List<Expense> getExpensesByDateRange(LocalDate fromDate, LocalDate toDate) {
            DateRangeValidator.validate(fromDate, toDate);
            return repository.findByExpenseDateBetween(fromDate, toDate);
        }
}
