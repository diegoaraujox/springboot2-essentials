package com.diego.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diego.spring.domain.CustomUser;

public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {
	CustomUser findByUsername(String username);
}
