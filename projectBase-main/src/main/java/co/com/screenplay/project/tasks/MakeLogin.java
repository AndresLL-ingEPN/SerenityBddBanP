package co.com.screenplay.project.tasks;

import co.com.screenplay.project.model.demoblaze.ModelCredentials;
import co.com.screenplay.project.ui.PageMain;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.annotations.Step;

/**
 * Task que realiza el proceso de autenticación en Swaglabs.
 * Registra el usuario en el log (sin exponer la contraseña) y en el reporte HTML.
 */
@Slf4j
public class MakeLogin implements Task {

    private final ModelCredentials data;

    public MakeLogin(ModelCredentials data) {
        this.data = data;
    }

    public static MakeLogin withCredentials(ModelCredentials data) {
        return Tasks.instrumented(MakeLogin.class, data);
    }

    @Step("{0} inicia sesión con usuario '#data.user'")
    @Override
    public <T extends Actor> void performAs(T actor) {
        log.info("[MakeLogin] → Iniciando autenticación");
        log.info("[MakeLogin] → Usuario: {}", data.getUser());

        Serenity.recordReportData()
                .withTitle("🔐 Credenciales de autenticación")
                .andContents("Usuario: " + data.getUser() + "\nContraseña: [PROTEGIDA]");

        actor.attemptsTo(
                Task.where("Ingresa credenciales",
                        Enter.theValue(data.getUser()).into(PageMain.INPUT_USERNAME),
                        Enter.theValue(data.getPassword()).into(PageMain.INPUT_PASSWORD)
                ),
                Task.where("Confirma el login",
                        Click.on(PageMain.BUTTON_LOGIN)
                )
        );

        log.info("[MakeLogin] ✔ Login completado para usuario: {}", data.getUser());
    }
}
