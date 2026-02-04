package com.operix.auth.config.security;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {
}
 