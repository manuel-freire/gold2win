package es.ucm.fdi.iw.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestAttribute;

@RestController
@RequestMapping("/csrf")
public class CsrfController {
    
    @GetMapping
    public CsrfToken getCsrfToken(@RequestAttribute("_csrf") CsrfToken csrfToken) {
        return csrfToken;
    }
}