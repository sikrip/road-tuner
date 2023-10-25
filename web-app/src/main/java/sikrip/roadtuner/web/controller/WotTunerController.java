package sikrip.roadtuner.web.controller;

import static sikrip.roadtuner.web.utils.ControllerUtils.showErrorPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sikrip.roadtuner.logreader.DatalogitLogReader;
import sikrip.roadtuner.model.LogEntry;
import sikrip.roadtuner.model.WotTunerProperties;
import sikrip.roadtuner.web.model.VvtTuneOptions;

@Controller
@Scope("session")
@RequestMapping("/wot-tuner")
public class WotTunerController {

    private final Logger LOGGER = LoggerFactory.getLogger(WotTunerController.class);

    private WotTunerProperties wotTunerProperties = new WotTunerProperties();
    private double[][] fuelMap = null;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @RequestMapping
    public String index(Model model) {
        model.addAttribute("nav", "wot-tuner");
        return "wot-tuner";
    }

    @RequestMapping("read-fuel-map")
    public String readFuelMap(Model model, @RequestParam("file") MultipartFile file) {
        try (final BufferedReader fuelMapReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            fuelMap = new double[wotTunerProperties.getFuelTableSize()][wotTunerProperties.getFuelTableSize()];
            String line;
            StringBuilder fuelMapStr = new StringBuilder();
            while ((line = fuelMapReader.readLine()) != null) {
                fuelMapStr.append(line).append("\n");
                final String[] mapValues = line.split("\t");
                for (int i = 0; i < mapValues.length; i++) {
                    fuelMap[i][i] = Double.parseDouble(mapValues[i]);
                }
            }
            model.addAttribute("fuelMapStr", fuelMapStr.toString());
        } catch (IOException e) {
            System.err.println("Could not load fuel map");
        }
        return "wot-tuner";
    }

    @RequestMapping("/load-logs")
    public String loadLogsForm(Model model) {
        model.addAttribute("wotTunerProperties", wotTunerProperties);
        model.addAttribute("maxFileSize", maxFileSize);
        model.addAttribute("nav", "wot-tuner");

        return "wot-tuner-form";
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public String run(Model model, @RequestParam("file") MultipartFile file, VvtTuneOptions vvtTuneOptions) {
        try {
            final DatalogitLogReader logReader = new DatalogitLogReader();
            final List<LogEntry> logEntries = logReader.readLog(file.getInputStream());

            model.addAttribute("wotTuneResult", "ok");
            model.addAttribute("nav", "wot-tuner");
            return "wot-tuner-results";
        } catch (Exception e) {
            LOGGER.error("Could not run WOT tuner.", e);
            return showErrorPage(LOGGER, model, "Could not run WOT tuner.");
        }
    }
}
