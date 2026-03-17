package co.com.screenplay.project.ui;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;

/**
 * Page Object para las páginas de carrito y checkout de Swaglabs.
 * Contiene los targets del flujo de compra: carrito → checkout → confirmación.
 */
public class PageCar extends PageObject {

    public static final Target BUTTON_CHECK_OUT =
            Target.the("Botón checkout de compras").locatedBy("//button[@id='checkout']");

    public static final Target LABEL_FIRST_NAME =
            Target.the("Campo Primer Nombre").locatedBy("//input[@id='first-name']");

    public static final Target LABEL_LAST_NAME =
            Target.the("Campo Apellido").locatedBy("//input[@id='last-name']");

    public static final Target LABEL_ZIP_CODE =
            Target.the("Campo Código Postal").locatedBy("//input[@id='postal-code']");

    public static final Target BUTTON_CONTINUE =
            Target.the("Botón Continuar").locatedBy("//input[@id='continue']");

    public static final Target BUTTON_FINISH =
            Target.the("Botón Finalizar compra").locatedBy("//button[@id='finish']");

    public static final Target LABEL_SUCCESSFULL_PURCHASE =
            Target.the("Mensaje compra exitosa").locatedBy("//h2[normalize-space()='Thank you for your order!']");
}

