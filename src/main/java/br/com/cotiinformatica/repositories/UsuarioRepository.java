package br.com.cotiinformatica.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.cotiinformatica.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

	/*
	 * Método para consultar 1 usuário através do email
	 */
	@Query("SELECT u FROM Usuario u WHERE u.email = :email")
	Usuario find(@Param("email") String email);
	
	/*
	 * Método para consultar 1 usuário através do email e da senha
	 */
	@Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.senha = :senha")
	Usuario find(@Param("email") String email, @Param("senha") String senha);
}
