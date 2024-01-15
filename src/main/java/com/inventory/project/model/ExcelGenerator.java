//package com.inventory.project.model;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
//public class ExcelGenerator {
//
//    public static byte[] generateExcel(List<Location> locations) throws IOException {
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("Locations");
//
//            // Create header row
//            Row headerRow = sheet.createRow(0);
//            headerRow.createCell(0).setCellValue("Location ID");
//            headerRow.createCell(1).setCellValue("Location Name");
//            // Add more columns as needed
//
//            // Fill data rows
//            int rowNum = 1;
//            for (Location location : locations) {
//                Row row = sheet.createRow(rowNum++);
//                row.createCell(0).setCellValue(location.getId());
//                row.createCell(1).setCellValue(location.getLocationName());
//                // Add more cells for other properties
//
//                // If you have a list of addresses and want to include them, loop through and add more cells
//                // List<Address> addresses = location.getAddresses();
//                // for (Address address : addresses) {
//                //     row.createCell(cellIndex++).setCellValue(address.getAddress());
//                // }
//            }
//
//            // Write the workbook content to a ByteArrayOutputStream
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            workbook.write(outputStream);
//
//            return outputStream.toByteArray();
//        }
//    }
//}
