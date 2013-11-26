package io.ei.jsontoxls;

public class Messages {
    public static final String NO_TEMPLATE_FOR_GIVEN_TOKEN = "Could not find a valid template for the given token. Token: {0}.";
    public static final String TRANSFORMATION_FAILURE = "XLS Transformation failed. Exception Message: {0}. Stack trace: {1}.";
    public static final String EMPTY_TEMPLATE = "Template cannot be empty.";
    public static final String EMPTY_JSON_DATA = "JSON data cannot be empty.";
    public static final String UNABLE_TO_SAVE_TEMPLATE = "Unable to save template due to internal error.";
    public static final String UNABLE_TO_GENERATE_EXCEL_ERROR = "Unable to generate excel from template and JSON due to internal error.";
    public static final String MALFORMED_JSON = "JSON is not valid.";
}
