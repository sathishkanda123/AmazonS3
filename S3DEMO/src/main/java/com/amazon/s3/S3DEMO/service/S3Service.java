package com.amazon.s3.S3DEMO.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazon.s3.S3DEMO.dto.RequestDTO;
import com.amazon.s3.S3DEMO.dto.ResponseDTO;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.codecommit.model.File;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

@Service
public class S3Service {

	@Value("${amazonProperties.bucketName}")
	private String bucketName;

	@Value("${amazonProperties.accessKey}")
	private String accessKey;

	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	@Autowired
	AmazonClient amazonClient;

	
	String LATEST = "Latest";
	
	String TEMP1 = "TEMP1";
	
	String TEMP2 = "TEMP2";

	long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.

	ResponseDTO responseDTO = null;

	public ResponseDTO downloadFile(RequestDTO requestDTO) {
		GetObjectRequest request = new GetObjectRequest(amazonClient.getBucketName(), LATEST);
		S3Object object = amazonClient.getObject(request);
		S3ObjectInputStream objectContent = object.getObjectContent();
		return responseDTO;
	}

	public ResponseDTO deleteFile() {
		amazonClient.deleteObject();
		return responseDTO;
	}

	public ResponseDTO saveFileInBucket(RequestDTO requestDTO) {

		ResponseDTO rdt = new ResponseDTO();
		TransferManager tm = TransferManagerBuilder.standard().withS3Client(amazonClient.getS3client())
				.withMultipartUploadThreshold((long) (5 * 1024 * 1025)).build();
		ObjectMetadata ob = new ObjectMetadata();
		try {
			Upload upload = tm.upload(bucketName, LATEST+java.io.File.separator + requestDTO.getFileName(),
					new ByteArrayInputStream(requestDTO.getFileString().getBytes()), ob);
			upload.waitForCompletion();
			rdt.setStatus("Success");
		} catch (AmazonServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rdt.setStatus("Failure");
		} catch (AmazonClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rdt.setStatus("Failure");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			rdt.setStatus("Failure");
			;
			e.printStackTrace();
		}

		return rdt;

	}

	public ResponseDTO saveFileInBucketkey(RequestDTO requestDTO) {

		long contentLength = requestDTO.getFileString().getBytes().length;

		List<PartETag> partETags = new ArrayList<PartETag>();
		// Initiate the multipart upload.
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(amazonClient.getBucketName(),
				LATEST+java.io.File.separator);
		InitiateMultipartUploadResult initResponse = amazonClient.uploadPart(initRequest);
		// Upload the file parts.
		long filePosition = 0;
		for (int i = 1; filePosition < contentLength; i++) {
			// Because the last part could be less than 5 MB, adjust the part size as
			// needed.
			partSize = Math.min(partSize, (contentLength - filePosition));

			// Create the request to upload a part.
			UploadPartRequest uploadRequest = new UploadPartRequest().withBucketName(amazonClient.getBucketName())
					.withKey(LATEST+java.io.File.separator + requestDTO.getFileName()).withUploadId(initResponse.getUploadId())
					.withPartNumber(i).withFileOffset(filePosition)
					.withInputStream(new ByteArrayInputStream(requestDTO.getFileString().getBytes()))
					.withPartSize(partSize);

			// Upload the part and add the response's ETag to our list.
			UploadPartResult uploadResult = amazonClient.uploadPartFile(uploadRequest);
			partETags.add(uploadResult.getPartETag());

			filePosition += partSize;
		}
		// Complete the multipart upload.
		CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(amazonClient.getBucketName(),
				LATEST+java.io.File.separator + requestDTO.getFileName(), initResponse.getUploadId(), partETags);

		amazonClient.completeUpload(compRequest);

		return responseDTO;
	}

	public ResponseDTO copyFiles() {
		
		ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(TEMP1+java.io.File.separator);
		ListObjectsV2Result listing = amazonClient.getS3client().listObjectsV2(req);
		/*for (String commonPrefix : listing.getCommonPrefixes()) {
			CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, commonPrefix, bucketName , commonPrefix.replaceAll("Latest", "TEMP"));              
			amazonClient.getS3client().copyObject(copyObjRequest);
		}*/
		
			for (S3ObjectSummary summary: listing.getObjectSummaries()) {
				System.out.println(summary.getKey());
				    CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, summary.getKey(), bucketName , summary.getKey().replaceAll(TEMP1, TEMP2));              
					amazonClient.getS3client().copyObject(copyObjRequest);
				}
			return null;
		}
	
	

	public ResponseDTO listFiles() {
		
		ObjectListing objects = amazonClient.getS3client().listObjects(bucketName);
		
		for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) 
        {
			System.out.println("Files inside bucket is"+objectSummary.getKey());
        } 
		
		/*ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix("Latest").withDelimiter("/");
		ListObjectsV2Result listing = amazonClient.getS3client().listObjectsV2(req);*/
		
		
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix("Latest"+"/");
		ObjectListing objectListing = amazonClient.getS3client().listObjects(listObjectsRequest);

		while (true) {
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            	  System.out.println("Files inside prefix"+objectSummary.getKey());
            }
            if (objectListing.isTruncated()) {
                objectListing = amazonClient.getS3client().listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }
		
		/*		for (String commonPrefix : listing.getCommonPrefixes()) {
		        System.out.println("Files inside prefix"+commonPrefix);
		}
		for (S3ObjectSummary summary: listing.getObjectSummaries()) {
		    System.out.println("Files inside prefix-Key"+summary.getKey());
		}
*/		return null;
	}
	

}
