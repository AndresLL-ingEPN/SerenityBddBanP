package co.com.screenplay.project.util.execution;

import co.com.screenplay.project.util.EnvironmentConfig;
import co.com.screenplay.project.util.constants.Paths;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnStage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static co.com.screenplay.project.util.constants.Constants.ATTRIB_REPORT_ASSETS_DIRECTORY;
import static co.com.screenplay.project.util.constants.Messages.ERROR_FILE_NOT_FOUND;
import static co.com.screenplay.project.util.constants.Messages.ST_UNSUPPORTED_EVIDENCE_FORMAT;

@Slf4j
public class AttachEvidenceToScenario {

    private final DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private EnvironmentConfig environmentConfig = new EnvironmentConfig();

    public void addScreenshot(Scenario scenario) {
        scenario.attach(
                ((TakesScreenshot) BrowseTheWeb.as(OnStage.theActorInTheSpotlight()).getDriver()).getScreenshotAs(OutputType.BYTES),
                "image/png",
                String.format("%s %s.jpg", scenario.getName(), formatDate.format(new Date()))
        );
    }

    public void addEvidenceManualTest(Scenario scenario) throws IOException {
        String[] tagsScenario = scenario.getSourceTagNames().toArray(new String[0]);
        for (String lineTag : tagsScenario) {
            if (lineTag.contains("@manual-test-evidence:")) {
                String[] numberEvidence = lineTag.split(",");
                for (String e : numberEvidence) {
                    String nameEvidence = StringUtils.substringBetween(e, "(", ")");
                    nameEvidence = nameEvidence.substring(6);
                    FileInputStream fileInputStream;
                    try {
                        fileInputStream = new FileInputStream(Paths.validatePath(environmentConfig.getVariable(ATTRIB_REPORT_ASSETS_DIRECTORY) + nameEvidence));
                    } catch (Exception exc) {
                        log.error(ERROR_FILE_NOT_FOUND + exc.getMessage());
                        continue;
                    }
                    byte[] fileInBytes = IOUtils.toByteArray(fileInputStream);
                    validateFileType(nameEvidence, scenario, fileInBytes);
                }
            }
        }
    }

    private void validateFileType(String nameEvidence, Scenario scenario, byte[] fileInBytes) {
        int pointIndex = nameEvidence.trim().indexOf(".");
        String extension = nameEvidence.trim().toLowerCase().substring(pointIndex);
        switch (extension) {
            case ".jpg":
            case ".png":
            case ".jpeg":
                scenario.attach(fileInBytes,
                    "image/png",
                    String.format("%s %s.jpg", scenario.getName(), formatDate.format(new Date())));
                break;
            case ".txt":
                scenario.attach(fileInBytes,
                    "text/plain",
                    String.format("%s %s.txt", scenario.getName(), formatDate.format(new Date())));
                break;
            case ".docx":
                scenario.attach(fileInBytes,
                    "application/msword",
                    String.format("%s %s.docx", scenario.getName(), formatDate.format(new Date())));
                break;
            case ".pdf":
                scenario.attach(fileInBytes,
                    "application/pdf",
                    String.format("%s %s.pdf", scenario.getName(), formatDate.format(new Date())));
                break;
            case ".html":
                scenario.attach(fileInBytes,
                    "text/html",
                    String.format("%s %s.html", scenario.getName(), formatDate.format(new Date())));
                break;
            case ".rar":
                scenario.attach(fileInBytes,
                    "application/x-rar-compressed",
                    String.format("%s %s.rar", scenario.getName(), formatDate.format(new Date())));
                break;
            case ".zip":
                scenario.attach(fileInBytes,
                    "application/zip",
                    String.format("%s %s.zip", scenario.getName(), formatDate.format(new Date())));
                break;
            case ".mp4":
                scenario.attach(fileInBytes,
                    "video/mp4",
                    String.format("%s %s.mp4", scenario.getName(), formatDate.format(new Date())));
                break;
            case ".mp3":
                scenario.attach(fileInBytes,
                    "audio/mpeg",
                    String.format("%s %s.mp3", scenario.getName(), formatDate.format(new Date())));
                break;
            default:
                throw new IllegalStateException(ST_UNSUPPORTED_EVIDENCE_FORMAT + nameEvidence);
        }
    }
}