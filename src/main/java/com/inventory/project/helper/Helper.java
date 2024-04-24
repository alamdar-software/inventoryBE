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
    public static List<Category> convertExcelToCategories(InputStream is) {
        return convertExcelToList(is, "Sheet1", Category.class);
    }
    public static List<Unit> convertExcelToUnits(InputStream is) {
        return convertExcelToList(is, "Sheet1", Unit.class);
    }

    public static List<Item> convertExcelToItems(InputStream is) {
        return convertExcelToList(is, "Sheet1", Item.class);
    }
    public static List<Brand> convertExcelToBrands(InputStream is) {
        return convertExcelToList(is, "Sheet1", Brand.class);
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
            } else if (entityClass.equals(Category.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                // Assuming name is in the second column
                Cell nameCell = row.getCell(1);
                if (nameCell != null) {
                    ((Category) entity).setName(getStringValue(nameCell));
                }
            }else if (entityClass.equals(Unit.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                // Assuming unitName is in the third column (index 2)
                Cell unitNameCell = row.getCell(2);
                if (unitNameCell != null) {
                    ((Unit) entity).setUnitName(getStringValue(unitNameCell));
                }
            }else if (entityClass.equals(Item.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                Cell descriptionCell = row.getCell(0); // Assuming description is in the first column (index 0)
                Cell nameCell = row.getCell(1); // Assuming name is in the second column (index 1)
                Cell unitNameCell = row.getCell(2); // Assuming unitName is in the third column (index 2)
                Cell minimumStockCell = row.getCell(3); // Assuming minimumStock is in the fourth column (index 3)

                if (descriptionCell != null) {
                    ((Item) entity).setDescription(getStringValue(descriptionCell));
                }
                if (nameCell != null) {
                    ((Item) entity).setName(getStringValue(nameCell));
                }
                if (unitNameCell != null) {
                    ((Item) entity).setUnitName(getStringValue(unitNameCell));
                }   if (minimumStockCell != null) {
                    // Convert the numeric value to string and set it as minimumStock
                    if (minimumStockCell.getCellType() == CellType.NUMERIC) {
                        double numericValue = minimumStockCell.getNumericCellValue();
                        ((Item) entity).setMinimumStock(String.valueOf((int) numericValue));
                    } else {
                        ((Item) entity).setMinimumStock(getStringValue(minimumStockCell));
                    }
                }

            } else if (entityClass.equals(Brand.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                Cell brandCell = row.getCell(0); // Assuming brand name is in the first column (index 0)
                if (brandCell != null) {
                    ((Brand) entity).setBrandName(getStringValue(brandCell));
                }
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


}
