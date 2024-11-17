package com.product.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.product.app.entity.Product;
import com.product.app.repositry.ProductRepository;

import org.apache.poi.ss.usermodel.Sheet;

@Service
public class ProductService {
    
	@Autowired
    private ProductRepository productRepository;

    // Method to process Excel file and convert to Product list
    public List<Product> processExcelFile(MultipartFile file) throws IOException {
        List<Product> products = new ArrayList<>();

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid file type. Please upload an Excel file.");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet.getRow(0).getPhysicalNumberOfCells() < 4) {
                throw new IllegalArgumentException("Invalid Excel file format");
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                try {
                    Product product = new Product();

                    // Product ID - Expecting numeric data
                    Cell idCell = row.getCell(0);
                    if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                        product.setProductId((long) Math.round(idCell.getNumericCellValue()));
                    }

                    // Product Name
                    Cell nameCell = row.getCell(1);
                    product.setProductName(nameCell != null ? nameCell.getStringCellValue() : "");

                    // Product Type
                    Cell typeCell = row.getCell(2);
                    product.setProductType(typeCell != null ? typeCell.getStringCellValue() : "");

                    // Product Price - Expecting numeric data
                    Cell priceCell = row.getCell(3);
                    if (priceCell != null && priceCell.getCellType() == CellType.NUMERIC) {
                        product.setPrice(priceCell.getNumericCellValue());
                    }

                    products.add(product);
                } catch (Exception e) {
                    System.out.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
                }
            }
        }

        return products;
    }

    // Method to save a list of products to the database
    public List<Product> saveAll(List<Product> products) {
        return productRepository.saveAll(products);
    }

    // Method to search products by name and type with filtering
    public List<Product> searchProducts(String productName, String productType) {
        return productRepository.findByProductNameContainingAndProductTypeContaining(
            productName != null ? productName : "",
            productType != null ? productType : ""
        );
    }

    // Method to return all products (used for exporting unfiltered data)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    // Method to get distinct product types (used for displaying the available types)
    public List<String> findAllProductTypes() {
        return productRepository.findDistinctProductTypes();
    }

    // Method to return filtered products based on search criteria (used for export as well)
    public List<Product> getFilteredProducts(String productName, String productType) {
        // Apply filters for both productName and productType to export only filtered products
        if (productName != null && productType != null) {
            return productRepository.findByProductNameContainingAndProductTypeContaining(productName, productType);
        }
        // If only productName is provided, filter by productName
        else if (productName != null) {
            return productRepository.findByProductName(productName);
        }
        // If only productType is provided, filter by productType
        else if (productType != null) {
            return productRepository.findByProductType(productType);
        }
        // If no criteria are provided, return all products (for general export case)
        else {
            return productRepository.findAll();
        }
    }

    // Method to export filtered products to Excel
    public Workbook exportFilteredProductsToExcel(String productName, String productType) throws IOException {
        List<Product> filteredProducts = getFilteredProducts(productName, productType);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Product ID");
        headerRow.createCell(1).setCellValue("Product Name");
        headerRow.createCell(2).setCellValue("Product Type");
        headerRow.createCell(3).setCellValue("Price");

        // Fill data rows
        int rowNum = 1;
        for (Product product : filteredProducts) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(product.getProductId());
            row.createCell(1).setCellValue(product.getProductName());
            row.createCell(2).setCellValue(product.getProductType());
            row.createCell(3).setCellValue(product.getPrice());
        }

        return workbook;
    }
}
