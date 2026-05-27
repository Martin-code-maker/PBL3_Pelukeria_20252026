package edu.mondragon.webengl.pelukeria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LayoutTestController {
    
    @GetMapping("/test-layout2")
    public String testLayout2() {
        return "hello2";  // Esto cargará hello2.html que usa layout2
    }
    
    @GetMapping("/test-layout")
    public String testLayout() {
        return "hello";  // Esto cargará tu hello.html original
    }
}