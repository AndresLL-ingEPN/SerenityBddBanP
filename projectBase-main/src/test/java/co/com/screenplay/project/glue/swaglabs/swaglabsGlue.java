package co.com.screenplay.project.glue.swaglabs;

import co.com.screenplay.project.hook.OpenWeb;
import co.com.screenplay.project.model.demoblaze.ModelCredentials;
import co.com.screenplay.project.model.demoblaze.ModelCustomer;
import co.com.screenplay.project.tasks.FindProduct;
import co.com.screenplay.project.tasks.MakeLogin;
import co.com.screenplay.project.tasks.RegisterCustomer;
import co.com.screenplay.project.tasks.ViewCart;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.actors.OnStage;

import java.util.List;

import static co.com.screenplay.project.ui.PageCar.LABEL_SUCCESSFULL_PURCHASE;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isPresent;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

/**
 * Step definitions para el flujo completo de compra en Swaglabs (saucedemo.com).
 * Implementa el patrón Screenplay: Actor → Task → Interaction → UI.
 *
 * NOTA: El nombre de clase correcto (PascalCase) es SwaglabsGlue.
 * Ver swaglabsGlue.java para la versión con nomenclatura Java correcta.
 */
@Slf4j
public class swaglabsGlue {

    @Given("que el {string} abre el sitio web de pruebas para seleccionar el {string}")
    public void queElAbreElSitioWebDePruebasParaSeleccionarEl(String actor, String descripcionProducto, DataTable dataTable) {
        log.info("──────────────────────────────────────────────");
        log.info("[GIVEN] Actor: '{}' | Producto inicial: '{}'", actor, descripcionProducto);
        log.info("[GIVEN] Ejecutando: OpenWeb → MakeLogin → FindProduct");

        OnStage.theActorCalled(actor).attemptsTo(
                OpenWeb.browserUrl(),
                MakeLogin.withCredentials(new ModelCredentials(dataTable)),
                FindProduct.withDescription(descripcionProducto)
        );

        log.info("[GIVEN] ✔ Sitio abierto, login realizado y producto '{}' agregado al carrito", descripcionProducto);
    }

    @And("el cliente selecciona otro {string}")
    public void elClienteSeleccionaOtro(String descripcionProducto) {
        log.info("──────────────────────────────────────────────");
        log.info("[AND] Seleccionando producto adicional: '{}'", descripcionProducto);

        OnStage.theActorInTheSpotlight().attemptsTo(
                FindProduct.withDescription(descripcionProducto)
        );

        log.info("[AND] ✔ Producto '{}' agregado al carrito", descripcionProducto);
    }

    @When("el decide hacer la compra, ingresa sus datos personales")
    public void elDecideHacerLaCompraIngresaSusDatosPersonales(List<List<String>> data) {
        log.info("──────────────────────────────────────────────");
        log.info("[WHEN] Iniciando proceso de compra: ViewCart → RegisterCustomer");

        OnStage.theActorInTheSpotlight().wasAbleTo(
                ViewCart.withProducts(),
                RegisterCustomer.withInformation(new ModelCustomer(data))
        );

        log.info("[WHEN] ✔ Carrito visualizado y datos del cliente ingresados");
    }

    @Then("el realiza la compra del producto exitosamente")
    public void elRealizaLaCompraDelProductoExitosamente() {
        log.info("──────────────────────────────────────────────");
        log.info("[THEN] Validando mensaje de compra exitosa en pantalla...");

        OnStage.theActorInTheSpotlight().should(
                seeThat(the(LABEL_SUCCESSFULL_PURCHASE), isPresent())
        );

        log.info("[THEN] ✔ Compra completada exitosamente - 'Thank you for your order!' visible");
        log.info("──────────────────────────────────────────────");
    }
}

