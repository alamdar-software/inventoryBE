package com.inventory.project.helper;

import com.inventory.project.controller.LocationController;
import com.inventory.project.model.*;
import com.inventory.project.repository.CategoryRepository;
import com.inventory.project.repository.UnitRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Service

public class Helper {
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final int MAX_DESCRIPTION_LENGTH = 255;


    public static boolean checkExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public List<Location> convertExcelToLocations(InputStream is) {
        return convertExcelToList(is, "Sheet1", Location.class);
    }

    public List<Category> convertExcelToCategories(InputStream is) {
        return convertExcelToList(is, "Sheet1", Category.class);
    }

    public List<Unit> convertExcelToUnits(InputStream is) {
        return convertExcelToList(is, "Sheet1", Unit.class);
    }

    public List<Item> convertExcelToItems(InputStream is) {
        return convertExcelToList(is, "Sheet1", Item.class);
    }

    public List<Brand> convertExcelToBrands(InputStream is) {
        return convertExcelToList(is, "Sheet1", Brand.class);
    }

    private <T> List<T> convertExcelToList(InputStream is, String sheetName, Class<T> entityClass) {
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

    private <T> T parseRowToEntity(Row row, Class<T> entityClass) {
        T entity = null;
        try {
            if (entityClass.equals(Location.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                ((Location) entity).setLocationName(getStringValue(row.getCell(0)));
                Address address = new Address(getStringValue(row.getCell(1)));
                ((Location) entity).addAddress(address);
            } else if (entityClass.equals(Address.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                ((Address) entity).setAddress(getStringValue(row.getCell(2)));
            } else if (entityClass.equals(Category.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                Cell nameCell = row.getCell(9); // Assuming name is in the third column (index 2)
                if (nameCell != null) {
                    ((Category) entity).setName(getStringValue(nameCell));
                }
            } else if (entityClass.equals(Unit.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                Cell unitNameCell = row.getCell(0); // Assuming unitName is in the tenth column (index 9)
                if (unitNameCell != null) {
                    ((Unit) entity).setUnitName(getStringValue(unitNameCell));
                }
            }else if (entityClass.equals(Item.class)) {
                entity = entityClass.getDeclaredConstructor().newInstance();

                // Assuming your columns are indexed properly based on your Excel sheet structure
                Cell itemNameCell = row.getCell(0); // Assuming item name is in the first column (index 0)
                Cell descriptionCell = row.getCell(5); // Assuming description is in the sixth column (index 5)
                Cell minimumStockCell = row.getCell(16); // Assuming minimumStock is in the seventeenth column (index 16)
                Cell unitNameCell = row.getCell(9); // Assuming unitName is in the tenth column (index 9)
                Cell categoryCell = row.getCell(2); // Assuming category name is in the third column (index 2)

                if (itemNameCell != null) {
                    ((Item) entity).setItemName(getStringValue(itemNameCell));
                }

                if (descriptionCell != null) {
                    String description = getStringValue(descriptionCell);
                    if (description.length() > MAX_DESCRIPTION_LENGTH) {
                        description = description.substring(0, MAX_DESCRIPTION_LENGTH);
                    }
                    ((Item) entity).setDescription(description);
                }

                if (minimumStockCell != null) {
                    // Convert the numeric value to string and set it as minimumStock
                    if (minimumStockCell.getCellType() == CellType.NUMERIC) {
                        double numericValue = minimumStockCell.getNumericCellValue();
                        ((Item) entity).setMinimumStock(String.valueOf((int) numericValue));
                    } else {
                        ((Item) entity).setMinimumStock(getStringValue(minimumStockCell));
                    }
                }
                if (unitNameCell != null) {
                    String unitName = getStringValue(unitNameCell);
                    Unit unit = unitRepository.findByUnitName(unitName);
                    if (unit != null) {
                        ((Item) entity).setUnit(unit);
                        ((Item) entity).setUnitName(unit.getUnitName());
                    } else {
                        System.out.println("Unit not found for name: " + unitName);
                        // Log or handle the error appropriately
                    }
                }

                // Handle Category
                if (categoryCell != null) {
                    String categoryName = getStringValue(categoryCell);
                    Category category = categoryRepository.findByName(categoryName);
                    if (category != null) {
                        ((Item) entity).setCategory(category);
                    } else {
                        System.out.println("Category not found for name: " + categoryName);
                        // Log or handle the error appropriately
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
    private String getStringValue(Cell cell) {
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
    public List<Item> convertExcelToItem(InputStream inputStream) throws IOException {
        List<Item> items = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    // Skip header row
                    continue;
                }

                Item item = new Item();

                // Assuming columns are in order: itemName, minimumStock, description, categoryName, unitName
                Cell minimumStockCell = row.getCell(3);
                Cell descriptionCell = row.getCell(1);
                Cell categoryNameCell = row.getCell(0);
                Cell unitNameCell = row.getCell(2);

                // Set item fields
                item.setMinimumStock(getStringValue(minimumStockCell));

                // Validate and set description with truncation if needed
                if (descriptionCell != null) {
                    String description = getStringValue(descriptionCell);
                    if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
                        description = description.substring(0, MAX_DESCRIPTION_LENGTH);
                    }
                    item.setDescription(description);
                } else {
                    // Handle null description scenario (optional)
                    item.setDescription(null); // or set a default value if needed
                }

                // You need to handle category and unit separately based on your logic
                // For simplicity, assuming you have a name string directly in the Excel cell
                item.setName(getStringValue(categoryNameCell));
                item.setUnitName(getStringValue(unitNameCell));

                items.add(item);
            }
        }

        return items;
    }


}
