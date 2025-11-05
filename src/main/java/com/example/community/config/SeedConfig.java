package com.example.community.config;

import com.example.community.entity.PostEntity;
import com.example.community.entity.UserEntity;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class SeedConfig {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Bean
    ApplicationRunner seedRunner() {
        return args -> seed(); // 부트 기동 후 1회 실행
    }

    @Transactional
    void seed() {
        if (userRepository.count() >= 10 && postRepository.count() >= 10) return;

        IntStream.rangeClosed(1, 10).forEach(i -> {
            UserEntity userEntity = new UserEntity("tester"+i+"@adapterz.kr", "123aS!"+i, "tester"+i, "http://example.com/image.png"+i);
            userRepository.save(userEntity);

            PostEntity postEntity = new PostEntity("title"+i, "content"+i, userEntity);
            postRepository.save(postEntity);
        });
    }
}