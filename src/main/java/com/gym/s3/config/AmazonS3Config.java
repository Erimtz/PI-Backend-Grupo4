package com.gym.s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AmazonS3Config {

    @Value("${aws.access-key-id}")
    private String accessKey;
    @Value("${aws.secret-access-key}")
    private String secretKey;
    @Value("${aws.s3.bucket-arn}")
    private String bucketArn;
    @Value("${aws.s3.region}")
    private String bucketRegion;

    @Bean
    public AmazonS3 s3client(){
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(bucketRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
