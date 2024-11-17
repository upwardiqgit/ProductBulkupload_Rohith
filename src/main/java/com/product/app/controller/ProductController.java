package com.product.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.product.app.entity.Product;
import com.product.app.service.ProductService;
import com.product.app.util.ExcelExporter;

import jakarta.servlet.http.HttpServletResponse;

@SessionScope
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;
    private List<Product> cumulativeResults = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // Upload Endpoint
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("File upload initiated: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty. Please upload a valid file.");
        }
        try {
            List<Product> products = productService.processExcelFile(file);
            logger.info("File processed successfully, products count: {}", products.size());
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid file format: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid file format.");
        } catch (Exception e) {
            logger.error("Error processing file: {}", e.getMessage());
            return ResponseEntity.status(500).body("An error occurred while processing the file.");
        }
    }

    // Save Endpoint
    @PostMapping("/save")
    public ResponseEntity<?> saveProducts(@RequestBody List<Product> products) {
        if (products == null || products.isEmpty()) {
            return ResponseEntity.badRequest().body("Product list is empty.");
        }

        productService.saveAll(products);
        return ResponseEntity.ok("Products saved successfully!");
    }

    // Get Distinct Product Types
    @GetMapping("/product-types")
    public ResponseEntity<List<String>> getProductTypes() {
        List<String> productTypes = productService.findAllProductTypes();
        return ResponseEntity.ok(productTypes);
    }

    // Search Products Endpoint
 // Appended Search Products Endpoint
    @GetMapping("/search")
    public ResponseEntity<?> searchAndAppendProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productType) {
        
        // Perform the search
        List<Product> products = productService.searchProducts(productName, productType);
        
        // Append to cumulative results
        cumulativeResults.addAll(products);

        return ResponseEntity.ok(cumulativeResults);
    }
   
    @GetMapping("/export")
    public void exportCumulativeSearchResults(HttpServletResponse response) throws IOException {
        try {
            if (cumulativeResults.isEmpty()) {
                response.setContentType("text/plain");
                response.getWriter().write("No data found to export.");
                return;
            }

            // Set response headers for Excel download
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=searched_products.xlsx");

            // Export the cumulative results to Excel
            ExcelExporter excelExporter = new ExcelExporter(cumulativeResults);
            excelExporter.export(response);

        } catch (Exception e) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error occurred while exporting: " + e.getMessage());
        }
    }
}
