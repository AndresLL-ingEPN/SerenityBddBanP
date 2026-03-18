package co.com.screenplay.project.util.overwritedata;

import co.com.screenplay.project.util.PropertiesReader;
import co.com.screenplay.project.util.constants.Paths;
import co.com.screenplay.project.util.constants.Constants;
import co.com.screenplay.project.util.constants.Messages;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@Slf4j
public class FeatureOverwrite {

    static PropertiesReader readProperties = new PropertiesReader();
    private static List<String> featuresList = new ArrayList<>();
    private static boolean externalDataProcessIsEmpty;

    private FeatureOverwrite() {
    }

    public static void overwriteFeatureFileAdd(final String featurePath) throws IOException {
        addExternalDataToFeature(featurePath);
    }

    public static void restoreFeatureFiles(final String featurePath) throws IOException {
        restoreExternalDataToFeature(featurePath);
    }

    private static void restoreExternalDataToFeature(final String featurePath) throws IOException {
        File featureFile = new File(featurePath);
        List<String> restoredLines = new ArrayList<>();
        try (BufferedReader buffReader = Files.newBufferedReader(
                java.nio.file.Paths.get(featureFile.getAbsolutePath()), StandardCharsets.UTF_8)) {
            String line;
            boolean insideExamples = false;
            boolean externalDataRestored = false;
            while ((line = buffReader.readLine()) != null) {
                if (line.trim().toLowerCase().startsWith("examples")) {
                    insideExamples = true;
                    externalDataRestored = false;
                    restoredLines.add(line);
                    continue;
                }
                if (insideExamples) {
                    // Línea comentada que contiene @externaldata → restaurar sin el #
                    if (line.trim().startsWith("#") && line.contains("@externaldata")) {
                        String restored = line.trim().substring(1).trim();
                        restoredLines.add("      " + restored);
                        externalDataRestored = true;
                        continue;
                    }
                    // Saltar filas de datos generadas (headers e instancias del Excel)
                    if (externalDataRestored && line.trim().startsWith("|")) {
                        continue;
                    }
                    // Saltar la línea con headers generada por el Excel (antes de restaurar)
                    if (!externalDataRestored && line.trim().startsWith("|") && !line.contains("@externaldata")) {
                        continue;
                    }
                }
                restoredLines.add(line);
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                java.nio.file.Paths.get(featureFile.getAbsolutePath()), StandardCharsets.UTF_8)) {
            for (final String writeLine : restoredLines) {
                writer.write(writeLine);
                writer.write("\n");
            }
        }
    }

