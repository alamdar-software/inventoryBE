package com.inventory.project.helper;

import com.inventory.project.model.Category;
import com.inventory.project.model.Item;
import com.inventory.project.model.Unit;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

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


    //convert excel to list of products
    public static List<Category> convertExcelToCategories(InputStream is) {
        return convertExcelToList(is, "Sheet1", Category.class);
    }

    public static List<Unit> convertExcelToUnits(InputStream is) {
        return convertExcelToList(is, "Sheet1", Unit.class);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static <T> T parseRowToEntity(Row row, Class<T> entityClass) {
        T entity = null;

        try {
            if (entityClass.equals(Category.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                ((Category) entity).setName(getStringValue(row.getCell(0)));
                // Set other properties of Category if needed
            } else if (entityClass.equals(Unit.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                ((Unit) entity).setUnitName(getStringValue(row.getCell(0)));
                // Set other properties of Unit if needed
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
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return null;
    }
}
