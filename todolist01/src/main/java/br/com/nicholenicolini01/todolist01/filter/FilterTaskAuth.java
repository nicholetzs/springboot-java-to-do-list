package br.com.nicholenicolini01.todolist01.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.nicholenicolini01.todolist01.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

    var servletPath = request.getServletPath();

    if (servletPath.startsWith("/tasks/")) {

        // Obtenha o cabeçalho de autorização da solicitação HTTP.
        var authorization = request.getHeader("Authorization");

        // Remova a parte "Basic" do cabeçalho e elimine espaços em branco.
        var authEncoded = authorization.substring(("Basic").length()).trim();

        // Decode a string codificada em base64 em bytes.
        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

        // Converta os bytes decodificados em uma string.
        var authString = new String(authDecoded);

        // Divida a string decodificada em duas partes usando ':' como separador.
        String[] credentials = authString.split(":");

        // A primeira parte é o nome de usuário.
        String username = credentials[0];

        // A segunda parte é a senha.
        String password = credentials[1];

        // Valide a existência do usuário procurando o usuário no repositório pelo nome de usuário.
        var user = this.userRepository.findByUsername(username);
        if (user == null) {
            // Se o usuário não for encontrado, envie uma resposta 401 Não Autorizado.
            response.sendError(401, "Usuário não autorizado");
        } else {
            // Se o usuário for encontrado, prossiga para validar a senha do usuário.

            // Use o BCrypt para verificar a senha fornecida em relação à senha armazenada como hash.
            var passwordVerifier = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (passwordVerifier.verified) {
                // Se a senha for verificada, permita que a solicitação prossiga pela cadeia de filtros.
                request.setAttribute("idUser", user.getId());
                filterChain.doFilter(request, response);
            } else {
                // Se a verificação da senha falhar, envie uma resposta 401 Não Autorizado.
                response.sendError(401);
            }
            // Continue o processamento da solicitação.
        }
    } else {
        filterChain.doFilter(request, response);

    }
}
}