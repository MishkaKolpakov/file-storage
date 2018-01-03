package ua.softserve.academy.kv030.authservice.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by Miha on 05.12.2017.
 */
@Controller
public class UIController {

    // inject via application.properties
    @Value("${welcome.message:test}")
    private String message = "Hello World";

    @RequestMapping("/home")
    public String welcome(Map<String, Object> model) {
        model.put("message", this.message);
        return "home";
    }
}
