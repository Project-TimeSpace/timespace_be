package com.backend.Login.Auth;

import com.backend.ConfigEnum.GlobalEnum.University;
import com.backend.ConfigSecurity.JwtTokenProvider;
import com.backend.ConfigSecurity.RefreshToken.RefreshToken;
import com.backend.ConfigSecurity.RefreshToken.RefreshTokenService;
import com.backend.Login.Dto.LoginResponseDto;
import com.backend.Login.Dto.RegisterRequestDto;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    // 1. 로그인 회원가입 로직
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public void register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getUsername())
                .university(University.fromCode(request.getUnivCode()))
                .maxFriend(50)
                .maxGroup(10)
                .createdAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime())
                .build();

        userRepository.save(user);
    }

    public LoginResponseDto login(String email, String password) {
        // 1) Admin 먼저 조회
        /*
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            if (!passwordEncoder.matches(password, admin.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            String token = jwtTokenProvider.createToken(admin.getId(), "admin");
            return new LoginResponseDto(token, "admin");
        }
        */

        // 2. User 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        //visitLogService.logVisit(user.getId());
        String token = jwtTokenProvider.createToken(user.getId(), "user");
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), "user");

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(3);
        refreshTokenService.saveOrUpdateToken(user, refreshToken, expiryDate);
        return new LoginResponseDto(token, "user");
    }

    public String reissueAccessToken(String refreshTokenValue) {
        // 1) 저장된 RefreshToken 조회
        RefreshToken refreshToken = refreshTokenService.getByToken(refreshTokenValue);

        // 2) 만료 여부 확인
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // 3) User 정보로부터 ID와 역할 가져오기
        User user = refreshToken.getUser();
        Long userId = user.getId();
        String role = "user"; // 필요한 경우 User 엔티티에서 실제 역할을 가져오도록 변경

        // 4) AccessToken 재발급 (ID, 역할 전달)
        return jwtTokenProvider.createToken(userId, role);
    }


    public void logout(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        refreshTokenService.deleteByUser(user);
    }


    // 2. Email 인증하기 로직
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    private final Map<String, String> emailAuthMap = new HashMap<>(); // 이메일-코드 매핑

    // 인증코드 생성- 6자리 숫자 방식
    public String createAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // 이메일 전송
    public void sendAuthEmail(String toEmail) throws MessagingException {
        String code = createAuthCode();
        emailAuthMap.put(toEmail, code);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("[프로젝트] 이메일 인증코드");
        try {
            helper.setFrom(fromEmail, "프로젝트 인증팀"); // ← 이름 추가
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setText("<h3>인증코드: <b>" + code + "</b></h3>", true);

        mailSender.send(message);
    }

    // 인증코드 검증
    public boolean verifyEmailCode(String email, String code) {
        return code.equals(emailAuthMap.get(email));
    }

    // 비밀번호 재설정
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

