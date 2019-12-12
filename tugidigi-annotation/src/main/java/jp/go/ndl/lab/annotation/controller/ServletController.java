package jp.go.ndl.lab.annotation.controller;

import jp.go.ndl.lab.annotation.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app")
@Slf4j
@Profile({Application.MODE_WEB})
public class ServletController {

    @GetMapping(path = "/**")
    public String staticPageRootLevel() {
        return "index";
    }
}
