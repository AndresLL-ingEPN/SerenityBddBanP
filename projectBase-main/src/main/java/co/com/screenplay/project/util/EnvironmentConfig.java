package co.com.screenplay.project.util;


import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

public class EnvironmentConfig {

    private static final EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();

    public String getVariable(String variable) {
        String value = System.getenv(variable);//obtiene desde variables de entorno del sistema operativo.
        if (value == null || value.isEmpty()) {
            value = System.getProperty(variable);//obtiene desde variables del sistema (propiedades específicas de Java que se pasan como argumentos al JVM mediante -DpropName=valor)
            if (value == null || value.isEmpty()) {
                value = environmentVariables.getProperty(variable);//propio de serenity obtiene variables: entorno, sistema y properties serenity
            }
        }
        return value == null ? "" : value;
    }
}