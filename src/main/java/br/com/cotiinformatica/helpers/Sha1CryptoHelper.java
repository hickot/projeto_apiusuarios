package br.com.cotiinformatica.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1CryptoHelper {

	public static String get(String value) {

		try {

			// Obtém uma instância de MessageDigest com o algoritmo SHA-1
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

			// Converte a string para bytes e atualiza o MessageDigest
			messageDigest.update(value.getBytes());

			// Calcula o hash SHA-1
			byte[] digest = messageDigest.digest();

			// Converte o hash para uma representação hexadecimal
			StringBuilder hexString = new StringBuilder();
			for (byte b : digest) {
				hexString.append(String.format("%02x", b));
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			// Trate a exceção adequadamente ou rethrow, dependendo do seu contexto
			throw new RuntimeException("Erro ao calcular o hash SHA-1", e);
		}
	}
}
