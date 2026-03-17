package co.com.screenplay.project.stepdefinition.hook;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

/**
 * Hook de Cucumber que inicializa el stage de actores Screenplay
 * antes de cada escenario y registra el resultado al finalizar.
 */
@Slf4j
public class Hook {

    @Before
    public void setTheStage(Scenario scenario) {
        log.info("╔══════════════════════════════════════════════════════════════");
        log.info("║ [INICIO ESCENARIO] {}", scenario.getName());
        log.info("║ Tags: {}", scenario.getSourceTagNames());
        log.info("╚══════════════════════════════════════════════════════════════");
        OnStage.setTheStage(new OnlineCast());
    }

    @After
    public void tearDown(Scenario scenario) {
        String status = scenario.getStatus().name();
        if (scenario.isFailed()) {
            log.error("╔══════════════════════════════════════════════════════════════");
            log.error("║ [FIN ESCENARIO - {}] {}", status, scenario.getName());
            log.error("╚══════════════════════════════════════════════════════════════");
        } else {
            log.info("╔══════════════════════════════════════════════════════════════");
            log.info("║ [FIN ESCENARIO - {}] {}", status, scenario.getName());
            log.info("╚══════════════════════════════════════════════════════════════");
        }
    }
}
