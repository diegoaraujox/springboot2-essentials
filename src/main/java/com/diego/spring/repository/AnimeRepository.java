package com.diego.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diego.spring.domain.Anime;

public interface AnimeRepository extends JpaRepository<Anime, Long>{
	 List<Anime> findByName(String name);
}
