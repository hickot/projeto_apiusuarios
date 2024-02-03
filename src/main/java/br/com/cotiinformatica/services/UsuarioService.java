package br.com.cotiinformatica.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.CriarUsuarioResponseDto;
import br.com.cotiinformatica.entities.Usuario;
import br.com.cotiinformatica.helpers.Sha1CryptoHelper;
import br.com.cotiinformatica.repositories.UsuarioRepository;
import br.com.cotiinformatica.security.JwtBearerSecurity;

@Service
public class UsuarioService {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	JwtBearerSecurity jwtBearerSecurity;
	
	/*
	 * Método para criar a conta do usuário
	 */
	public CriarUsuarioResponseDto criarUsuario(CriarUsuarioRequestDto request) {

		//verificar se já existe um usuário cadastrado com o email informado
		if(usuarioRepository.find(request.getEmail()) != null) {
			throw new IllegalArgumentException("Já existe um usuário cadastrado com o email informado.");
		}
		
		//capturar os dados do usuário
		Usuario usuario = new Usuario();
		usuario.setIdUsuario(UUID.randomUUID());
		usuario.setNome(request.getNome());
		usuario.setEmail(request.getEmail());
		usuario.setSenha(Sha1CryptoHelper.get(request.getSenha()));
		
		//gravar o usuário no banco de dados
		usuarioRepository.save(usuario);
		
		//retornar os dados
		CriarUsuarioResponseDto response = new CriarUsuarioResponseDto();
		response.setIdUsuario(usuario.getIdUsuario());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataHoraCriacao(Instant.now());
		return response;
	}
	
	/*
	 * Método para autenticar o usuário
	 */
	public AutenticarUsuarioResponseDto autenticarUsuario(AutenticarUsuarioRequestDto request) {

		//buscar o usuário no banco de dados através do email e da senha
		Usuario usuario = usuarioRepository
				.find(request.getEmail(), Sha1CryptoHelper.get(request.getSenha()));
		
		//verificar se o usuário foi encontrado
		if(usuario != null) {
			
			//retornando um DTO contendo os dados do usuário autenticado
			AutenticarUsuarioResponseDto response = new AutenticarUsuarioResponseDto();
			response.setIdUsuario(usuario.getIdUsuario());
			response.setNome(usuario.getNome());
			response.setEmail(usuario.getEmail());
			response.setDataHoraAcesso(Instant.now());
			response.setDataHoraExpiracao(jwtBearerSecurity.getExpiration().toInstant()); 
			response.setAccessToken(jwtBearerSecurity.getToken(usuario.getEmail()));
			
			return response;
		}
		
		throw new IllegalArgumentException("Acesso negado. Usuário inválido");
	}
}




