package com.diego.spring.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.diego.spring.exception.BadRequestException;
import com.diego.spring.exception.BadRequestExceptionDetails;
import com.diego.spring.exception.ValidationExceptionDetails;

@ControllerAdvice
public class RestRequestHandler {
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<BadRequestExceptionDetails> handlerBadRequestException(BadRequestException bre) {
		return new ResponseEntity<>(
				BadRequestExceptionDetails.builder()
					.timestamp(LocalDateTime.now())
					.status(HttpStatus.BAD_REQUEST.value())
					.title("Bad request exception, check the documentation")
					.details(bre.getMessage())
					.developerMessage(bre.getClass().getName())
					.build(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationExceptionDetails> handlerMethodArgumentNotValidException(
			MethodArgumentNotValidException ex) {
		
		List<FieldError> fieldErros = ex.getBindingResult().getFieldErrors();
		String fields = fieldErros.stream().map(FieldError::getField).collect(Collectors.joining(" "));
		String fieldsMessages = fieldErros.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
		return new ResponseEntity<>(
				ValidationExceptionDetails.builder()
					.timestamp(LocalDateTime.now())
					.status(HttpStatus.BAD_REQUEST.value())
					.title("Bad request exception, invalid fields")
					.details("Check the field errors")
					.developerMessage(ex.getClass().getName())
					.fields(fields)
					.fieldsMessage(fieldsMessages)
					.build(), HttpStatus.BAD_REQUEST);
	}
	
	
	/*@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			org.springframework.http.HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		ExceptionDetails exceptionDetails = ExceptionDetails.builder()
				.timestamp(LocalDateTime.now())
				.status(status.value())
				.title(ex.getCause().getMessage())
				.details(ex.getMessage())
				.developerMessage(ex.getClass().getName())
				.build();
			
		return new ResponseEntity<Object>(exceptionDetails, headers, status);
	}*/
}
