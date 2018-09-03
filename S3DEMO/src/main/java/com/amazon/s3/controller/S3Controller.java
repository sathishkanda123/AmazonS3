package com.amazon.s3.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="s3demo/api/v1")
public class S3Controller {

	
	@RequestMapping(value="/test")
	public String test() {
		return "am working..";
	}
	
	
}
