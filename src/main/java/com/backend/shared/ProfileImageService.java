package com.backend.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.backend.configenum.GlobalEnum;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

	private final S3Client s3;

	@Value("${naver.object-storage.bucket}") private String bucket;
	@Value("${naver.object-storage.endpoint}") private String endpoint;

	private static final Set<String> ALLOWED = Set.of("image/jpeg","image/png","image/webp");

	/**
	 * 공용 업로드 메서드
	 * @param type   USER or GROUP
	 * @param ownerId USER면 userId, GROUP면 groupId
	 * @param oldUrl 기존 이미지 URL(교체 시 삭제), 없으면 null/blank
	 * @return 공개 버킷/프리픽스 기준의 접근 URL
	 */
	public String uploadImage(GlobalEnum.ProfileImageType type, Long ownerId, MultipartFile file, String oldUrl) throws IOException {
		if (file.isEmpty()) throw new IllegalArgumentException("빈 파일입니다.");
		if (!ALLOWED.contains(file.getContentType()))
			throw new IllegalArgumentException("허용 타입: jpeg/png/webp");
		if (file.getSize() > 5 * 1024 * 1024)
			throw new IllegalArgumentException("최대 5MB까지 허용됩니다.");

		String ext = Optional.ofNullable(StringUtils.getFilenameExtension(file.getOriginalFilename()))
			.map(String::toLowerCase).orElse("bin");

		// 타입에 따른 프리픽스 결정
		String prefix = switch (type) {
			case USER  -> "profiles";
			case GROUP -> "groups";
		};

		String key = "%s/%d/%s.%s".formatted(prefix, ownerId, UUID.randomUUID(), ext);

		PutObjectRequest putReq = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(file.getContentType())
			.acl(ObjectCannedACL.PUBLIC_READ)
			.build();

		s3.putObject(putReq, RequestBody.fromBytes(file.getBytes()));

		if (oldUrl != null && !oldUrl.isBlank()) {
			try { deleteByUrl(oldUrl); } catch (Exception ignore) {}
		}

		return "%s/%s/%s".formatted(endpoint, bucket, key);
	}

	public void deleteByUrl(String url) {
		String key = extractKeyFromUrl(url);
		s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
	}

	private String extractKeyFromUrl(String url) {
		URI u = URI.create(url);
		String path = u.getPath(); // /bucket/...
		if (path.startsWith("/")) path = path.substring(1);
		int slash = path.indexOf('/');
		if (slash < 0) throw new IllegalArgumentException("URL 형식 오류");
		String bucketFromUrl = path.substring(0, slash);
		if (!bucketFromUrl.equals(bucket)) throw new IllegalArgumentException("버킷 불일치");
		return path.substring(slash + 1);
	}
}
