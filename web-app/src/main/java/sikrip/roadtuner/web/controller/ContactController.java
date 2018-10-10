package sikrip.roadtuner.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ContactController {

    @RequestMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("nav", "contact");
        return "contact";
    }
}
