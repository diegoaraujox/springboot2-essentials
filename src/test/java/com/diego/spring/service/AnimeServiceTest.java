package com.diego.spring.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.diego.spring.domain.Anime;
import com.diego.spring.exception.BadRequestException;
import com.diego.spring.repository.AnimeRepository;
import com.diego.spring.util.AnimeCreator;
import com.diego.spring.util.AnimePostRequestBodyCreator;
import com.diego.spring.util.AnimePutRequestBodyCreator;

@ExtendWith(SpringExtension.class) //teste unitário, junit com spring
class AnimeServiceTest {

	@InjectMocks //Testar a classe AnimeService especificamente
	private AnimeService animeService;
	
	@Mock //Testar todas as classes que estão sendo utilizadas dentro do AnimeController
	private AnimeRepository animeRepositoryMock;
	
	@BeforeEach // Fazer antes de cada teste (métodos)
	void setUp() {
		//Definir os comportamentos
		PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
		
		//Quando chamar listAll(), sem importar o argumento Any(), retorne animePage 
		BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
			.thenReturn(animePage);
		
		//Quando chamar listAllNonPageable()
		BDDMockito.when(animeRepositoryMock.findAll())
			.thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		//Quando chamar findByIdOrThrowBadRequestException(), por qualquer valor
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Optional.of(AnimeCreator.createValidAnime()));
		
		// Quando chamar findByName(), por qualquer tipo de string, retorne a lista
		BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
			.thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		// Quando chamar save(), por qualquer tipo AnimePostRequestBody, retorne um Anime
		BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
			.thenReturn(AnimeCreator.createValidAnime());
		
		// Quando chamar delete(), faz absolutamente nada
		BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
	}
	
	@Test
	@DisplayName("return listAll of animes inside page object when successful")
	void listAll_ReturnsListOfInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));
		
		Assertions.assertThat(animePage).isNotNull(); 
		Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1); 
		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("listAllNonPageable list of animes inside page object when successful")
	void listAllNonPageable_ReturnsListOfAnime_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		List<Anime> listAnimes = animeService.listAllNonPageable();
		
		Assertions.assertThat(listAnimes)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);
		
		Assertions.assertThat(listAnimes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByIdOrThrowBadRequestException returns anime when successful")
	void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSuccessful() {
		Long expectedID = AnimeCreator.createValidAnime().getId();
		Anime animeSaved = animeService.findByIdOrThrowBadRequestException(expectedID);
		
		Assertions.assertThat(animeSaved).isNotNull();
		
		Assertions.assertThat(animeSaved.getId())
			.isNotNull()
			.isEqualTo(expectedID);
	}
	
	@Test
	@DisplayName("findByIdOrThrowBadRequestException returns throws BadRequestException when anime not found")
	void findByIdOrThrowBadRequestException_ThrowsBadRequestException_WhenAnimeNotFound() {
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Optional.empty());
		
		// Retornará um BadRequestException, quando executar o findByIdOrThrowBadRequestException()
		Assertions.assertThatExceptionOfType(BadRequestException.class)
			.isThrownBy(() -> animeService.findByIdOrThrowBadRequestException(1));
	}
	
	@Test
	@DisplayName("findByName returns list of animes when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		List<Anime> listAnimes = animeService.findByName("anime");
		
		Assertions.assertThat(listAnimes)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);
		
		Assertions.assertThat(listAnimes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByName returns an empty list of animes when anime not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeNotFound() {
		//Sobrepor @BeforeEach
		BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
			.thenReturn(Collections.emptyList());
		
		List<Anime> listAnimes = animeService.findByName("anime");
		
		Assertions.assertThat(listAnimes)
			.isNotNull()
			.isEmpty();
	}
	
	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnsAnime_WhenSuccessful() {
		Anime anime = animeService.save(AnimePostRequestBodyCreator
				.createAnimePostRequestBody());
		
		Assertions.assertThat(anime)
			.isNotNull()
			.isEqualTo(AnimeCreator.createValidAnime());
		
	}
	
	@Test
	@DisplayName("replace update Anime when successful")
	void replace_UpdateAnime_WhenSuccessful() {
		
		Assertions.assertThatCode(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
			.doesNotThrowAnyException();
		
	}
	
	@Test
	@DisplayName("delete remove Anime when successful")
	void delete_RemoveAnime_WhenSuccessful() {
		
		Assertions.assertThatCode(() -> animeService.delete(1))
			.doesNotThrowAnyException();
		
	}
}
