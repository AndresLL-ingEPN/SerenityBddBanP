package co.com.screenplay.project.util.execution;

import co.com.screenplay.project.exceptions.ExcRuntime;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class CustomCucumberWithSerenityRunner extends Runner {

    private final Class<CucumberWithSerenity> classValue;
    private CucumberWithSerenity cucumberWithSerenity;

    public CustomCucumberWithSerenityRunner(final Class<CucumberWithSerenity> classValue) {
        this.classValue = classValue;
        try {
            cucumberWithSerenity = new CucumberWithSerenity(classValue);
        } catch (InitializationError e) {
            throw new ExcRuntime(e.getMessage(), e);
        }
    }

    @Override
    public Description getDescription() {
        return cucumberWithSerenity.getDescription();
    }

    private void runAnnotatedMethods(final Class<?> annotation) {
        if (!annotation.isAnnotation()) {
            return;
        }
        final Method[] methods = this.classValue.getMethods();
        for (final Method method : methods) {
            final Annotation[] annotations = method.getAnnotations();
            for (final Annotation item : annotations) {
                if (item.annotationType().equals(annotation)) {
                    try {
                        method.invoke(null);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ExcRuntime(e.getMessage(), e);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void run(final RunNotifier notifier) {
        try {
            runAnnotatedMethods(BeforeSuite.class);
            cucumberWithSerenity = new CucumberWithSerenity(classValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        cucumberWithSerenity.run(notifier);
        try {
            runAnnotatedMethods(AfterSuite.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}