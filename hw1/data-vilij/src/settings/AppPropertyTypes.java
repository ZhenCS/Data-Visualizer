package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    CURRENT_PATH,
    DATA_RESOURCE_PATH,
    GUI_ICONS_RESOURCE_PATH,
    CHART_CSS_PATH,
    /* user interface icon file names */
    SCREENSHOT_ICON,
    RUN_ICON,
    STOP_ICON,
    CONFIG_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,
    RUN_TOOLTIP,
    STOP_TOOLTIP,
    CONFIG_TOOLTIP,

    /* warning messages */
    EXIT_WHILE_RUNNING_WARNING,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    SCREENSHOT_ERROR,
    TOO_MANY_LINES_MSG,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
    LEFT_PANE_TITLE,
    LEFT_PANE_TITLEFONT,
    LEFT_PANE_TITLESIZE,
    CHART_TITLE,
    DONE_BUTTON,
    EDIT_BUTTON,
    AVERAGE_LINE,
    CLASSIFIER_LINE,
    CONFIG_LABEL,
    MAX_ITERATIONS_LABEL,
    UPDATE_INTERVAL_LABEL,
    CLUSTER_LABEL,
    CONFIG_TITLE
}
