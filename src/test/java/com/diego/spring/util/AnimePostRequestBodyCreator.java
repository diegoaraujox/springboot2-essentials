package com.diego.spring.util;

import com.diego.spring.request.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {

	public static AnimePostRequestBody createAnimePostRequestBody() {
		return AnimePostRequestBody.builder()
				.name(AnimeCreator.createAnimeToBeSaved().getName())
				.build();
	}
}
