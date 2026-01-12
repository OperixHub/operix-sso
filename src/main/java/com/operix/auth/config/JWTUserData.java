package com.operix.auth.config;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {
}
 