package br.com.cotiinformatica.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtBearerSecurity {

	@Value("${jwt.secretkey}")
	String jwtSecretKey;
	
	@Value("${jwt.expiration}")
	String jwtExpiration;
	
	/*
	 * Método para gerar a data de expiração do token
	 */
	public Date getExpiration() {
		Date dataAtual = new Date();
		return new Date(dataAtual.getTime() + Integer.parseInt(jwtExpiration) * 1000);
	}
	
	/*
	 * Método para gerar o token
	 */
	public String getToken(String emailUsuario) {
		
		return Jwts.builder()
                .setSubject(emailUsuario) // nome do usuário
                .setIssuedAt(new Date()) // data de geração
                .setExpiration(getExpiration()) // data de expiração
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey) // chave antifalsificação
                .compact();
	}
}








