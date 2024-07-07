package br.com.erudio.service;

import br.com.erudio.data.vo.v1.security.TokenResponseVO;
import br.com.erudio.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    public TokenResponseVO authenticate(Authentication authentication) {
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        tokenResponseVO.setAccessToken(jwtService.generateToken(authentication.getName(), authentication.getAuthorities()));
        tokenResponseVO.setRefreshToken(jwtService.generateRefreshToken(authentication.getName(), authentication.getAuthorities()));

        return tokenResponseVO;
    }

    public TokenResponseVO refreshToken(String username, String refreshToken) {
        if (refreshToken.contains("Bearer ")) refreshToken = refreshToken.substring("Bearer ".length());
        TokenResponseVO tokenResponseVO = new TokenResponseVO();

        if (!jwtService.validateToken(refreshToken)) {
            return tokenResponseVO;
        }

        var user = userService.loadUserByUsername(username);

        tokenResponseVO.setAccessToken(jwtService.generateToken(user.getUsername(), user.getAuthorities()));
        tokenResponseVO.setRefreshToken(jwtService.generateRefreshToken(user.getUsername(), user.getAuthorities()));

        return tokenResponseVO;
    }

}
