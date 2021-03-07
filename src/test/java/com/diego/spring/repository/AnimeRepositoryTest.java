package com.diego.spring.repository;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.diego.spring.domain.Anime;
import com.diego.spring.util.AnimeCreator;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {
	
	@Autowired
	private AnimeRepository animeRepository;
	
	@Test
	@DisplayName("Save Persist Anime When Successful")
	void save_PersistAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeSaved = this.animeRepository.save(animeToBeSaved);
		
		Assertions.assertThat(animeSaved).isNotNull();
		Assertions.assertThat(animeSaved.getId()).isNotNull();
		Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
		
	}
	
	@Test
	@DisplayName("Update Anime When Successful")
	void update_ReplaceAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeSaved = this.animeRepository.save(animeToBeSaved);
		
		animeSaved.setName("Overlord");
		Anime animeUpdated = animeRepository.save(animeSaved);
		
		Assertions.assertThat(animeUpdated).isNotNull();
		Assertions.assertThat(animeUpdated.getId()).isNotNull();
		Assertions.assertThat(animeUpdated.getName()).isEqualTo(animeSaved.getName());
	}
	
	@Test
	@DisplayName("Delete Anime When Successful")
	void delete_RemoveAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeSaved = this.animeRepository.save(animeToBeSaved);
		
		this.animeRepository.delete(animeSaved);
		Optional<Anime> optional = this.animeRepository.findById(animeSaved.getId());
		
		Assertions.assertThat(optional).isEmpty();
	}
	

	@Test
	@DisplayName("Find by Name Return List When Successfu")
	void findByName_ReturnListOfAnime_WhenSuccessfu() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeSaved = this.animeRepository.save(animeToBeSaved);
		
		String name = animeSaved.getName();
		List<Anime> listAnimes = this.animeRepository.findByName(name);
		
		Assertions.assertThat(listAnimes).isNotEmpty();
		Assertions.assertThat(listAnimes).contains(animeSaved);
	}
	
	@Test
	@DisplayName("Find by Name Return Empty List  When no anime is found")
	void findByName_ReturnEmptyListOfAnime_WhenAnimeNotFound() {
		List<Anime> listAnime = this.animeRepository.findByName("nomeSemFundamento");
		
		Assertions.assertThat(listAnime).isEmpty();
	}
	
	@Test
	@DisplayName("Save Throws Constrain Violation Exception when Name is Empty")
	void save_ThrowsConstrainViolationException_WhenNameEmpty() {
		Anime animeToBeSaved = new Anime();
		
		Assertions.assertThatThrownBy(() -> this.animeRepository.save(animeToBeSaved))
			.isInstanceOfAny(ConstraintViolationException.class);
	}
}
