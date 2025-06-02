package com.backend.ConfigSecurity;

import com.backend.Domain.User.User;
import com.backend.Domain.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        // 2) 일반 사용자 조회
        User user = userRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + id));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getId().toString())
                .password(user.getPassword())
                .roles("User")                     // 권한 부여
                .build();
    }
}


