package com.skt.service;

import com.skt.bean.Employee;
import com.skt.bean.EmployeeAttendance;
import com.skt.repository.EmployeeAttendanceRepository;
import com.skt.repository.EmployeeRepository;
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
public class AttendanceExcelService {

    @Autowired
    private EmployeeAttendanceRepository repository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public void saveExcelData(MultipartFile file) throws Exception {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {   // skip header
            Row row = sheet.getRow(i);

            EmployeeAttendance attendance = new EmployeeAttendance();
            int empId = (int) row.getCell(0).getNumericCellValue();
            Employee employee = employeeRepository
                    .findById((long) empId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            attendance.setEmployeeId(employee);
            attendance.setAttendanceDate(row.getCell(1).getLocalDateTimeCellValue().toLocalDate());
            attendance.setCheckIn(row.getCell(2).getLocalDateTimeCellValue().toLocalTime());
            attendance.setCheckOut(row.getCell(3).getLocalDateTimeCellValue().toLocalTime());
            attendance.setStatus(row.getCell(4).getStringCellValue());
            attendance.setRemarks(row.getCell(5).getStringCellValue());

            repository.save(attendance);
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
        Sheet sheet = workbook.createSheet("Attendance Template");

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
                "Employee_Id",
                "Attendance_Date",
                "Check_In",
                "Check_Out",
                "Status",
                "Remarks"
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

    public List<EmployeeAttendance> getAttendanceByDateRange(LocalDate fromDate, LocalDate toDate) {
        DateRangeValidator.validate(fromDate, toDate);
        return repository.findByAttendanceDateBetween(fromDate, toDate);
    }

    // Generate Excel
    public byte[] generateExcel(LocalDate fromDate, LocalDate toDate) throws IOException {

        List<EmployeeAttendance> list =
                getAttendanceByDateRange(fromDate, toDate);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance_Report");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Employee ID");
        header.createCell(1).setCellValue("Date");
        header.createCell(2).setCellValue("Check In");
        header.createCell(3).setCellValue("Check Out");
        header.createCell(4).setCellValue("Status");
        header.createCell(5).setCellValue("Remarks");

        int rowIndex = 1;

        for (EmployeeAttendance a : list) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(a.getEmployeeId().getEmpId());
            row.createCell(1).setCellValue(a.getAttendanceDate().toString());
            row.createCell(2).setCellValue(
                    a.getCheckIn() != null ? a.getCheckIn().toString() : ""
            );
            row.createCell(3).setCellValue(
                    a.getCheckOut() != null ? a.getCheckOut().toString() : ""
            );
            row.createCell(4).setCellValue(a.getStatus());
            row.createCell(5).setCellValue(a.getRemarks());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}

