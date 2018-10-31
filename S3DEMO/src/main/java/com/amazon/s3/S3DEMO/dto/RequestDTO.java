package com.amazon.s3.S3DEMO.dto;

public class RequestDTO {

    public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	private byte[] file;	
	
	private String fileString;	


    public String getFileString() {
		return fileString;
	}

	public void setFileString(String fileString) {
		this.fileString = fileString;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	private String fileName;
}
