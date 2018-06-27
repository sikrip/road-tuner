package sikrip.roaddyno.web.controller;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sikrip.roaddyno.engine.SimulationException;
import sikrip.roaddyno.model.InvalidLogFileException;
import sikrip.roaddyno.web.RoadDynoWebApplication;
import sikrip.roaddyno.web.chart.ChartDataProvider;
import sikrip.roaddyno.web.model.LoggedRunsEntry;

@Controller
@Scope("session")
public class RoadDynoController {

	private static final int MAX_AUXILIARY_PLOTS = 2;
	private final Logger LOGGER = LoggerFactory.getLogger(RoadDynoController.class);

	@Autowired
	private ObjectMapper objectMapper;

	private final LoggedRunsRepository loggedRunsRepository = new LoggedRunsRepository();

	private final ChartDataProvider chartDataProvider = new ChartDataProvider();

	@Value("${multipart.maxFileSize}")
	private String maxFileSize;

	@RequestMapping("/")
	public String index() {
		if (loggedRunsRepository.isEmpty()) {
			return "index";
		} else {
			return "redirect:/dyno-plots";
		}
	}

	@RequestMapping("/add")
	public String add(Model model) {
		if (loggedRunsRepository.canAddRun()) {
			model.addAttribute("nav", "dyno-plots");
			model.addAttribute("runInfo", new LoggedRunsEntry());
			model.addAttribute("maxFileSize", maxFileSize);
			return "select-log-file-form";
		} else {
			return showErrorPage(model, "Maximum number of plots reached.");
		}
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(LoggedRunsEntry runInfo, @RequestParam("file") MultipartFile file, Model model) {
		if (!file.isEmpty()) {
			try {
				loggedRunsRepository.add(runInfo, file);

				// show update run form
				model.addAttribute("runInfo", runInfo);
				model.addAttribute("nav", "dyno-plots");

				return "update-run-form";
			} catch (InvalidLogFileException e) {
				return showErrorPage(model, "Could not add run. " + e.getMessage());
			}
		} else {
			return showErrorPage(model, "Could not add run, uploaded file is empty.");
		}
	}

	@RequestMapping("edit/{id}")
	public String edit(@PathVariable String id, Model model) {
		LoggedRunsEntry loggedRunsEntry = loggedRunsRepository.get(id);
		if (loggedRunsEntry != null) {
			model.addAttribute("runInfo", loggedRunsEntry);
			model.addAttribute("nav", "dyno-plots");
			return "update-run-form";
		}
		return showErrorPage(model, String.format("Could not edit run with id %s. Run not found.", id));
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(LoggedRunsEntry updatedRun, Model model) {
		try {
			loggedRunsRepository.update(updatedRun);
			return "redirect:/dyno-plots";
		} catch (SimulationException e) {
			return showErrorPage(model, "Could not update run. " + e.getMessage());
		}
	}

	@RequestMapping(value = "/change-status", method = RequestMethod.POST)
	public String changeStatus(String rid, Boolean active, Model model) {
		loggedRunsRepository.activate(rid, active == null ? false : active);
		return "redirect:/dyno-plots";
	}

	@RequestMapping("remove/{id}")
	public String remove(@PathVariable String id) {
		loggedRunsRepository.delete(id);
		return "redirect:/dyno-plots";
	}

	@RequestMapping("/dyno-plots")
	public String dynoPlots(Model model) {
		if (loggedRunsRepository.isEmpty()) {
			return "redirect:/dyno-plots-empty";
		}
		try {
			final List<LoggedRunsEntry> runsToPlot = loggedRunsRepository.getRunsToPlot();
			final Set<String> auxiliaryPlotFields = loggedRunsRepository.getAuxiliaryPlotFields();

			// Main chart
			final String chartDef = objectMapper.writeValueAsString(chartDataProvider.createMainChartDefinition(runsToPlot));
			model.addAttribute("chartDef", chartDef);


			final String[] auxiliaryFieldsArray = auxiliaryPlotFields.toArray(new String[]{});
			for (int i = 0; i < Math.max(auxiliaryFieldsArray.length, MAX_AUXILIARY_PLOTS); i++) {
				final String auxChartDef = objectMapper.writeValueAsString(
					chartDataProvider.createAuxiliaryChartDefinition(runsToPlot, auxiliaryFieldsArray[i])
				);
				model.addAttribute("auxChartDef" + i, auxChartDef);
			}
		} catch (JsonProcessingException e) {
			return showErrorPage(model, "Could not plot runs. " + e.getMessage());
		}
		model.addAttribute("runInfoList", loggedRunsRepository.getRuns());
		model.addAttribute("nav", "dyno-plots");
		return "dyno-plots";
	}

	@RequestMapping("/dyno-plots-empty")
	public String clearAll(Model model) {
		loggedRunsRepository.clear();
		model.addAttribute("nav", "dyno-plots");
		return "dyno-plots-empty";
	}

	@RequestMapping("/help")
	public String help(Model model) {
		model.addAttribute("nav", "help");
		return "help";
	}

	@RequestMapping("/tsdyno")
	public String tsDyno(Model model) {
		model.addAttribute("nav", "tsdyno");
		return "tsdyno";
	}

	@RequestMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("nav", "contact");
		return "contact";
	}

	private String showErrorPage(Model model, String error) {
		LOGGER.error(error);
		model.addAttribute(RoadDynoWebApplication.ERROR_TEXT_KEY, error);
		return "error";
	}

}
