package com.tiketeer.Tiketeer.auth.jwt;

import java.util.Date;

public record RefreshTokenPayload(String tokenId, Date issuedAt) {
}
