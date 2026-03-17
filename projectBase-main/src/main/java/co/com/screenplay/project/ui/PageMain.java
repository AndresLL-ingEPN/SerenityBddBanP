package co.com.screenplay.project.ui;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.annotations.DefaultUrl;

/**
 * Page Object para la página de login de Swaglabs.
 * Contiene los targets de los elementos del formulario de autenticación.
 */
@DefaultUrl("page:webdriver.base.url.swaglabs")
public class PageMain extends PageObject {

    public static final Target INPUT_USERNAME =
            Target.the("Campo usuario").locatedBy("#user-name");

    public static final Target INPUT_PASSWORD =
            Target.the("Campo contraseña").locatedBy("#password");

    public static final Target BUTTON_LOGIN =
            Target.the("Botón Iniciar sesión").locatedBy("#login-button");

    public static final Target LABEL_PRODUCT =
            Target.the("Etiqueta lista de Productos").locatedBy("//span[contains(text(), 'Products')]");
}

