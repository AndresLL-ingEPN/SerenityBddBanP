package co.com.screenplay.project.runners;

import co.com.screenplay.project.util.constants.Paths;
import co.com.screenplay.project.util.execution.AfterSuite;
import co.com.screenplay.project.util.execution.BeforeSuite;
import co.com.screenplay.project.util.execution.ControlExecution;
import co.com.screenplay.project.util.execution.CustomCucumberWithSerenityRunner;
import co.com.screenplay.project.util.overwritedata.FeatureOverwrite;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static co.com.screenplay.project.util.constants.Constants.EXT_FEATURE;

@RunWith(CustomCucumberWithSerenityRunner.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"co.com.screenplay.project.stepdefinition", "co.com.screenplay.project.glue"},
        plugin = {"json:build/cucumber-reports/json/cucumber.json", "summary"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "@FlujoCompletoBP"
)

public class RunnerOpenWeb {

    private static final Logger log = LoggerFactory.getLogger(RunnerOpenWeb.class);

    private RunnerOpenWeb() {
    }

    @BeforeSuite
    public static void init() throws IOException {
        ControlExecution.featuresSegmentation();
        List<String> features = FeatureOverwrite.listFilesByFolder(new File(Paths.featuresPath()));
        for (String feature : features) {
            if (feature.contains(EXT_FEATURE)) {
                FeatureOverwrite.overwriteFeatureFileAdd(feature);
            }
        }
        FeatureOverwrite.clearListFilesByFolder();
    }

    @AfterSuite
    public static void after() throws IOException {
        List<String> features = FeatureOverwrite.listFilesByFolder(new File(Paths.featuresPath()));
        for (String feature : features) {
            if (feature.contains(EXT_FEATURE)) {
                FeatureOverwrite.restoreFeatureFiles(feature);
            }
        }
        FeatureOverwrite.clearListFilesByFolder();
        log.info("=====> End Execution");
    }
}