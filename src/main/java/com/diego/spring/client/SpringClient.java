package com.diego.spring.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.diego.spring.domain.Anime;

import lombok.extern.log4j.Log4j2;

///RestTemplate Examples

@Log4j2
public class SpringClient {
	public static void main(String[] args) {
		
		// Cria um Request GET para buscar um Anime utilizando ID
		ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/anime/4", Anime.class);
		log.info(entity);
		
		Anime object = new RestTemplate().getForObject("http://localhost:8080/anime/4", Anime.class);
		log.info(object);
		
		// Mapeamento automático de dados
		Anime[] objectsAnime = new RestTemplate().getForObject("http://localhost:8080/anime/all", Anime[].class);
		log.info("Array ", Arrays.toString(objectsAnime));
		
		// Conversão utilizando método exchange()
		ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/anime/all", HttpMethod.GET, null, 
				new ParameterizedTypeReference<>() {}); //pegar o tipo e converter para lista
		
		log.info("exchange ", exchange.getBody());
		
		//	POST utlizando exchange() e Headers customizado createJsonHeader()
		Anime animeSamurai = Anime.builder().name("Samurai").build();
		ResponseEntity<Anime> animeExchangeSaved = new RestTemplate().exchange("http://localhost:8080/anime/", 
				HttpMethod.POST,
				new HttpEntity<>(animeSamurai, createJsonHeader()), 
				Anime.class);
		
		log.info("exchange (POST): {}", animeExchangeSaved);
		
		//	PUT para atualizar o objeto salvo anteriormente
		Anime animeToBeUpdated = animeExchangeSaved.getBody();
		animeToBeUpdated.setName("Samurai 2");
		
		ResponseEntity<Void> animeExchangeUpdated = new RestTemplate().exchange("http://localhost:8080/anime/", 
				HttpMethod.PUT,
				new HttpEntity<>(animeToBeUpdated, createJsonHeader()), 
				Void.class); //Void.class conforme metodo do AnimeController
		
		log.info("exchange (PUT): {}", animeExchangeUpdated);
		
		//	DELETE para deletar o objeto alterado
		ResponseEntity<Void> animeExchangeDeleted = new RestTemplate().exchange("http://localhost:8080/anime/{id}", 
				HttpMethod.DELETE,
				null, 
				Void.class, //Void.class conforme método do AnimeController
				animeToBeUpdated.getId()); 
		log.info("exchange (PUT): {}", animeExchangeDeleted);
	}
	
	// Criar Headers customizados para o Request
	private static HttpHeaders createJsonHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
