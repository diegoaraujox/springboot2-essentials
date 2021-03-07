package com.diego.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.diego.spring.domain.Anime;
import com.diego.spring.request.AnimePostRequestBody;
import com.diego.spring.request.AnimePutRequestBody;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {
	public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);
	
	public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);
	public abstract Anime toAnime(AnimePutRequestBody animePutRequestBody);
}
