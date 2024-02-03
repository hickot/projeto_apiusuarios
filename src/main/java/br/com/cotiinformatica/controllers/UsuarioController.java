package br.com.cotiinformatica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.CriarUsuarioResponseDto;
import br.com.cotiinformatica.services.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/usuario")
public class UsuarioController {

	@Autowired
	UsuarioService usuarioService;
	
	/*
	 * ENDPOINT POST: /api/usuario/criar
	 */
	@PostMapping("criar") 
	public CriarUsuarioResponseDto criarUsuario
		(@RequestBody @Valid CriarUsuarioRequestDto request) {		
		//executando o serviço de criação de usuário e devolvendo o retorno
		return usuarioService.criarUsuario(request);
	}
	
	/*
	 * ENDPOINT POST: /api/usuario/autenticar
	 */
	@PostMapping("autenticar")
	public AutenticarUsuarioResponseDto autenticarUsuario
		(@RequestBody @Valid AutenticarUsuarioRequestDto request) {
		//executando o serviço de autenticação de usuário e devolvendo o retorno
		return usuarioService.autenticarUsuario(request);
	}
}



