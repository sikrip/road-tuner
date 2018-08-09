package sikrip.roadtuner.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
public class RoadTunerWebApplication implements HandlerExceptionResolver {

	private final Logger LOGGER = LoggerFactory.getLogger(RoadTunerWebApplication.class);

	public static final String ERROR_TEXT_KEY = "errorTxt";

	@Value("${multipart.maxFileSize}")
	private String maxFileSize;

	public static void main(String[] args) {
		final Map<String, Object> appProperties = new HashMap<>();
		appProperties.put("spring.config.name", "road.tuner.application");
		new SpringApplicationBuilder(RoadTunerWebApplication.class).properties(appProperties)
				.build()
				.run(args);
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
			errorMessage = String.format("Failed to upload file, please check that the file is smaller than %s and try again.", maxFileSize);
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
