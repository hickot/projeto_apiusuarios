package br.com.cotiinformatica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.ErrorResponseDto;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CriarUsuarioTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	Faker faker = new Faker();
	
	static String emailUsuario;

	@Order(1)
	@Test
	public void criarUsuarioComSucesso() throws Exception {
		
		CriarUsuarioRequestDto dto = new CriarUsuarioRequestDto();
		dto.setNome(faker.name().fullName());
		dto.setEmail(faker.internet().emailAddress());
		dto.setSenha("@Teste1234");
		
		mockMvc.perform(post("/api/usuario/criar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());
		
		emailUsuario = dto.getEmail();
	}

	@Order(2)
	@Test
	public void emailJaCadastrado() throws Exception {

		CriarUsuarioRequestDto dto = new CriarUsuarioRequestDto();
		dto.setNome(faker.name().fullName());
		dto.setEmail(emailUsuario);
		dto.setSenha("@Teste1234");
		
		MvcResult result = mockMvc.perform(post("/api/usuario/criar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String content = result.getResponse().getContentAsString();
		ErrorResponseDto response = objectMapper.readValue(content, ErrorResponseDto.class);
		
		assertEquals(new String(response.getErrors().get(0).getBytes(StandardCharsets.ISO_8859_1)),
		        "Já existe um usuário cadastrado com o email informado.");
	}

	@Order(3)
	@Test
	public void validacaoDeCampos() throws Exception {

		CriarUsuarioRequestDto dto = new CriarUsuarioRequestDto();
		dto.setNome(null); //campo vazio
		dto.setEmail(null); //campo vazio
		dto.setSenha(null); //campo vazio
		
		MvcResult result = mockMvc.perform(post("/api/usuario/criar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		String content = result.getResponse().getContentAsString();
		ErrorResponseDto response = objectMapper.readValue(content, ErrorResponseDto.class);
		
		assertTrue(response.getErrors().size() >= 3); //pelo menos 3 mensagens de erro
	}
}
