package com.diego.spring.configurer;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configuração customizada para a paginação do Spring Boot

@Configuration 
public class AnimeWebMvcConfigurer implements WebMvcConfigurer {
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		// TODO Auto-generated method stub
		
		//	Sobrescreve a forma de efetuar a paginação
		//	http://localhost:8080/anime?size=5&page=1&sort=name,desc
		PageableHandlerMethodArgumentResolver pageHandler = new PageableHandlerMethodArgumentResolver();
		pageHandler.setFallbackPageable(PageRequest.of(1, 5));
		resolvers.add(pageHandler);
	}
}
