package co.com.screenplay.project.util.execution;

import co.com.screenplay.project.exceptions.ExcInvalidArgument;
import co.com.screenplay.project.util.constants.Paths;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static co.com.screenplay.project.util.constants.Constants.PROP_PARALLELCONTROL;
import static co.com.screenplay.project.util.constants.Messages.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
public class ControlParallel {

    private ControlParallel() {
    }

    private static final String RUNNER_EJECUTANDOSE = "RunnerEjecutandose";
    private static final String RUNNER_1 = "Runner1";
    private static final String RUNNER_2 = "Runner2";
    private static final String RUNNER_3 = "Runner3";
    private static final String RUNNER_4 = "Runner4";
    private static final String ADD = "add";
    private static final String DELETE = "delete";
    private static final String PARALLEL_CONTROL_PROPERTIES = Paths.propertiesPath() + PROP_PARALLELCONTROL;

    public static void setOrRemoveExecution(String addOrDeleteRunner) {
        File propertiesFile = new File(PARALLEL_CONTROL_PROPERTIES);
        List<String> propertiesModified = addOrDeleteExecutioInProperties(propertiesFile, addOrDeleteRunner);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(java.nio.file.Paths.get(propertiesFile.getAbsolutePath()), StandardCharsets.UTF_8);
            for (final String writeline : propertiesModified) {
                writer.write(writeline);
                writer.write("\n");
            }
        } catch (IOException e) {
            log.error(ERROR_WRITING_FILE + e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.error(ERROR_CLOSING_BUFFEREDWRITER + e.getMessage(), e);
                }
            }
        }
    }

    private static List<String> addOrDeleteExecutioInProperties(final File propertiesFile, String addOrDelete) {
        final List<String> fileData = new ArrayList<>();
        BufferedReader buffReader = null;
        try {
            buffReader = Files.newBufferedReader(java.nio.file.Paths.get(propertiesFile.getAbsolutePath()), StandardCharsets.UTF_8);
            String data;
            if (addOrDelete.equals(ADD)) {
                while ((data = buffReader.readLine()) != null) {
                    fileData.add(data);
                }
                fileData.add(RUNNER_EJECUTANDOSE);
            } else {
                int cont = 0;
                while ((data = buffReader.readLine()) != null) {
                    if (data.contains(RUNNER_EJECUTANDOSE)) {
                        cont++;
                    }
                    if (cont != 1) {
                        fileData.add(data);
                    }
                }
            }
        } catch (IOException e) {
            log.error(ERROR_READ_FILE + e.getMessage(), e);
        } finally {
            closeBufferedReader(buffReader);
        }
        return fileData;
    }

    public static boolean validateExecution(String stage) {
        boolean go = false;
        File featureFile = new File(PARALLEL_CONTROL_PROPERTIES);
        BufferedReader buffReader = null;
        try {
            buffReader = Files.newBufferedReader(java.nio.file.Paths.get(featureFile.getAbsolutePath()), StandardCharsets.UTF_8);
            String data;
            int cont = 0;
            while ((data = buffReader.readLine()) != null) {
                if (data.contains(RUNNER_EJECUTANDOSE)) {
                    cont++;
                }
            }
            if (stage.trim().equalsIgnoreCase(RUNNER_1) || stage.trim().equalsIgnoreCase(RUNNER_2)) {
                if (cont == 1) {
                    go = true;
                }
            } else if (stage.trim().equalsIgnoreCase(RUNNER_3)) {
                if (cont == 2) {
                    go = true;
                }
            } else if (stage.trim().equalsIgnoreCase(RUNNER_4)) {
                if (cont == 3) {
                    go = true;
                }
            } else {
                throw new ExcInvalidArgument(String.format(ST_ARGUMENT_NOT_SUPPORTED, stage));
            }
        } catch (IOException e) {
            log.error(ERROR_READ_FILE + e.getMessage(), e);
        } finally {
            closeBufferedReader(buffReader);
        }
        return go;
    }

    private static void closeBufferedReader(BufferedReader buffReader) {
        if (buffReader != null) {
            try {
                buffReader.close();
            } catch (IOException e) {
                log.error(ERROR_CLOSING_BUFFEREDREADER + e.getMessage(), e);
            }
        }
    }

    public static void startRunner(final String runner) {
        await().atMost(30, SECONDS).until(featuresOverwritten(runner));
        ControlParallel.setOrRemoveExecution(ADD);
        log.info("Start " + runner);
    }

    public static void endsRunner(final String runner) {
        ControlParallel.setOrRemoveExecution(DELETE);
        log.info("Ends " + runner);
    }

    public static Callable<Boolean> featuresOverwritten(final String runner) {
        return () -> ControlParallel.validateExecution(runner);
    }
}