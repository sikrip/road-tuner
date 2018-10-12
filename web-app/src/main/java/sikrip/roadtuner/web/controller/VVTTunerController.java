package sikrip.roadtuner.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sikrip.roadtuner.logreader.DatalogitLogReader;
import sikrip.roadtuner.model.RunData;
import sikrip.roadtuner.web.model.VvtTuneOptions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sikrip.roadtuner.engine.vvttuner.VVTTuner.tuneVVT;
import static sikrip.roadtuner.web.utils.ControllerUtils.showErrorPage;

@Controller
@RequestMapping("/vvt-tuner")
public class VVTTunerController {

    private final Logger LOGGER = LoggerFactory.getLogger(VVTTunerController.class);

    @Value("${multipart.maxFileSize}")
    private String maxFileSize;

    @RequestMapping
    public String index(Model model) {
        model.addAttribute("nav", "vvt-tuner");
        return "vvt-tuner";
    }

    @RequestMapping("/load-logs")
    public String loadLogsForm(Model model) {
        final VvtTuneOptions vvtTuneOptions = new VvtTuneOptions();
        vvtTuneOptions.setRpmStep(150);
        vvtTuneOptions.setStartRpm(3900);
        vvtTuneOptions.setEndRpm(8500);
        model.addAttribute("vvtTuneOptions", vvtTuneOptions);
        model.addAttribute("maxFileSize", maxFileSize);
        model.addAttribute("nav", "vvt-tuner");

        return "vvt-tuner-form";
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public String run(Model model, @RequestParam("files") List<MultipartFile> files, VvtTuneOptions vvtTuneOptions) {
        try {
            final DatalogitLogReader logReader = new DatalogitLogReader();
            final List<RunData> runDataList = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    runDataList.add(new RunData(
                            true,
                            file.getOriginalFilename(),
                            logReader.readLog(file.getInputStream())
                    ));
                } catch (Exception e) {
                    LOGGER.error("Could not read file", e);
                }
            }

            final Map<Double, RunData> vvtTuneResult = tuneVVT(
                    runDataList,
                    vvtTuneOptions.getStartRpm(),
                    vvtTuneOptions.getEndRpm(),
                    vvtTuneOptions.getRpmStep()
            ).entrySet()
                    .stream()
                    .filter(e -> e.getValue() != null)
                    .sorted(Map.Entry.comparingByKey())
                    // Convert to linked hash map so that the entries remain sorted
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

            model.addAttribute("vvtTuneResult", vvtTuneResult);
            model.addAttribute("nav", "vvt-tuner");
            return "vvt-tuner-results";
        } catch (Exception e) {
            LOGGER.error("Could not run VVT tune advice.", e);
            return showErrorPage(LOGGER, model, "Could not run VVT tune advice.");
        }
    }
}
