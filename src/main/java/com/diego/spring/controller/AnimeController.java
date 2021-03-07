package com.diego.spring.controller;

import java.util.List;

import javax.validation.Valid;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diego.spring.domain.Anime;
import com.diego.spring.request.AnimePostRequestBody;
import com.diego.spring.request.AnimePutRequestBody;
import com.diego.spring.service.AnimeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("anime")
@Log4j2
public class AnimeController {

	//@Autowired
	//private DateUtil dateUitl;
	@Autowired
	private AnimeService animeService;
	
	// Paginado para não devolver a lista toda
	@GetMapping 
	// Swagger SpringDoc
	@Operation(summary = "List all animes paginated.", description = "The default size is 20, use parameter <size> "
			+ "to change the default value.", tags = "anime")
	public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable){
		//log.info(dateUitl.formatLocalDateTimeDatabaseStyle(LocalDateTime.now()));
		//GET passando tamanho máximo da pagina e a pagina: localhost:8080/anime?size=5&page=1&sort=name,desc
		
		return new ResponseEntity<>(animeService.listAll(pageable), HttpStatus.OK); 
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<Anime>> listAll(){
		//log.info(dateUitl.formatLocalDateTimeDatabaseStyle(LocalDateTime.now()));
		
		return new ResponseEntity<>(animeService.listAllNonPageable(), HttpStatus.OK); 
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Anime> findByID(@PathVariable long id){
		//log.info(dateUitl.formatLocalDateTimeDatabaseStyle(LocalDateTime.now()));

		return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
	}
	
	@GetMapping("by-id/{id}")
	public ResponseEntity<Anime> findByIDAuthentication(@PathVariable long id,
			@AuthenticationPrincipal UserDetails userDetails){
		log.info(userDetails);

		return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
	}
	
	@GetMapping("/find")
	public ResponseEntity<List<Anime>> findByName(@RequestParam String name){
		//log.info(dateUitl.formatLocalDateTimeDatabaseStyle(LocalDateTime.now()));

		return ResponseEntity.ok(animeService.findByName(name));
	}
	
	@PostMapping
	//@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animePostRequestBody){
		return new ResponseEntity<>(animeService.save(animePostRequestBody), HttpStatus.CREATED);
	}
	
	@PutMapping
	public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animePutRequestBody){
		//log.info(dateUitl.formatLocalDateTimeDatabaseStyle(LocalDateTime.now()));

		animeService.replace(animePutRequestBody);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/admin/{id}")
	// Swagger SpringDoc
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Success operation."),
			@ApiResponse(responseCode = "400", description = "When Anime does not exist in Database.")
	})
	public ResponseEntity<Void> delete(@PathVariable long id){
		//log.info(dateUitl.formatLocalDateTimeDatabaseStyle(LocalDateTime.now()));
		
		animeService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
