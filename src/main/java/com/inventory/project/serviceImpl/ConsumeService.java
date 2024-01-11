package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.ConsumedItem;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;
//import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
@Service
public class ConsumeService {
    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ConsumedItemRepo consumedItemRepo;


    public List<ConsumedItem> getCiplByItemAndLocation(String item, String locationName) {
        return consumedItemRepo.findByItemAndLocationName(item, locationName);
    }

    public List<ConsumedItem> getCiplByItem(String item) {
        return consumedItemRepo.findByItem(item);
    }

    public List<ConsumedItem> getCiplByLocation(String locationName) {
        return consumedItemRepo.findByLocationName(locationName);
    }

    public List<ConsumedItem> getCiplByTransferDate(LocalDate transferDate) {
        return consumedItemRepo.findByTransferDate(transferDate);
    }

    public List<ConsumedItem> getCiplByLocationAndTransferDate(String locationName, LocalDate transferDate) {
        return consumedItemRepo.findByLocationNameAndTransferDate(locationName,transferDate);

    }
    public List<ConsumedItem> getCiplByItemAndLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
        if (transferDate == null || item == null || item.isEmpty() || locationName == null || locationName.isEmpty()) {
            return Collections.emptyList(); // If any required parameter is null or empty, return an empty list
        }

        List<ConsumedItem> ciplList = consumedItemRepo.findByItemAndLocationNameAndTransferDate(item, locationName, transferDate);

        if (ciplList.isEmpty()) {
            return Collections.emptyList(); // No matching records found for the provided item, location, and date
        }

        return ciplList; // Return the matching records
    }


    public List<ConsumedItem> getAll() {
        return consumedItemRepo.findAll();
    }

//    public byte[] generateExcelFile(List<ConsumedItem> consumedItems) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("ConsumedItems");
//
//        // Create header row
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("Location");
//        headerRow.createCell(1).setCellValue("Transfer Date");
//        headerRow.createCell(2).setCellValue("Item");
//        headerRow.createCell(3).setCellValue("SubLocation");
//        headerRow.createCell(4).setCellValue("Quantity");
//        // Add more headers as needed
//
//        // Populate data rows
//        int rowNum = 1;
//        for (ConsumedItem consumedItem : consumedItems) {
//            Row row = sheet.createRow(rowNum++);
//            row.createCell(0).setCellValue(consumedItem.getLocationName());
//            row.createCell(1).setCellValue(consumedItem.getTransferDate().toString());
//            row.createCell(2).setCellValue(String.join(",", consumedItem.getItem()));
//            row.createCell(3).setCellValue(String.join(",", consumedItem.getSubLocations()));
//            row.createCell(4).setCellValue(String.join(",", consumedItem.getQuantity()));
//            // Add more data as needed
//        }
//
//        // Write the workbook content to a ByteArrayOutputStream
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        workbook.write(outputStream);
//        workbook.close();
//
//        return outputStream.toByteArray();
//    }
//
//    public byte[] generatePdfFile(List<ConsumedItem> consumedItems) throws IOException {
//        PDDocument document = new PDDocument();
//        PDPage page = new PDPage();
//        document.addPage(page);
//
//        PDPageContentStream contentStream = new PDPageContentStream(document, page);
//
//        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//        contentStream.newLineAtOffset(20, 700);
//
//        // Write headers
//        contentStream.showText("Location");
//        contentStream.newLineAtOffset(100, 0);
//        contentStream.showText("Transfer Date");
//        contentStream.newLineAtOffset(100, 0);
//        contentStream.showText("Item");
//        contentStream.newLineAtOffset(100, 0);
//        contentStream.showText("SubLocation");
//        contentStream.newLineAtOffset(100, 0);
//        contentStream.showText("Quantity");
//        // Add more headers as needed
//
//        contentStream.newLineAtOffset(-400, -15);
//
//        // Write data
//        for (ConsumedItem consumedItem : consumedItems) {
//            contentStream.showText(consumedItem.getLocationName());
//            contentStream.newLineAtOffset(100, 0);
//            contentStream.showText(consumedItem.getTransferDate().toString());
//            contentStream.newLineAtOffset(100, 0);
//            contentStream.showText(String.join(",", consumedItem.getItem()));
//            contentStream.newLineAtOffset(100, 0);
//            contentStream.showText(String.join(",", consumedItem.getSubLocations()));
//            contentStream.newLineAtOffset(100, 0);
//            contentStream.showText(String.join(",", consumedItem.getQuantity()));
//            // Add more data as needed
//
//            contentStream.newLineAtOffset(-400, -15);
//        }
//
//        contentStream.close();
//
//        // Write the PDF content to a ByteArrayOutputStream
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        document.save(outputStream);
//        document.close();
//
//        return outputStream.toByteArray();
//    }

}
