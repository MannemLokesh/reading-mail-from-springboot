package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	
	@GetMapping("/")
	public ResponseEntity<?> getMessage()
	{
		return new ResponseEntity<>("Getting Data is Working", HttpStatus.OK);
	}

}
