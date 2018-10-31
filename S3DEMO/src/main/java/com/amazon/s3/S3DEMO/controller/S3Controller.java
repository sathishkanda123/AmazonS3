package com.amazon.s3.S3DEMO.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazon.s3.S3DEMO.dto.RequestDTO;
import com.amazon.s3.S3DEMO.dto.ResponseDTO;
import com.amazon.s3.S3DEMO.service.S3Service;

@RestController
@RequestMapping(value="/s3demo/api/v1")
public class S3Controller {
	
	@Autowired
	S3Service s3Service;
	
	@RequestMapping(value="/test")
	public String test() {
		return "am working..";
	}
	
	@RequestMapping(value="/save",method = RequestMethod.POST)
	public ResponseDTO saveFileInBucket(@RequestBody RequestDTO requestDTO) {
		return s3Service.saveFileInBucket(requestDTO);
	}
	
	@RequestMapping(value="/showfile")
	public ResponseDTO showFile(@RequestParam RequestDTO requestDTO) {
		return s3Service.downloadFile(requestDTO);
	}
	
	@RequestMapping(value="/delete",method = RequestMethod.GET)
	public ResponseDTO deleteObj() {
		return	s3Service.deleteFile();
	}
	
	@RequestMapping(value="/copy",method = RequestMethod.GET)
	public ResponseDTO copy() {
		return	s3Service.copyFiles();
	}
	
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public ResponseDTO listFiles() {
		return	s3Service.listFiles();
	}

}


