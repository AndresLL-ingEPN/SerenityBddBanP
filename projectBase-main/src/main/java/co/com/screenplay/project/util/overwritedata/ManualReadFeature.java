package co.com.screenplay.project.util.overwritedata;

import co.com.screenplay.project.exceptions.ExcRuntime;
import co.com.screenplay.project.util.constants.Constants;
import co.com.screenplay.project.util.constants.Messages;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class ManualReadFeature {

    public static final String MANUAL_RESULT_PASSED = "  @manual-result:passed";
    public static final String MANUAL_RESULT_FAILED = "  @manual-result:failed";
    public static final String MANUAL_RESULT_UNDEFINED = "  #EstadoScenarioNoDefinido";
    public static final String STATUS_PASSED = "passed";
    public static final String STATUS_FAILED = "failed";

    private ManualReadFeature() {
    }

    public static String setPassedOrFailedFromPane(String featureName, String nameScenario, int numScenario) {
        String statusExecution;
        String[] options = {"   No   ", "   Si   "};
        numScenario = numScenario + 1;

        JOptionPane jOptionPane = new JOptionPane("El  \"" + nameScenario.trim() + "\"  se ejecutó correctamente?",
                JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION,
                null, options, options[0]);
        JDialog jDialog = jOptionPane.createDialog(null, featureName + "   -->   Scenario N° " + numScenario);
        jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialog.setVisible(true);
        String optionSelected = (String) jOptionPane.getValue();
        if (optionSelected.trim().equals("No")) {
            statusExecution = MANUAL_RESULT_FAILED;
        } else {
            statusExecution = MANUAL_RESULT_PASSED;
        }
        return statusExecution;
    }

    public static String setPassedOrFailedFromCSV(int numScenario, String filePath) {
        String lineData;
        String statusExecution = MANUAL_RESULT_UNDEFINED;
        numScenario = numScenario + 1;

        try (BufferedReader bfReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath)), StandardCharsets.UTF_8))) {
            while ((lineData = bfReader.readLine()) != null) {
                String[] numberAndResultTest = lineData.split(",");
                String columnOne = numberAndResultTest[0];
                if (columnOne.trim().equalsIgnoreCase(String.valueOf(numScenario))) {
                    switch (numberAndResultTest[1].trim().toLowerCase()) {
                        case STATUS_FAILED:
                            statusExecution = MANUAL_RESULT_FAILED;
                            break;
                        case STATUS_PASSED:
                            statusExecution = MANUAL_RESULT_PASSED;
                            break;
                        default:
                            log.warn(String.format(Messages.STATUS_NOT_DEFINED_FOR_SCENARIO, numScenario, filePath));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            log.error(Messages.ERROR_READ_FILE + e.getMessage(), e);
        }
        return statusExecution;
    }

    private static String readManualFeaturePassedOrdFailed(final File featureFile, final int lineScenario) throws IOException {
        String resultScenario = "";
        BufferedReader buffReaderScenario = null;
        try {
            buffReaderScenario = Files.newBufferedReader(Paths.get(featureFile.getAbsolutePath()), StandardCharsets.UTF_8);
            String lineOfFeatureFile;
            int countLine = 1;
            while ((lineOfFeatureFile = buffReaderScenario.readLine()) != null) {
                if ((lineOfFeatureFile.trim().contains("@manual-result:") || lineOfFeatureFile.trim().contains(MANUAL_RESULT_UNDEFINED))
                        && countLine == lineScenario - 1) {
                    resultScenario = lineOfFeatureFile;
                }
                countLine++;
            }
        } finally {
            if (buffReaderScenario != null) {
                try {
                    buffReaderScenario.close();
                } catch (IOException e) {
                    log.error(Messages.ERROR_CLOSING_BUFFEREDREADER + e.getMessage(), e);
                }
            }
        }
        return resultScenario;
    }

    public static void validateScenario(Scenario scenario) {
        String nameFile = "isManualTests";
        File file = new File(System.getProperty(Constants.USER_DIR) + File.separator + Constants.TARGET + File.separator + nameFile);
        try {
            boolean fileCreated = file.createNewFile();
            if (fileCreated) log.info(String.format(Messages.FILE_CREATED_SUCCESSFULLY, nameFile));
        } catch (IOException e) {
            log.error(Messages.ERROR_CREATING_FILE + nameFile + e.getMessage());
        }

        try {
            File featureFile = new File(scenario.getUri());
            int lineScenario = scenario.getLine();
            ManualReadFeature.validatePassedOrdFailed(featureFile, lineScenario);
        } catch (IOException e) {
            throw new ExcRuntime(Messages.ERROR_VALIDATING_RESULT_MANUAL_SCENARIO + e.getMessage(), e);
        }
    }

    private static void validatePassedOrdFailed(File featureFile, int lineScenario) throws IOException {
        String passedOrdFailed = readManualFeaturePassedOrdFailed(featureFile, lineScenario);
        String status;
        if (passedOrdFailed.contains(STATUS_PASSED)) {
            status = STATUS_PASSED;
        } else if (passedOrdFailed.contains(STATUS_FAILED)) {
            status = STATUS_FAILED;
        } else {
            throw new io.cucumber.java.PendingException(Messages.ERROR_CHECK_DATA_PATH);
        }
        Assert.assertEquals(Messages.ST_MANUAL_SCENARIO_STATE, STATUS_PASSED, status);
    }
}