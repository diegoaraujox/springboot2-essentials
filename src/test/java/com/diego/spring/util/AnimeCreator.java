package com.diego.spring.util;

import com.diego.spring.domain.Anime;

public class AnimeCreator {
	
	public static Anime createAnimeToBeSaved() {
		return Anime.builder()
				.name("Anime 1")
				.build();
	}
	
	
	public static Anime createValidAnime() {
		return Anime.builder()
				.name("Anime 2")
				.id(1L)
				.build();
	}
	
	
	public static Anime createAnimeUpdatedAnime() {
		return Anime.builder()
				.name("Anime 3")
				.id(1L)
				.build();
	}
}
