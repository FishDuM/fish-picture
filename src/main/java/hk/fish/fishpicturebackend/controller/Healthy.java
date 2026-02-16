package hk.fish.fishpicturebackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Healthy {
    @GetMapping("/health")
    public String health(){
        return "ok";
    }
}
