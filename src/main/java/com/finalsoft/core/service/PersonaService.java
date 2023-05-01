package com.finalsoft.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finalsoft.common.domain.persistence.Persona;
import com.finalsoft.core.repository.PersonaRepository;

@Service
@Transactional(readOnly = true)
public class PersonaService {
	
	@Autowired
	private PersonaRepository personaRepository;

	public List<Persona> getAll(){
		return personaRepository.findAll();
	}

}
