package com.diego.spring.util;

import com.diego.spring.request.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {

	public static AnimePutRequestBody createAnimePutRequestBody() {
		return AnimePutRequestBody.builder()
				.id(AnimeCreator.createAnimeUpdatedAnime().getId())
				.name(AnimeCreator.createAnimeUpdatedAnime().getName())
				.build();
	}
}
