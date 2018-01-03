package ua.softserve.academy.kv030.authservice.filters;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ua.softserve.academy.kv030.authservice.exceptions.InvalidTokenException;
import ua.softserve.academy.kv030.authservice.utils.JwtUtil;
import ua.softserve.academy.kv030.authservice.values.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        try {
            String token = ((HttpServletRequest) request).getHeader(Constants.TOKEN_HEADER);

            if (token != null) {
                Authentication auth = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);

                ((HttpServletResponse) response).setHeader(Constants.TOKEN_HEADER, jwtUtil.refresh(token));
            }
        } catch (InvalidTokenException e) {
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        filterChain.doFilter(request,response);
    }
}

