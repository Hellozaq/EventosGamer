package com.duoc.gamer.controller;

import com.duoc.gamer.dto.AuthDTO;
import com.duoc.gamer.security.JwtTokenUtil;
import com.duoc.gamer.service.impl.CustomUserDetailsService;
import com.duoc.gamer.util.JwtCookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // Muestra la vista de login
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("auth", new AuthDTO());
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute("auth") AuthDTO authDTO,
                                HttpServletResponse response,
                                Model model) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())
            );
        } catch (BadCredentialsException e) {
            model.addAttribute("mensajeError", "Correo o contraseña incorrectos");
            return "login";
        }
        // Generar token JWT
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authDTO.getEmail());
        String jwt = jwtTokenUtil.generateToken(userDetails);

        Cookie cookie = JwtCookieUtil.crearCookieJWT(jwt);
        response.addCookie(cookie);

        // Verificar si el usuario tiene rol ADMIN para redirigirlo al template admin-home.html
        boolean isAdminOrModerator = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_MODERATOR"));

        if (isAdminOrModerator) {
            return "redirect:/admin-home";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // La cookie se elimina
        response.addCookie(cookie);
        return "redirect:/login?logout";
    }
}