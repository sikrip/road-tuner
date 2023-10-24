package sikrip.roadtuner.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
public class RoadTunerWebApplication implements HandlerExceptionResolver {

	private final Logger LOGGER = LoggerFactory.getLogger(RoadTunerWebApplication.class);

	public static final String ERROR_TEXT_KEY = "errorTxt";

	public static void main(String[] args) {
		SpringApplication.run(RoadTunerWebApplication.class, args);
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
		LOGGER.error("Request: " + httpServletRequest.getRequestURL() + " raised " + e);

		final String errorMessage;
		if (e instanceof NullPointerException) {
			// Null pointer exception
			errorMessage = "Unexpected error occurred.";
		} else if (e instanceof MultipartException) {
			// File upload error
			errorMessage = "Failed to upload file.";
		} else {
			// Other errors
			errorMessage = e.getMessage();
		}

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(ERROR_TEXT_KEY, errorMessage);
		modelAndView.addObject("url", httpServletRequest.getRequestURL());
		modelAndView.setViewName("error");
		return modelAndView;
	}
}
