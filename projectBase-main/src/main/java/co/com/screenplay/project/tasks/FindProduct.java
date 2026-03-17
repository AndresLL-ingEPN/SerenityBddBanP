package co.com.screenplay.project.tasks;

import co.com.screenplay.project.ui.PageProducts;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Task que busca un producto por nombre y lo agrega al carrito de compras.
 * Registra el nombre del producto en el log y en el reporte HTML.
 */
@Slf4j
public class FindProduct implements Task {

    private final String description;

    public FindProduct(String description) {
        this.description = description;
    }

    public static FindProduct withDescription(String description) {
        return instrumented(FindProduct.class, description);
    }

    @Step("{0} busca y agrega al carrito el producto: '#description'")
    @Override
    public <T extends Actor> void performAs(T actor) {
        log.info("[FindProduct] → Buscando producto en catálogo: '{}'", description);

        Serenity.recordReportData()
                .withTitle("🛍️ Producto seleccionado")
                .andContents("Nombre: " + description);

        actor.attemptsTo(
                Click.on(PageProducts.SELECTED_PRODUCT.of(description)),
                WaitUntil.the(PageProducts.BUTTON_ADD_CART, isVisible()),
                Click.on(PageProducts.BUTTON_ADD_CART),
                Click.on(PageProducts.BUTTON_BACK_PRODUCTS)
        );

        log.info("[FindProduct] ✔ Producto '{}' agregado al carrito exitosamente", description);
    }
}