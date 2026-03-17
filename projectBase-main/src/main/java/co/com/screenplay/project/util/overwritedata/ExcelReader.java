package co.com.screenplay.project.util.overwritedata;

import co.com.screenplay.project.util.constants.Messages;
import co.com.screenplay.project.exceptions.ExcInvalidArgument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class ExcelReader {

    public static List<Map<String, String>> getData(final String excelFilePath) {
        String sheetName = getSheetName(excelFilePath);
        String filePath = getPathFile(excelFilePath);
        try {
            final Sheet sheet = getSheetByName(filePath, sheetName);
            return readSheet(sheet);
        } catch (Exception e) {
            log.error(Messages.ERROR_READ_FILE + e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public List<Map<String, String>> getData(final String excelFilePath, final int sheetNumber) throws IOException {
        final Sheet sheet = getSheetByIndex(excelFilePath, sheetNumber);
        return readSheet(sheet);
    }

    private static Sheet getSheetByName(final String excelFilePath, final String sheetName) throws IOException {
        return getWorkBook(excelFilePath).getSheet(sheetName);
    }

    private static Sheet getSheetByIndex(final String excelFilePath, final int sheetNumber) throws IOException {
        return getWorkBook(excelFilePath).getSheetAt(sheetNumber);
    }

    private static Workbook getWorkBook(final String excelFilePath) throws IOException {
        return WorkbookFactory.create(new File(excelFilePath));
    }

    private static List<Map<String, String>> readSheet(final Sheet sheet) {
        Row row;
        final int totalRow = sheet.getPhysicalNumberOfRows();
        final List<Map<String, String>> excelRows = new ArrayList<>();
        final int headerRowNumber = getHeaderRowNumber(sheet);
        if (headerRowNumber != -1) {
            final int totalColumn = sheet.getRow(headerRowNumber).getLastCellNum();
            final int setCurrentRow = 1;
            boolean hasData = true;
            for (int currentRow = setCurrentRow; hasData && currentRow <= totalRow; currentRow++) {
                row = getRow(sheet, sheet.getFirstRowNum() + currentRow);
                final LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
                for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
                    final LinkedHashMap<String, String> cellValue = getCellValue(sheet, row, currentColumn);
                    if (0 == currentColumn && StringUtils.isEmpty(cellValue.get(cellValue.keySet().iterator().next()))) {
                        hasData = false;
                        break;
                    }
                    columnMapdata.putAll(cellValue);
                }
                excelRows.add(columnMapdata);
            }
        }
        return excelRows;
    }


    private static int getHeaderRowNumber(final Sheet sheet) {
        Row row;
        final int totalRow = sheet.getLastRowNum();
        for (int currentRow = 0; currentRow <= totalRow + 1; currentRow++) {
            row = getRow(sheet, currentRow);
            if (row != null) {
                final int totalColumn = row.getLastCellNum();
                for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
                    Cell cell;
                    cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.BOOLEAN || cell.getCellType() == CellType.ERROR) {
                        return row.getRowNum();
                    }
                }
            }
        }
        return -1;
    }

    private static Row getRow(final Sheet sheet, final int rowNumber) {
        return sheet.getRow(rowNumber);
    }

    private static LinkedHashMap<String, String> getCellValue(final Sheet sheet, final Row row, final int currentColumn) {
        final LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
        Cell cell;
        if (row == null) {
            if (sheet.getRow(sheet.getFirstRowNum()).getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() != CellType.BLANK) {
                final String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(currentColumn).getStringCellValue();
                columnMapdata.put(columnHeaderName, "");
            }
        } else {
            cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (validateCellType(sheet, cell)) {
                if (cell.getCellType() == CellType.STRING) {
                    final String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex()).getStringCellValue();
                    columnMapdata.put(columnHeaderName, cell.getStringCellValue());
                } else if (cell.getCellType() == CellType.NUMERIC) {
                    final String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex()).getStringCellValue();
                    columnMapdata.put(columnHeaderName, NumberToTextConverter.toText(cell.getNumericCellValue()));
                } else if (cell.getCellType() == CellType.BLANK) {
                    final String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex()).getStringCellValue();
                    columnMapdata.put(columnHeaderName, "");
                } else if (cell.getCellType() == CellType.BOOLEAN) {
                    final String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex()).getStringCellValue();
                    columnMapdata.put(columnHeaderName, Boolean.toString(cell.getBooleanCellValue()));
                } else if (cell.getCellType() == CellType.ERROR) {
                    final String columnHeaderName = sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex()).getStringCellValue();
                    columnMapdata.put(columnHeaderName, Byte.toString(cell.getErrorCellValue()));
                } else {
                    throw new ExcInvalidArgument(Messages.ERROR_CELL_TYPE_NOT_RECOGNIZED);
                }
            }
        }
        return columnMapdata;
    }

    public static boolean validateCellType(final Sheet sheet, final Cell cell) {
        return sheet.getRow(sheet.getFirstRowNum()).getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() != CellType.BLANK;
    }

    public static String getSheetName(String excelFilePath) {
        String sheetName = "";
        if (excelFilePath.trim().contains("..")) {
            sheetName = excelFilePath.substring(excelFilePath.indexOf("..") + 2).trim();
        } else {
            log.error(Messages.ERROR_EXCEL_TAB_NOT_DECLARED + excelFilePath);
        }
        return sheetName;
    }

    public static String getPathFile(String excelFilePath) {
        String path = "";
        if (excelFilePath.trim().contains("..")) {
            path = excelFilePath.substring(0, excelFilePath.indexOf("..")).trim();
        } else {
            log.error(Messages.ERROR_EXCEL_PATH_NOT_DECLARED + excelFilePath);
        }
        return path;
    }
}