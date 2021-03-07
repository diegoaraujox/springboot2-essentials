package com.diego.spring.integration;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.diego.spring.domain.Anime;
import com.diego.spring.domain.CustomUser;
import com.diego.spring.repository.AnimeRepository;
import com.diego.spring.repository.CustomUserRepository;
import com.diego.spring.request.AnimePostRequestBody;
import com.diego.spring.util.AnimeCreator;
import com.diego.spring.util.AnimePostRequestBodyCreator;
import com.diego.spring.wrapper.PageableResponse;

//Troca porta para evitar conflito com servidor rodando
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Faz o dropdatabase (mais demorado)
class AnimeControllerIT {
	
	@Autowired
	@Qualifier(value = "testRestTemplateRoleUser")
	private TestRestTemplate testRestTemplateRoleUser;
	
	@Autowired
	@Qualifier(value = "testRestTemplateRoleAdmin")
	private TestRestTemplate testRestTemplateRoleAdmin;
	
	@Autowired
	private AnimeRepository animeRepository;
	
	// Para autenticar
	@Autowired
	private CustomUserRepository customUserRepository;
	
	//	Necessário criar um usuário para authentication
	private static final CustomUser USER = CustomUser.builder()
		.name("joao")
		.password("{bcrypt}$2a$10$f15GanrhWH/NeeTejjHBueS92WwJ/LuOKPFmZ9dvhxQTx4FQJ/uFu")
		.username("joao")
		.authorities("ROLE_USER")
		.build();
	
	//	Necessário criar um usuário para authentication
	private static final CustomUser ADMIN = CustomUser.builder()
		.name("diego")
		.password("{bcrypt}$2a$10$f15GanrhWH/NeeTejjHBueS92WwJ/LuOKPFmZ9dvhxQTx4FQJ/uFu")
		.username("diego")
		.authorities("ROLE_USER,ROLE_ADMIN")
		.build();
	
	@TestConfiguration // Teste com url protegida com spring security
	@Lazy // Espera para inicializar
	static class config {
		
		@Bean(name = "testRestTemplateRoleUser")
		public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
				.rootUri("http://localhost:" + port)
				.basicAuthentication("joao", "123456789");
			
			return new TestRestTemplate(restTemplateBuilder);
		}
		
		@Bean(name = "testRestTemplateRoleAdmin")
		public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
				.rootUri("http://localhost:" + port)
				.basicAuthentication("diego", "123456789");
			
			return new TestRestTemplate(restTemplateBuilder);
		}
	}
	
	@Test
	@DisplayName("return list of animes inside page object when successful")
	void list_ReturnsListOfInsidePageObject_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(USER);
		
		PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/anime", HttpMethod.GET, null,
				new ParameterizedTypeReference<PageableResponse<Anime>>() {
				}).getBody();
		
		Assertions.assertThat(animePage).isNotNull();
	}
	
	@Test
	@DisplayName("return list of animes inside page object when successful")
	void listAll_ReturnsListOfAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(USER);
		
		String expectedName = animeSaved.getName();
		List<Anime> listAnimes = testRestTemplateRoleUser.exchange("/anime/all", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();
		

		Assertions.assertThat(listAnimes).isNotNull();

		Assertions.assertThat(listAnimes).isNotEmpty().hasSize(1);

		Assertions.assertThat(listAnimes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByID returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(USER);
		
		Long expectedID = animeSaved.getId();
		Anime animeFind = testRestTemplateRoleUser.getForObject("/anime/{id}", Anime.class, expectedID);
		
		Assertions.assertThat(animeFind).isNotNull();
		
		Assertions.assertThat(animeFind.getId())
			.isNotNull()
			.isEqualTo(expectedID);
	}
	
	@Test
	@DisplayName("findByName returns list of animes when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(USER);
		
		String expectedName = animeSaved.getName();
		String formattedUrl = String.format("/anime/find?name=%s", expectedName);
		
		List<Anime> listAnimes = testRestTemplateRoleUser.exchange(formattedUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();
		
		Assertions.assertThat(listAnimes)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);
		
		Assertions.assertThat(listAnimes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByName returns an empty list of animes when anime not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeNotFound() {
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(USER);
				
		List<Anime> listAnimes = testRestTemplateRoleUser.exchange("/anime/find?name=abc", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();
		
		Assertions.assertThat(listAnimes)
			.isNotNull()
			.isEmpty();
	}
	
	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnsAnime_WhenSuccessful() {
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(USER);
		
		AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
		ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/anime", animePostRequestBody, Anime.class);
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
		
	}
	
	@Test
	@DisplayName("replace update Anime when successful")
	void replace_UpdateAnime_WhenSuccessful() {
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(USER);
		
		animeSaved.setName("teste");
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/anime", HttpMethod.PUT, 
				new HttpEntity<>(animeSaved), Void.class);
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("delete remove Anime when successful")
	void delete_RemoveAnime_WhenSuccessful() {
		
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(ADMIN);
		
		//identificando a página autenticada através do /admin na URL
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/anime/admin/{id}", HttpMethod.DELETE, 
				null, Void.class, animeSaved.getId());
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("delete returns 403 when user is not admin")
	void delete_Returns403_WhenUserIsNotAdmin() {
		
		Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		// cria um usuário válido para autenticar (spring security) e poder acessar o método
		customUserRepository.save(USER);
		
		//identificando a página autenticada através do /admin na URL
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/anime/admin/{id}", HttpMethod.DELETE, 
				null, Void.class, animeSaved.getId());
		
		Assertions.assertThat(animeResponseEntity).isNotNull();
		
		// Retorna para forbidden
		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
}
