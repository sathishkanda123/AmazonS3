package com.amazon.s3.S3DEMO.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

@Service
public class AmazonClient {

	private AmazonS3 s3client;
	
	
	@Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

	public AmazonS3 getS3client() {
		return s3client;
	}

	public void setS3client(AmazonS3 s3client) {
		this.s3client = s3client;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Value("${amazonProperties.bucketName}")
    private String bucketName;
    
	@Value("${amazonProperties.accessKey}")
    private String accessKey;
    
	@Value("${amazonProperties.secretKey}")
    private String secretKey;
	
	@PostConstruct
    private void initializeAmazon() {
       AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
       this.s3client = AmazonS3ClientBuilder
    		   .standard()
    		   .withCredentials(new AWSStaticCredentialsProvider(credentials))
    		   .withRegion(Regions.AP_SOUTH_1)
    		   .build();
 }

	public S3Object getObject(GetObjectRequest request) {
		return  s3client.getObject(request);
	}

	public void deleteObject() {
		
// NOt worked
		/*DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(keyName)
                .withQuiet(false);
// Verify that the objects were deleted successfully.
DeleteObjectsResult delObjRes = s3client.deleteObjects(multiObjectDeleteRequest);
int successfulDeletes = delObjRes.getDeletedObjects().size();
System.out.println(successfulDeletes + " objects successfully deleted.");*/
	
		//Delete all working
/*		ObjectListing objects = s3client.listObjects(bucketName);
		for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) 
        {
			System.out.println( objectSummary.getKey()+ " objectSummary.getKey()");
			s3client.deleteObject(bucketName, objectSummary.getKey());
        }           
*/
		
		//Delete prticular folder
		ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix("TEMP2"+java.io.File.separator);
		ListObjectsV2Result listing = s3client.listObjectsV2(req);
		for (S3ObjectSummary objectSummary : listing.getObjectSummaries()) 
        {
			System.out.println( objectSummary.getKey()+ " objectSummary.getKey()");
			s3client.deleteObject(bucketName, objectSummary.getKey());
        }       
	
	
	}

	public InitiateMultipartUploadResult uploadPart(InitiateMultipartUploadRequest initRequest) {
		return  s3client.initiateMultipartUpload(initRequest);
	}

	public UploadPartResult uploadPartFile(UploadPartRequest uploadRequest) {
		return s3client.uploadPart(uploadRequest);
	}

	public void completeUpload(CompleteMultipartUploadRequest compRequest) {
		s3client.completeMultipartUpload(compRequest);		
	}
	

	
	
	
}
