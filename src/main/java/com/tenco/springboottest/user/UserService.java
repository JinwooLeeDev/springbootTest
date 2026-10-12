package com.tenco.springboottest.user;

import com.tenco.springboottest._core.error.Exception400;
import com.tenco.springboottest._core.error.Exception404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final UserJpaRepository userJpaRepository;

    public User login(UserRequest.LoginDto loginDto) {
        return userJpaRepository.loginQuery(
                        loginDto.getUsername(), loginDto.getPassword())
                .orElseThrow(() -> new Exception400("사용자명 또는 비밀번호가 올바르지 않습니다."));
    }

    @Transactional
    public void join(UserRequest.JoinDto joinDto) {
        userJpaRepository.checkDuplicationUsername(joinDto.getUsername())
                .ifPresent(user -> {
                    throw new Exception400("이미 존재하는 사용자명입니다");
                });

        userJpaRepository.checkDuplicationEmail(joinDto.getEmail())
                .ifPresent(user -> {
                    throw new Exception400("이미 사용중인 이메일입니다.");
                });

        User user = User.builder()
                .username(joinDto.getUsername())
                .password(joinDto.getPassword())
                .email(joinDto.getEmail())
                .build();
        userJpaRepository.save(user);
    }

    public User userInfo(Long id) {
        log.info("사용자 정보 조회 - ID : {}", id);
        return userJpaRepository.findById(id).orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public User updateUserInfo(Long id, UserRequest.UpdateDto updateDto) {
        User user = userInfo(id);
        user.update(updateDto.getPassword());
        return user;
    }
}
