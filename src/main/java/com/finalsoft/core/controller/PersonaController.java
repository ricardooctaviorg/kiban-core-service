package com.finalsoft.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finalsoft.common.domain.persistence.Persona;
import com.finalsoft.core.service.PersonaService;

@RestController
@RequestMapping("/")
public class PersonaController {

    @Autowired
    private PersonaService personaService;
    
    @GetMapping(value = "all")
    public List<Persona> getAll()
    {
    	return personaService.getAll();
    } 
    
}
