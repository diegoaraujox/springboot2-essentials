package com.diego.spring.request;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePostRequestBody {
	@NotEmpty(message = "The anime name cannot be empty")
	@Schema(description = "This is the Anime's name", example = "Anime A1") // Swagger SpringDoc
	private String name;
}
