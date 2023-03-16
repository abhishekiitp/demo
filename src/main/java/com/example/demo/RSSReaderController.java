package com.example.demo;

import com.apptasticsoftware.rssreader.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
@Slf4j
public class RSSReaderController {

    private final RSSReaderService service;

    @Autowired
    public RSSReaderController(RSSReaderService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String getData(Model model) {
        log.info("Fetching Data");
        Set<Item> items = service.getData();
        model.addAttribute("items", items);
        return "welcome";
    }

    @GetMapping("/refresh")
    public String refresh(/*Model model*/) {
        log.info("Refreshing Data");
        service.init();
        return "redirect:/";
    }
}
