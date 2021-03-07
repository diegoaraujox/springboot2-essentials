package com.diego.spring.controller;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.diego.spring.domain.Anime;
import com.diego.spring.request.AnimePostRequestBody;
import com.diego.spring.request.AnimePutRequestBody;
import com.diego.spring.service.AnimeService;
import com.diego.spring.util.AnimeCreator;
import com.diego.spring.util.AnimePostRequestBodyCreator;
import com.diego.spring.util.AnimePutRequestBodyCreator;

@ExtendWith(SpringExtension.class) //teste unitário, junit com spring
class AnimeControllerTest {
	
	@InjectMocks //Testar a classe especificamente
	private AnimeController animeController;
	
	@Mock //Testar todas as classes que estão sendo utilizadas dentro do AnimeController
	private AnimeService animeServiceMock;
	
	@BeforeEach //Faz antes de cada um dos testes
	void setUp() {
		//Definir os comportamentos
		PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
		
		//Quando chamar listAll(), sem importar o argumento Any(), retorne animePage 
		BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
			.thenReturn(animePage);
		
		//Quando chamar listAllNonPageable()
		BDDMockito.when(animeServiceMock.listAllNonPageable())
			.thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		//Quando chamar findByIdOrThrowBadRequestException(), por qualquer valor
		BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
				.thenReturn(AnimeCreator.createValidAnime());
		
		// Quando chamar findByName(), por qualquer tipo de string, retorne a lista
		BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
				.thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		// Quando chamar save(), por qualquer tipo AnimePostRequestBody, retorne um Anime
		BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
				.thenReturn(AnimeCreator.createValidAnime());
		
		// Quando chamar replace(), faz absolutamente nada
		BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));
		
		// Quando chamar delete(), faz absolutamente nada
		BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
	}
	
	@Test
	@DisplayName("return list of animes inside page object when successful")
	void list_ReturnsListOfInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		Page<Anime> animePage = animeController.list(null).getBody();
		
		Assertions.assertThat(animePage).isNotNull(); 
		Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1); 
		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("return list of animes inside page object when successful")
	void listAll_ReturnsListOfAnime_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		List<Anime> listAnimes = animeController.listAll().getBody();
		
		Assertions.assertThat(listAnimes)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);
		
		Assertions.assertThat(listAnimes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByID returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		Long expectedID = AnimeCreator.createValidAnime().getId();
		Anime animeSaved = animeController.findByID(expectedID).getBody();
		
		Assertions.assertThat(animeSaved).isNotNull();

		Assertions.assertThat(animeSaved.getId())
			.isNotNull()
			.isEqualTo(expectedID);
	}
	
	@Test
	@DisplayName("findByName returns list of animes when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		List<Anime> listAnimes = animeController.findByName("anime").getBody();
		
		//verifica ListAnimes: NotNull, NotEmpty, hasSize
		Assertions.assertThat(listAnimes)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);
		
		//verifica nome esperado é igual ao gerado na posição 0
		Assertions.assertThat(listAnimes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByName returns an empty list of animes when anime not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeNotFound() {
		//	Sobrepor @BeforeEach
		BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
			.thenReturn(Collections.emptyList());
		
		List<Anime> listAnimes = animeController.findByName("anime").getBody();
		
		Assertions.assertThat(listAnimes)
			.isNotNull()
			.isEmpty();
	}
	
	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnsAnime_WhenSuccessful() {
		Anime anime = animeController.save(AnimePostRequestBodyCreator
				.createAnimePostRequestBody()).getBody();
		
		Assertions.assertThat(anime)
			.isNotNull()
			.isEqualTo(AnimeCreator.createValidAnime());
		
	}
	
	@Test
	@DisplayName("replace update Anime when successful")
	void replace_UpdateAnime_WhenSuccessful() {
		
		Assertions.assertThatCode(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
			.doesNotThrowAnyException();
		
		ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());
		
		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("delete remove Anime when successful")
	void delete_RemoveAnime_WhenSuccessful() {
		
		Assertions.assertThatCode(() -> animeController.delete(1))
			.doesNotThrowAnyException();
		
		ResponseEntity<Void> entity = animeController.delete(1);
		
		Assertions.assertThat(entity).isNotNull();
		
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
}
