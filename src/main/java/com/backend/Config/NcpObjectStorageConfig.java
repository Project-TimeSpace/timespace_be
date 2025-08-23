package com.backend.Config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class NcpObjectStorageConfig {

	@Value("${naver.object-storage.endpoint}")
	private String endpoint;

	@Value("${naver.object-storage.region}")
	private String region;

	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.endpointOverride(URI.create(endpoint))       // NCP 엔드포인트
			.region(Region.of(region))                    // 서명에만 쓰임
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(accessKey, secretKey)))
			.serviceConfiguration(S3Configuration.builder()
				.pathStyleAccessEnabled(true)          // NCP는 path-style이 안정적
				.build())
			.build();
	}
}

