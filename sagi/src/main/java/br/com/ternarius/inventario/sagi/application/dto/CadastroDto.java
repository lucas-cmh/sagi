package br.com.ternarius.inventario.sagi.application.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import br.com.ternarius.inventario.sagi.domain.entity.Usuario;
import br.com.ternarius.inventario.sagi.domain.enums.StatusUsuario;
import br.com.ternarius.inventario.sagi.domain.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Elvis da Guarda
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CadastroDto {
	
    @Size(min = 5, max = 50, message = "Digite o nome e sobrenome.")
    @NotBlank
    private String nome;

    @NotEmpty(message = "Digite o e-mail")
    @Size(max = 100, message = "Limite de 100 caracteres.")
    @Email(message = "E-mail inválido.")
    private String email;

    @Size(min = 4, max = 6, message = "Deve conter entre 4 a 6 caracteres.")
    private String senha;

    private String confirmacaoSenha;

    public CadastroDto(Usuario usuario) {
    	if (usuario != null) {
    		nome = usuario.getNome();
    		email = usuario.getEmail();
    	}
    }
    
    public Usuario toEntity() {
    	return Usuario.builder()
    				.nome(nome)
    				.email(email)
    				.senha(senha)
    				.status(StatusUsuario.CADASTRO_EM_ABERTO)
    				.tipo(TipoUsuario.USER)
    				.build();
    }
}