package io.github.mateusbm.locmaq.controllers;

import io.github.mateusbm.locmaq.services.BoletimMedicaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BoletimMedicaoController {
    @Autowired
    private BoletimMedicaoService boletimMedicaoService;

}
