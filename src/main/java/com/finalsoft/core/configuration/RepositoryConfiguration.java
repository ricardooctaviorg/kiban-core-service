package com.finalsoft.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

import com.finalsoft.common.domain.persistence.*;

@Configuration
public class RepositoryConfiguration implements RepositoryRestConfigurer {
	
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(AuditLog.class
				            , Author.class
				            , Book.class
				            , Persona.class
				            , Porcentaje.class
				            , Score.class
				            );
	}

}
