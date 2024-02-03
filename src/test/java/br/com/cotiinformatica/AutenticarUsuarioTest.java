package br.com.cotiinformatica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.ErrorResponseDto;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutenticarUsuarioTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;

	Faker faker = new Faker();
	
	@Order(1)
	@Test
	public void autenticarUsuarioComSucesso() throws Exception {

		//Cadastrando um usuário
		CriarUsuarioRequestDto criarUsuarioDto = new CriarUsuarioRequestDto();
		criarUsuarioDto.setNome(faker.name().fullName());
		criarUsuarioDto.setEmail(faker.internet().emailAddress());
		criarUsuarioDto.setSenha("@Teste1234");
		
		mockMvc.perform(post("/api/usuario/criar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(criarUsuarioDto)));
		
		//Autenticando o usuário que foi cadastrado
		AutenticarUsuarioRequestDto autenticarUsuarioDto = new AutenticarUsuarioRequestDto();
		autenticarUsuarioDto.setEmail(criarUsuarioDto.getEmail());
		autenticarUsuarioDto.setSenha(criarUsuarioDto.getSenha());
		
		MvcResult result = mockMvc.perform(post("/api/usuario/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(autenticarUsuarioDto)))
				.andExpect(status().isOk())
				.andReturn();
		
		String content = result.getResponse().getContentAsString();
		AutenticarUsuarioResponseDto response = objectMapper.readValue(content, AutenticarUsuarioResponseDto.class);
		
		assertTrue(response.getAccessToken() != null); //verificar se retornou um token
	}

	@Order(2)
	@Test
	public void acessoNegado() throws Exception {

		//Autenticando o usuário que foi cadastrado
		AutenticarUsuarioRequestDto autenticarUsuarioDto = new AutenticarUsuarioRequestDto();
		autenticarUsuarioDto.setEmail(faker.internet().emailAddress());
		autenticarUsuarioDto.setSenha("@Teste4321");
				
		MvcResult result = mockMvc.perform(post("/api/usuario/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(autenticarUsuarioDto)))
				.andExpect(status().isBadRequest())
				.andReturn();
				
		String content = result.getResponse().getContentAsString();
		ErrorResponseDto response = objectMapper.readValue(content, ErrorResponseDto.class);
		
		assertEquals(new String(response.getErrors().get(0).getBytes(StandardCharsets.ISO_8859_1)),
		        "Acesso negado. Usuário inválido");
	}

	@Order(3)
	@Test
	public void validacaoDeCampos() throws Exception {

		AutenticarUsuarioRequestDto dto = new AutenticarUsuarioRequestDto();
		dto.setEmail(null); //campo vazio
		dto.setSenha(null); //campo vazio
		
		MvcResult result = mockMvc.perform(post("/api/usuario/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String content = result.getResponse().getContentAsString();
		ErrorResponseDto response = objectMapper.readValue(content, ErrorResponseDto.class);
		
		assertTrue(response.getErrors().size() >= 2); //pelo menos 2 mensagens de erro
	}

}
