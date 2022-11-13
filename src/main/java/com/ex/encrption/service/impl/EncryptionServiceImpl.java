package com.ex.encrption.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.ex.encrption.model.AES;
import com.ex.encrption.model.Token;
import com.ex.encrption.service.EncryptionService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class EncryptionServiceImpl implements EncryptionService {

	private static final String AES_KEY = "TOKEN_SECURITY_MOGLIX_AES_KEY_IN_JWT";
	private static final String secret_key = "aman@123";

	@Override
	public String encrypt(String data) {
		AES aes = new AES(AES_KEY);
		return aes.encrypt(data);
	}

	@Override
	public String decrypt(String data) {
		AES aes = new AES(AES_KEY);
		return aes.decrypt(data);
	}

	

	@Override
	public String encode(Token token) {
		AES aes = new AES(AES_KEY);
		Claims claims = Jwts.claims();
		claims.put("userId", token.getUserId());
		// we are encypting the userRefer using cipher
		claims.put("userRefer", aes.encrypt(token.getUserRefer()));
		claims.put("expirationTime", token.getExpiryInMinutes());

		return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 60 * 1000))
				.signWith(SignatureAlgorithm.HS512, secret_key.getBytes(StandardCharsets.UTF_8)).compact();
	}

	@Override
	public Token decode(String tokenSource) {
		AES aes = new AES(AES_KEY);
		Claims body = Jwts.parser().setSigningKey(secret_key.getBytes(StandardCharsets.UTF_8))
				.parseClaimsJws(tokenSource).getBody();

		Token token = new Token();
		token.setUserId(Long.valueOf(String.valueOf(body.get("userId"))));
		token.setUserRefer(aes.decrypt(String.valueOf(body.get("userRefer"))));
		token.setExpiryInMinutes(Integer.parseInt(String.valueOf(body.get("expirationTime"))));
		return token;
	}

}
