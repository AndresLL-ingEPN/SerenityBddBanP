package co.com.screenplay.project.util.execution;

import co.com.screenplay.project.util.EnvironmentConfig;
import co.com.screenplay.project.util.constants.Paths;
import co.com.screenplay.project.util.overwritedata.FeatureOverwrite;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static co.com.screenplay.project.util.constants.Constants.ENV_SYSTEM_JOBPOSITIONINPHASE;
import static co.com.screenplay.project.util.constants.Constants.ENV_SYSTEM_TOTALJOBSINPHASE;
import static co.com.screenplay.project.util.constants.Messages.*;

@Slf4j
public class ControlExecution {

    static List<String> allFeatures = new ArrayList<>();
    static EnvironmentConfig environmentConfig = new EnvironmentConfig();
    private static boolean parameterizedSegmentation;

    private ControlExecution() {
        throw new IllegalStateException(ST_UTILITY_CLASS);
    }

    public static void featuresSegmentation() {
        String totalAgents = environmentConfig.getVariable(ENV_SYSTEM_TOTALJOBSINPHASE);
        String agentNumber = environmentConfig.getVariable(ENV_SYSTEM_JOBPOSITIONINPHASE);
        log.info("=====> Total agents: '" + totalAgents + "' | Agent number: '" + agentNumber + "'");
        allFeatures = FeatureOverwrite.listFilesByFolder(new File(Paths.featuresPath()));

        if (valitateParalelExcecution(totalAgents)) {
            List<String> pathsFeatureToRemove = getPathsFeatureToRemove(agentNumber);
            if (!parameterizedSegmentation) {
                pathsFeatureToRemove.clear();
                pathsFeatureToRemove = getPathsFeatureToRemoveDefault(totalAgents, agentNumber);
            }
            removeFeatures(pathsFeatureToRemove);
        }
        FeatureOverwrite.clearListFilesByFolder();
    }

    private static boolean valitateParalelExcecution(String totalAgents) {
        totalAgents = totalAgents.isEmpty() ? "1" : totalAgents;
        return Integer.parseInt(totalAgents) > 1;
    }

    private static List<String> getPathsFeatureToRemove(String agenteNum) {
        List<String> featuresToDelete = new ArrayList<>();
        for (String featurePath : allFeatures) {
            String data;

            boolean isPresentAgent = false;
            try (BufferedReader bufferedReader = Files.newBufferedReader(java.nio.file.Paths.get(featurePath), StandardCharsets.UTF_8)) {
                while ((data = bufferedReader.readLine()) != null) {
                    if (data.toLowerCase().trim().contains(" @agente")) parameterizedSegmentation = true;
                    isPresentAgent = data.toLowerCase().trim().contains(" @agente" + agenteNum);
                    if (isPresentAgent) {
                        break;
                    }
                }
                if (!isPresentAgent) {
                    featuresToDelete.add(featurePath);
                }
            } catch (IOException e) {
                throw new IllegalStateException(ERROR_CREATING_LIST_OF_FEATURES_TO_DELETE + e.getMessage(), e);
            }
        }
        return featuresToDelete;
    }

    private static List<String> getPathsFeatureToRemoveDefault(String totalAgentsExecution, String agentNumberExcecution) {

        List<String> featuresPathToRemove = new ArrayList<>();
        double totalAgents = Double.parseDouble(totalAgentsExecution);
        int agentNumber = Integer.parseInt(agentNumberExcecution);
        double totalFeatures = allFeatures.size();
        double asignedFeatures = totalFeatures / totalAgents;

        double asignedFeaturesRounded = Math.round(asignedFeatures);
        log.info(String.format(FEATURES_ASIGNED, totalFeatures, asignedFeatures, asignedFeaturesRounded));

        //solo si el num de features asignados por agente es mayor o igual a 1 continual el proceso
        if (asignedFeatures >= 1) {
            int numFeatureDesde = (int) ((agentNumber * asignedFeaturesRounded) - asignedFeaturesRounded) + 1;
            int numFeatureHasta = Math.min((int) (agentNumber * asignedFeaturesRounded), allFeatures.size());

            for (int i = 0; i < allFeatures.size(); i++) {
                if (i < numFeatureDesde - 1 || i > numFeatureHasta - 1) {
                    if (agentNumber == totalAgents && numFeatureHasta < allFeatures.size() && i >= numFeatureHasta) {
                        int featuresExtra = allFeatures.size() - numFeatureHasta;
                        log.info(FEATURES_EXTRA_TO_EXECUTE + featuresExtra);
                    } else {
                        featuresPathToRemove.add(allFeatures.get(i));
                    }
                }
            }
        } else {
            throw new IllegalStateException(ST_NOT_SEGMENTATION_PROCESS);
        }
        return featuresPathToRemove;
    }

    private static void removeFeatures(List<String> pathsFeatureToRemove) {
        pathsFeatureToRemove.forEach(feature -> log.info("=====> Feature to delete: " + feature));

        for (String featurePath : pathsFeatureToRemove) {
            try {
                Files.delete(java.nio.file.Paths.get(featurePath));
            } catch (IOException e) {
                throw new IllegalStateException(ERROR_DELETING_FILE + featurePath + " - " + e.getMessage(), e);
            }
        }
    }
}