    private static void addExternalDataToFeature(final String featurePath) throws IOException {
        File featureFile = new File(featurePath);
        List<String> featureWithExternalData;
        if (featurePath.toLowerCase().contains("manual.feature")) {
            featureWithExternalData = impSetPaneOrCsvDataToFeature(featureFile);
        } else {
            featureWithExternalData = impSetFileDataToFeature(featureFile);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                java.nio.file.Paths.get(featureFile.getAbsolutePath()), StandardCharsets.UTF_8)) {
            for (final String writeLine : featureWithExternalData) {
                writer.write(writeLine);
                writer.write("\n");
            }
        }
    }

    private static void externalDataProcess(String data, List<String> fileData) {
        String filePath = getValidFilePath(data);
        List<Map<String, String>> externalData = getDataFromFile(Paths.dataPath() + Paths.validatePath(filePath));
        if (!externalData.isEmpty()) {
            externalDataProcessIsEmpty = false;
            Collection<String> headers = externalData.get(0).keySet();
            fileData.add(getGherkinExample(headers));
            for (int rowNumber = 0; rowNumber < externalData.size() - 1; rowNumber++) {
                Collection<String> rowValues = externalData.get(rowNumber).values();
                String example = getGherkinExample(rowValues);
                if (!"".equals(example))
                    fileData.add(example);
            }
        } else {
            externalDataProcessIsEmpty = true;
        }
    }

    private static List<String> impSetFileDataToFeature(final File featureFile) throws IOException {
        final List<String> fileData = new ArrayList<>();
        try (BufferedReader buffReader = Files.newBufferedReader(java.nio.file.Paths.get(featureFile.getAbsolutePath()), StandardCharsets.UTF_8)) {
            String data;
            List<String> staticDataExample = new ArrayList<>();
            boolean exampleData = false;
            while ((data = buffReader.readLine()) != null) {
                if (data.trim().contains("@externaldata")) {
                    externalDataProcess(data, fileData);
                    exampleData = false;
                    data = validateExternaldataProcess(data);
                } else if ((data.trim().startsWith("@") || data.trim().startsWith("Scenario")) && exampleData) {
                    exampleData = false;
                    fileData.addAll(staticDataExample);
                } else {
                    log.debug(Messages.DOES_NOT_MEET_CONDITIONS);
                }
                if (!exampleData) {
                    fileData.add(data);
                } else {
                    staticDataExample.add(data);
                }
                if (data.contains("Examples")) {
                    staticDataExample.clear();
                    exampleData = true;
                }
            }
            if (exampleData && !staticDataExample.isEmpty()) {
                fileData.addAll(staticDataExample);
                staticDataExample.clear();
            }
        }
        return fileData;
    }

    private static String validateExternaldataProcess(String data) {
        if (!externalDataProcessIsEmpty) {
            StringBuilder externalString = new StringBuilder("#").append(data);
            data = externalString.toString();
        }
        return data;
    }

    private static String getValidFilePath(String data) {
        return data.substring(StringUtils.ordinalIndexOf(data, "@", 2) + 1)
                .replace("|", "")
                .trim();
    }

    private static List<Map<String, String>> getDataFromFile(String filePath) {
        if (isCSV(filePath)) {
            return CSVReader.getData(filePath);
        } else {
            return ExcelReader.getData(filePath);
        }
    }

    private static boolean isCSV(String filePath) {
        return filePath.toLowerCase().trim().endsWith(Constants.EXT_CSV);
    }

    private static String getGherkinExample(Collection<String> examplesFields) {
        String example = "";
        for (String field : examplesFields) {
            example = String.format("%s|%s", example, field);
        }
        if ("".equals(example))
            return "";
        return example + "|";
    }

    private static List<String> impSetPaneOrCsvDataToFeature(final File featureFile) throws IOException {
        final List<String> fileData = new ArrayList<>();
        try (BufferedReader buffReader = Files.newBufferedReader(java.nio.file.Paths.get(featureFile.getAbsolutePath()), StandardCharsets.UTF_8);
             BufferedReader buffReaderScenario = Files.newBufferedReader(java.nio.file.Paths.get(featureFile.getAbsolutePath()), StandardCharsets.UTF_8)) {
            String data;
            String externalDataSt;
            final List<String> scenarios = new ArrayList<>();
            String nameScenario;
            int numScenario = 0;
            String azureOrLocalExecution = readProperties.getProperty(Constants.ATTRIB_AZURE_LOCAL_EXECUTION, Constants.PROP_MANUALTEST);
            while ((nameScenario = buffReaderScenario.readLine()) != null) {
                if (nameScenario.trim().contains("Scenario:")) {
                    scenarios.add(nameScenario);
                }
            }
            while ((data = buffReader.readLine()) != null) {
                boolean foundHashTag = data.trim().contains("@manual") && !(data.trim().contains("@manual-result:"));
                if (foundHashTag) {
                    if (azureOrLocalExecution.equalsIgnoreCase("azure")) {
                        String dataPassedOrFailed = featureFile.getName().replace(Constants.EXT_FEATURE, Constants.EXT_CSV);
                        dataPassedOrFailed = Paths.dataPath() + Constants.MANUALTEST + File.separator + dataPassedOrFailed;
                        externalDataSt = ManualReadFeature.setPassedOrFailedFromCSV(numScenario, dataPassedOrFailed);
                    } else {
                        externalDataSt = ManualReadFeature.setPassedOrFailedFromPane(featureFile.getName(), scenarios.get(numScenario), numScenario);
                    }
                    numScenario++;
                    StringBuilder externalString = new StringBuilder(data).append(" ").append(externalDataSt.trim());
                    data = externalString.toString();
                }
                fileData.add(data);
            }
        }
        return fileData;
    }


    public static List<String> listFilesByFolder(final File folder) {
        for (final File fileOrFolder : Objects.requireNonNull(folder.listFiles())) {
            if (fileOrFolder.isDirectory()) {
                listFilesByFolder(fileOrFolder);
            } else {
                featuresList.add(fileOrFolder.getAbsolutePath());
            }
        }
        return new ArrayList<>(featuresList);
    }


    public static void clearListFilesByFolder() {
        featuresList.clear();
    }
}