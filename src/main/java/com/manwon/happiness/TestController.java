package com.manwon.happiness;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/error")
    public void error() {
        try {
            Object x = "문자열";
            Integer y = (Integer) x; // ClassCastException
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }
}
