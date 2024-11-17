package com.product.app.util;

import com.product.app.entity.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    private List<Product> products;
    
    public ExcelExporter(List<Product> products) {
        this.products = products;
    }
    
    public void export(HttpServletResponse response) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");
            
            // Create font for headers
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            
            // Create cell style for headers
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Create cell style for numeric values
            CellStyle numericStyle = workbook.createCellStyle();
            numericStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Product ID", "Product Name", "Product Type", "Price"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(product.getProductId());
                row.createCell(1).setCellValue(product.getProductName());
                row.createCell(2).setCellValue(product.getProductType());
                
                Cell priceCell = row.createCell(3);
                priceCell.setCellValue(product.getPrice());
                priceCell.setCellStyle(numericStyle);
            }
            
            // Autosize columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to response
            workbook.write(response.getOutputStream());
        }
    }
}