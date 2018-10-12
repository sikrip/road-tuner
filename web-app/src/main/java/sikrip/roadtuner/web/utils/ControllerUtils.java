package sikrip.roadtuner.web.utils;

import org.slf4j.Logger;
import org.springframework.ui.Model;
import sikrip.roadtuner.web.RoadTunerWebApplication;

/**
 * Utils for MVC controllers.
 */
public final class ControllerUtils {

    private ControllerUtils() {
        // util class
    }

    /**
     * Logs the error and redirects to the error page.
     */
    public static String showErrorPage(Logger logger, Model model, String error) {
        logger.error(error);
        model.addAttribute(RoadTunerWebApplication.ERROR_TEXT_KEY, error);
        return "error";
    }
}
