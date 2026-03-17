package co.com.screenplay.project.ui;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;

/**
 * Page Object para la página de lista de productos de Swaglabs.
 * Contiene los targets de los elementos interactivos del catálogo.
 */
public class PageProducts extends PageObject {

    public static final Target SELECTED_PRODUCT =
            Target.the("Producto '{0}'").locatedBy("//div[normalize-space()='{0}']");

    public static final Target BUTTON_ADD_CART =
            Target.the("Botón agregar al carrito").locatedBy("//button[@id='add-to-cart']");

    public static final Target BUTTON_BACK_PRODUCTS =
            Target.the("Botón regresar a productos").locatedBy("//button[@id='back-to-products']");

    public static final Target BUTTON_CART =
            Target.the("Botón carrito de compras").locatedBy("//a[@class='shopping_cart_link']");
}

