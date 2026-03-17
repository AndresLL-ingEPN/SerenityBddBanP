package co.com.screenplay.project.tasks;

import co.com.screenplay.project.ui.PageCar;
import co.com.screenplay.project.ui.PageProducts;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Task que navega al carrito de compras e inicia el proceso de checkout.
 */
@Slf4j
public class ViewCart implements Task {

    public static ViewCart withProducts() {
        return instrumented(ViewCart.class);
    }

    @Step("{0} navega al carrito e inicia el checkout")
    @Override
    public <T extends Actor> void performAs(T actor) {
        log.info("[ViewCart] → Abriendo carrito de compras");

        actor.attemptsTo(
                Click.on(PageProducts.BUTTON_CART),
                Scroll.to(PageCar.BUTTON_CHECK_OUT),
                WaitUntil.the(PageCar.BUTTON_CHECK_OUT, isVisible()),
                Click.on(PageCar.BUTTON_CHECK_OUT)
        );

        log.info("[ViewCart] ✔ Carrito abierto y checkout iniciado");
    }
}