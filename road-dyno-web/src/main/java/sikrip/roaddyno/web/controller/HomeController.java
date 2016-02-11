package sikrip.roaddyno.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sikrip.roaddyno.web.chartmodel.ChartModel;

@Controller
public class HomeController {

	@Autowired
	private ObjectMapper objectMapper;

	@RequestMapping("/")
	public String index(Model model) {
		String chartJson="{}";
		try {
			chartJson = objectMapper.writeValueAsString(new ChartModel());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		model.addAttribute("chartJson", chartJson);
		return "index";
	}

}
