package co.com.screenplay.project.enhancers;

import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Enhancer que configura el path local del msedgedriver antes de cada escenario.
 * Migrado de WebDriverManagerEnhancer (eliminado en Serenity 4.x)
 * a BeforeAWebdriverScenario (API vigente en Serenity 4.x).
 */
public class LocalEdgeDriverEnhancer implements BeforeAWebdriverScenario {

    private static final Logger log = LoggerFactory.getLogger(LocalEdgeDriverEnhancer.class);
    private static final String DEFAULT_EDGE_DRIVER_VERSION = "146.0.3856.59";

    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     MutableCapabilities capabilities) {
        String version = System.getProperty("edge.driver.version", DEFAULT_EDGE_DRIVER_VERSION);
        String driverPath = System.getProperty("webdriver.edge.driver");

        if (driverPath != null && new File(driverPath).exists()) {
            log.info("LocalEdgeDriverEnhancer: usando driver local {} (versión {})", driverPath, version);
        } else {
            log.info("LocalEdgeDriverEnhancer: WebDriverManager automático para edge versión {}", version);
        }
        return capabilities;
    }
}
