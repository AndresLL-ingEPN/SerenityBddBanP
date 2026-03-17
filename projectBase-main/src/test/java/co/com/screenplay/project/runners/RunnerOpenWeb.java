package co.com.screenplay.project.runners;

import co.com.screenplay.project.util.constants.Paths;
import co.com.screenplay.project.util.overwritedata.FeatureOverwrite;
import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"co.com.screenplay.project.stepdefinition", "co.com.screenplay.project.glue"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "@FlujoCompleto3"
)
public class RunnerOpenWeb {

    private static final Logger log = LoggerFactory.getLogger(RunnerOpenWeb.class);

    static {
        try {
            File featuresDir = new File(Paths.featuresPath());
            if (featuresDir.exists()) {
                List<String> featureFiles = FeatureOverwrite.listFilesByFolder(featuresDir);
                for (String featureFile : featureFiles) {
                    log.info("Pre-procesando feature: {}", featureFile);
                    FeatureOverwrite.overwriteFeatureFileAdd(featureFile);
                }
                FeatureOverwrite.clearListFilesByFolder();
                log.info("Pre-procesamiento de features completado.");
            } else {
                log.warn("Directorio de features no encontrado: {}", featuresDir.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
