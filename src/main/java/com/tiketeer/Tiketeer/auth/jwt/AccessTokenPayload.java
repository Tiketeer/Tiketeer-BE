package com.tiketeer.Tiketeer.auth.jwt;

import java.util.Date;

import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;

public record AccessTokenPayload(String email, RoleEnum roleEnum, Date issuedAt) {

}
