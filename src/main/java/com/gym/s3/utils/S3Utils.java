package com.gym.s3.utils;

public class S3Utils {

    public static String extractBucketNameFromArn(String bucketArn) {
        String[] parts = bucketArn.split(":");
        if (parts.length >= 6 && parts[2].equals("s3")) {
            return parts[5];
        } else {
            throw new IllegalArgumentException("Invalid S3 bucket ARN");
        }
    }
}
