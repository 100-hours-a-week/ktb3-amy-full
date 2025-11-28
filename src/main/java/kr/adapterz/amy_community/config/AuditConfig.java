package kr.adapterz.amy_community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 인증 정보 없으면 "system"으로 기록
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system");
            }

            // Principal(email) 반환
            String email = authentication.getName();
            return Optional.ofNullable(email);
        };
    }
}