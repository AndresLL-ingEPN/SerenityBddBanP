package co.com.screenplay.project.tasks;

import co.com.screenplay.project.model.demoblaze.ModelCustomer;
import co.com.screenplay.project.ui.PageCar;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Task que completa el formulario de información del cliente en el checkout de Swaglabs
 * y finaliza la compra. Registra los datos del cliente en log y en el reporte HTML.
 */
@Slf4j
public class RegisterCustomer implements Task {

    private final ModelCustomer data;

    public RegisterCustomer(ModelCustomer data) {
        this.data = data;
    }

    public static RegisterCustomer withInformation(ModelCustomer data) {
        return instrumented(RegisterCustomer.class, data);
    }

    @Step("{0} completa el formulario de checkout y finaliza la compra")
    @Override
    public <T extends Actor> void performAs(T actor) {
        log.info("[RegisterCustomer] → Completando formulario de checkout");
        log.info("[RegisterCustomer] → Nombre: {} {} | Código postal: {}",
                data.getName(), data.getLastname(), data.getZip());

        Serenity.recordReportData()
                .withTitle("📋 Datos del cliente en checkout")
                .andContents(String.format(
                        "Nombre    : %s%n" +
                        "Apellido  : %s%n" +
                        "Cód. Postal: %s",
                        data.getName(), data.getLastname(), data.getZip()
                ));

        actor.attemptsTo(
                WaitUntil.the(PageCar.LABEL_FIRST_NAME, isVisible()),
                Enter.theValue(data.getName()).into(PageCar.LABEL_FIRST_NAME),
                Enter.theValue(data.getLastname()).into(PageCar.LABEL_LAST_NAME),
                Enter.theValue(data.getZip()).into(PageCar.LABEL_ZIP_CODE),
                Click.on(PageCar.BUTTON_CONTINUE),
                Scroll.to(PageCar.BUTTON_FINISH),
                WaitUntil.the(PageCar.BUTTON_FINISH, isVisible()),
                Click.on(PageCar.BUTTON_FINISH)
        );

        log.info("[RegisterCustomer] ✔ Formulario completado y compra finalizada");
    }
}