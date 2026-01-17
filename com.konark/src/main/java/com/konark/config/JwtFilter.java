package com.konark.config;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.konark.util.JwtUtil;

import io.jsonwebtoken.Claims;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal( HttpServletRequest request,
									 HttpServletResponse response,
									 FilterChain filterChain)
		throws ServletException, IOException {

		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);

			try {
				String username = jwtUtil.extractUsername(token);

				UsernamePasswordAuthenticationToken auth =
					new UsernamePasswordAuthenticationToken(
						username, null, Collections.emptyList());

				SecurityContextHolder.getContext().setAuthentication( auth);

			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.getServletPath().equals("/login");
	}

//	@Override
//	protected void doFilterInternal( HttpServletRequest request,
//									 HttpServletResponse response,
//									 FilterChain filterChain)
//		throws ServletException, IOException {
//
//		String header = request.getHeader("Authorization");
//
//		if (header != null && header.startsWith("Bearer ")) {
//			String token = header.substring(7);
//
//			try {
//				Claims claims = jwtUtil.getClaims( token);
//
//				String username = claims.getSubject();
//				String role = claims.get("role", String.class);
//				String user = claims.get("user", String.class);
//
//				UsernamePasswordAuthenticationToken auth =
//					new UsernamePasswordAuthenticationToken(
//						username,
//						null,
//						Collections.singletonList(new SimpleGrantedAuthority( role))
//					);
//
//				auth.setDetails(claims); // 👈 store claims
//
//				SecurityContextHolder.getContext().setAuthentication(auth);
//			} catch (Exception e) {
//				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//				return;
//			}
//		}
//		filterChain.doFilter(request, response);
//	}
}
