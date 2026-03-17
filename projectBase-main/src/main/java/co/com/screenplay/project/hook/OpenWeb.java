package co.com.screenplay.project.hook;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.annotations.Step;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

import static co.com.screenplay.project.util.Constant.WEB_URL;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Task que abre el navegador y navega a la URL configurada en serenity.conf.
 * Registra la URL en el log y en el reporte HTML de Serenity.
 */
@Slf4j
public class OpenWeb implements Task {

    /**
     * Abre el navegador y navega a la URL configurada en serenity.conf.
     */
    public static Performable browserUrl() {
        return instrumented(OpenWeb.class);
    }

    @Step("{0} abre el navegador y navega a la URL configurada")
    @Override
    public <T extends Actor> void performAs(T actor) {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();
        String pathWebUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty(WEB_URL);

        log.info("[OpenWeb] → Abriendo navegador");
        log.info("[OpenWeb] → URL destino: {}", pathWebUrl);

        Serenity.recordReportData()
                .withTitle("🌐 URL de la aplicación bajo prueba")
                .andContents(pathWebUrl);

        actor.attemptsTo(Open.url(pathWebUrl));

        log.info("[OpenWeb] ✔ Navegador abierto en: {}", pathWebUrl);
    }
}
