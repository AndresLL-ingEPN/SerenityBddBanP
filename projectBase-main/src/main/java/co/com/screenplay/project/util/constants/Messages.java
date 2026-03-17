package co.com.screenplay.project.util.constants;

public class Messages {

    private Messages() {
    }

    /**
     * MESSAGES
     */
    public static final String ERROR = "=====> ERROR: ";
    public static final String ERROR_READ_PROP = "=====> Could not reading property '%s', in file %s";
    public static final String ERROR_CREATING_FILE = "=====> Could not creating file ";
    public static final String ERROR_READ_FILE = "=====> Could not reading file ";
    public static final String ERROR_WRITING_FILE= "=====> Could not writing to file ";
    public static final String ERROR_DELETING_FILE= "=====> Could not deleting file ";
    public static final String ERROR_COPYING_FILE= "=====> Could not copying file ";
    public static final String ERROR_FILE_NOT_FOUND= "=====> File not found ";
    public static final String ERROR_CELL_TYPE_NOT_RECOGNIZED = "=====> Cell type (excel) NOT recognized";
    public static final String ERROR_EXCEL_TAB_NOT_DECLARED = "=====> Excel tab name was not declared: ";
    public static final String ERROR_EXCEL_PATH_NOT_DECLARED = "=====> The path of the excel file was not declared: ";
    public static final String ERROR_CHECK_DATA_PATH = "=====> The result of the manual scenario was not found, check data/path of results.";
    public static final String ERROR_VALIDATING_RESULT_MANUAL_SCENARIO = "=====> Error validating result of Manual scenario - ";
    public static final String ERROR_CLOSING_BUFFEREDREADER = "=====> Could not closing BufferedReader ";
    public static final String ERROR_CLOSING_BUFFEREDWRITER = "=====> Could not closing BufferedWriter ";
    public static final String ERROR_CREATING_LIST_OF_FEATURES_TO_DELETE = "=====> Could not create list of features to delete ";

    public static final String STATUS_NOT_DEFINED_FOR_SCENARIO = "=====> Status not defined for scenario %s in %s";
    public static final String EXECUTION_NOT_SUPPORTED = "=====> Execution type not supported.";

    public static final String DOES_NOT_MEET_CONDITIONS = "=====> Does not meet conditions";
    public static final String FILE_CREATED_SUCCESSFULLY = "=====> File %s created successfully";
    public static final String FEATURES_ASIGNED = "=====> Total Features: %s | asignedFeatures: %s | asignedFeaturesRounded: %s";
    public static final String FEATURES_EXTRA_TO_EXECUTE = "=====> Features extra to execute ";

    /**
     * STATEMENTS
     */
    public static final String ST_MANUAL_SCENARIO_STATE = "MANUAL SCENARIO STATE: ";
    public static final String ST_ARGUMENT_NOT_SUPPORTED = "Argument '%s' not supported in the method";
    public static final String ST_NOT_SEGMENTATION_PROCESS = "Segmentation process not carried out";
    public static final String ST_UNSUPPORTED_EVIDENCE_FORMAT = "Unsupported evidence format";
    public static final String ST_UTILITY_CLASS= "Utility class";

}