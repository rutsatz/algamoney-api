package com.example.algamoney.api.resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Com o token JWT, não temos nenhum estado na aplicação. O JWTTokenStore, na
 * verdade não armazena em lugar nenhum (passamos ele pois o Spring Security
 * precisa de um store), ele somente valida. O Access token tem um tempo de
 * expiração curto, então não tem tanto problema. Já o refresh token é o que tem
 * o tempo de vida maior e é mais sensível. Como ele está num cookie http e em
 * produção é usado https, não tem problema usar assim. Então no logout da
 * aplicação, o que vamos fazer, é remover o refresh token do cookie http.
 * 
 * @author raffa
 *
 */
@RestController
@RequestMapping("/tokens")
public class TokenResource {

	/**
	 * Faz o logout, removendo o refresh token.
	 *
	 * @param request
	 * @param response
	 */
	@DeleteMapping("/revoke")
	public void revoke(HttpServletRequest request, HttpServletResponse response) {

		// Remover o refresh token.
		Cookie cookie = new Cookie("refreshToken", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(false); // TODO: Em produção será true.
		cookie.setPath(request.getContextPath() + "/oauth/token");
		cookie.setMaxAge(0);

		response.addCookie(cookie);
		response.setStatus(HttpStatus.NO_CONTENT.value());

	}

}
