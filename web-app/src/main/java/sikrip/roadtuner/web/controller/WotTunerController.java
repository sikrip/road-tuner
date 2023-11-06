package sikrip.roadtuner.web.controller;

import static sikrip.roadtuner.web.utils.ControllerUtils.showErrorPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sikrip.roadtuner.engine.wottuner.WotTuner;
import sikrip.roadtuner.logreader.DatalogitLogReader;
import sikrip.roadtuner.logreader.PowerTuneLogReader;
import sikrip.roadtuner.model.InvalidLogFileException;
import sikrip.roadtuner.model.LogEntry;
import sikrip.roadtuner.model.WotTuneResult;
import sikrip.roadtuner.model.WotTunerProperties;

@Controller
@Scope("session")
@RequestMapping("/wot-tuner")
public class WotTunerController {

    private final Logger LOGGER = LoggerFactory.getLogger(WotTunerController.class);
    private WotTunerProperties wotTunerProperties = new WotTunerProperties();
    private WotTuneResult wotTuneResult = null;
    private double[][] currentFuelMap = null;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize = null;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("maxFileSize", maxFileSize);
        model.addAttribute("nav", "wot-tuner");
        model.addAttribute("currentFuelMap", currentFuelMap);
        model.addAttribute("wotTunerProperties", wotTunerProperties);
    }

    @RequestMapping
    public String index() {
        return "wot-tuner";
    }

    @RequestMapping("/reset")
    public String index(Model model) {
        wotTunerProperties = new WotTunerProperties();
        currentFuelMap = null;
        model.addAttribute("currentFuelMap", currentFuelMap);
        model.addAttribute("wotTunerProperties", wotTunerProperties);
        return "wot-tuner";
    }

    @RequestMapping(value = "read-fuel-map", method = RequestMethod.POST)
    public String readFuelMap(Model model, @RequestParam("file") MultipartFile file) {
        try (final BufferedReader fuelMapReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            currentFuelMap = new double[wotTunerProperties.getFuelTableSize()][wotTunerProperties.getFuelTableSize()];
            String line;
            int rowIdx = 0;
            while ((line = fuelMapReader.readLine()) != null) {
                final String[] mapValues = line.split("\t");
                for (int colIdx = 0; colIdx < mapValues.length; colIdx++) {
                    currentFuelMap[rowIdx][colIdx] = Double.parseDouble(mapValues[colIdx]);
                }
                rowIdx++;
            }
        } catch (IOException e) {
            currentFuelMap = null;
            LOGGER.error("Could not load fuel map", e);
        } finally {
            model.addAttribute("currentFuelMap", currentFuelMap);
        }
        return "wot-tuner";
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public String run(Model model, @RequestParam("file") MultipartFile file, WotTunerProperties wotTunerProperties) {
        try {
            this.wotTunerProperties = wotTunerProperties;
            final List<LogEntry> logEntries = readLog(file);
            wotTuneResult = WotTuner.analyze(logEntries, currentFuelMap, wotTunerProperties);
            model.addAttribute("wotTuneResult", wotTuneResult);
            return "wot-tuner";
        } catch (Exception e) {
            LOGGER.error("Could not run WOT tuner.", e);
            return showErrorPage(LOGGER, model, "Could not run WOT tuner.");
        }
    }

    private static List<LogEntry> readLog(MultipartFile file) throws IOException {
        try {
            final DatalogitLogReader logReader = new DatalogitLogReader();
            final List<LogEntry> logEntries = logReader.readLog(file.getInputStream());
            return logEntries;
        } catch (InvalidLogFileException e) {
            return PowerTuneLogReader.readEcuLog(file.getInputStream());
        }
    }
}