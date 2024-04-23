package com.inventory.project.helper;

import com.inventory.project.model.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Helper {
    public static boolean checkExcelFormat(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }

    }


    public static List<Location> convertExcelToLocations(InputStream is) {
        return convertExcelToList(is, "Sheet1", Location.class);
    }

    private static <T> List<T> convertExcelToList(InputStream is, String sheetName, Class<T> entityClass) {
        List<T> list = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet != null) {
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (row.getRowNum() == 0) { // Skip header row
                        continue;
                    }
                    T entity = parseRowToEntity(row, entityClass);
                    if (entity != null) {
                        list.add(entity);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static <T> T parseRowToEntity(Row row, Class<T> entityClass) {
        T entity = null;
        try {
            if (entityClass.equals(Location.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                ((Location) entity).setLocationName(getStringValue(row.getCell(0)));

                // Assuming the address is in the second column
                Address address = new Address(getStringValue(row.getCell(1)));
                ((Location) entity).addAddress(address);
            } else if (entityClass.equals(Address.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                ((Address) entity).setAddress(getStringValue(row.getCell(0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }



    private static String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return null;
    }

    public static List<Category> convertExcelToListOfProduct(InputStream is) {
        List<Category> list = new ArrayList<>();

        try {


            XSSFWorkbook workbook = new XSSFWorkbook(is);

            XSSFSheet sheet = workbook.getSheet("Sheet1");

            int rowNumber = 0;
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row row = iterator.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cells = row.iterator();

                int cid = 0;

                Category p = new Category();

                while (cells.hasNext()) {
                    Cell cell = cells.next();

                    switch (cid) {
                        case 0:
                            p.setId(Long.valueOf((int) cell.getNumericCellValue()));
                            break;
                        case 1:
                            p.setName(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cid++;

                }

                list.add(p);


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

}
