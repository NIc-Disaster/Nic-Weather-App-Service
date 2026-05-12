package com.example.weathermap.controller;

import com.example.weathermap.service.WeatherQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    private final WeatherQueryService weatherQueryService;

    public ViewController(WeatherQueryService weatherQueryService) {
        this.weatherQueryService = weatherQueryService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("districtOptions", weatherQueryService.getDistrictOptions());
        return "index";
    }
}
