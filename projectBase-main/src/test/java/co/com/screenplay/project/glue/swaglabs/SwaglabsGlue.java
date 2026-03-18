package co.com.screenplay.project.glue.swaglabs;

import co.com.screenplay.project.hook.OpenWeb;
import co.com.screenplay.project.model.demoblaze.ModelCredentials;
import co.com.screenplay.project.model.demoblaze.ModelCustomer;
import co.com.screenplay.project.tasks.FindProduct;
import co.com.screenplay.project.tasks.MakeLogin;
import co.com.screenplay.project.tasks.RegisterCustomer;
import co.com.screenplay.project.tasks.ViewCart;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.actors.OnStage;

import static co.com.screenplay.project.ui.PageCar.LABEL_SUCCESSFULL_PURCHASE;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isPresent;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

/**
 * Step definitions para el flujo completo de compra en Swaglabs con Scenario Outline.
 * Los datos provienen de un archivo Excel externo mediante el mecanismo @externaldata.
 *
 * <p>Patrón de diseño: Screenplay (Actor → Task → Interaction → UI)</p>
 * <p>Fuente de datos: resources/data/registro/dataDemoblaze.xlsx..compra</p>
 */
@Slf4j
public class SwaglabsGlue {

    /**
     * Abre el navegador, realiza el login y agrega el primer producto al carrito.
     * Los parámetros provienen directamente de las columnas del Excel.
     *
     * @param actor            nombre del actor Screenplay (ej. "cliente")
     * @param producto         nombre del primer producto (columna 'producto' del Excel)
     * @param user             usuario de acceso (columna 'user' del Excel)
     * @param password         contraseña de acceso (columna 'password' del Excel)
     */
    @Given("que el {string} inicia sesion con {string} y {string} y selecciona el producto {string}")
    public void queElIniciaSesionYSeleccionaElProducto(String actor, String user, String password, String producto) {
        log.info("══════════════════════════════════════════════════════");
        log.info("[GIVEN] Actor: '{}' | Usuario: '{}' | Producto inicial: '{}'", actor, user, producto);
        log.info("[GIVEN] Ejecutando: OpenWeb → MakeLogin → FindProduct");

        ModelCredentials credentials = new ModelCredentials(user, password);

        OnStage.theActorCalled(actor).attemptsTo(
                OpenWeb.browserUrl(),
                MakeLogin.withCredentials(credentials),
                FindProduct.withDescription(producto)
        );

        log.info("[GIVEN] ✔ Sesión iniciada y producto '{}' agregado al carrito", producto);
    }

    /**
     * Selecciona el segundo producto y lo agrega al carrito.
     *
     * @param producto1 nombre del segundo producto (columna 'producto1' del Excel)
     */
    @And("el cliente agrega el segundo producto {string} al carrito")
    public void elClienteAgregaElSegundoProductoAlCarrito(String producto1) {
        log.info("══════════════════════════════════════════════════════");
        log.info("[AND] Seleccionando segundo producto: '{}'", producto1);

        OnStage.theActorInTheSpotlight().attemptsTo(
                FindProduct.withDescription(producto1)
        );

        log.info("[AND] ✔ Segundo producto '{}' agregado al carrito", producto1);
    }

    /**
     * Navega al carrito y completa el formulario de checkout con los datos del cliente.
     *
     * @param name     nombre del cliente (columna 'name' del Excel)
     * @param lastname apellido del cliente (columna 'lastname' del Excel)
     * @param zip      código postal del cliente (columna 'zip' del Excel)
     */
    @When("el cliente procede al checkout con nombre {string} apellido {string} y zip {string}")
    public void elClienteProcedAlCheckout(String name, String lastname, String zip) {
        log.info("══════════════════════════════════════════════════════");
        log.info("[WHEN] Checkout → Nombre: '{}' | Apellido: '{}' | ZIP: '{}'", name, lastname, zip);

        ModelCustomer customer = new ModelCustomer(name, lastname, zip);

        OnStage.theActorInTheSpotlight().wasAbleTo(
                ViewCart.withProducts(),
                RegisterCustomer.withInformation(customer)
        );

        log.info("[WHEN] ✔ Carrito visualizado y datos del cliente ingresados correctamente");
    }

    /**
     * Verifica que el mensaje de compra exitosa sea visible en pantalla.
     */
    @Then("el cliente verifica que la compra fue realizada exitosamente")
    public void elClienteVerificaQuelaCompraFueExitosa() {
        log.info("══════════════════════════════════════════════════════");
        log.info("[THEN] Validando confirmación de compra exitosa...");

        OnStage.theActorInTheSpotlight().should(
                seeThat(the(LABEL_SUCCESSFULL_PURCHASE), isPresent())
        );

        log.info("[THEN] ✔ Compra confirmada - 'Thank you for your order!' visible en pantalla");
        log.info("══════════════════════════════════════════════════════");
    }
}
