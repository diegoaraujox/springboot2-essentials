package com.diego.spring.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.diego.spring.domain.Anime;
import com.diego.spring.exception.BadRequestException;
import com.diego.spring.mapper.AnimeMapper;
import com.diego.spring.repository.AnimeRepository;
import com.diego.spring.request.AnimePostRequestBody;
import com.diego.spring.request.AnimePutRequestBody;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnimeService {
	
	private final AnimeRepository animeRepo;

	public Page<Anime> listAll(Pageable pageable) {
		return animeRepo.findAll(pageable);
	}
	
	public List<Anime> listAllNonPageable() {
		return animeRepo.findAll();
	}
	
	public List<Anime> findByName(String name) {
		return animeRepo.findByName(name);
	}
	
	public Anime findByIdOrThrowBadRequestException(long id) {
		return animeRepo.findById(id)
				.orElseThrow(() -> new BadRequestException("Anime not found"));
	}
	
	@Transactional // ativando rollback
	public Anime save(AnimePostRequestBody animePostRequestBody) {
		return animeRepo.save(AnimeMapper.INSTANCE.toAnime(animePostRequestBody));
	}
	
	public void delete(long id) {
		animeRepo.delete(findByIdOrThrowBadRequestException(id));
	}
	
	public void replace(AnimePutRequestBody animePutRequestBody) {
		//	Verificar se existe antes de fazer a alteração
		Anime animeSavedDB = findByIdOrThrowBadRequestException(animePutRequestBody.getId());
		
		Anime anime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
		anime.setId(animeSavedDB.getId());

		animeRepo.save(anime);
	}
}
