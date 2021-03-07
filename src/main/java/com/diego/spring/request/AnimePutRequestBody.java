package com.diego.spring.request;

import lombok.Builder;
import lombok.Data;

//DTO
@Data
@Builder
public class AnimePutRequestBody {
	private Long id;
	private String name;
}
