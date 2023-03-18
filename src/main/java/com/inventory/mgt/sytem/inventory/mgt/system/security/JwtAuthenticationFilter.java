package com.inventory.mgt.sytem.inventory.mgt.system.security;

import com.inventory.mgt.sytem.inventory.mgt.system.session.Session;
import com.inventory.mgt.sytem.inventory.mgt.system.session.SessionService;
import com.inventory.mgt.sytem.inventory.mgt.system.utils.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider provider;
    @Autowired
    SessionService sessionService;

    @Autowired
    private CustomUserDetailsService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader(AUTHORIZATION);
//        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
//            // pass to the next filter
//            filterChain.doFilter(request, response);
//          }
//        //if auth header token is present, split and get the second part;
//
//            try {
//                String token = authHeader.split(" ")[1].trim();
//                if (!provider.validateToken(token)) {
//                    filterChain.doFilter(request, response);
//                    return;
//                }
//                // GET THE USER ID FROM THE GENERATED TOKEN
//                Long userId = provider.getIdFromJwt(token);
//                // LOAD THE USER DETAILS ASSOCIATED  WITH THAT TOKEN
//                UserPrincipal userPrincipal = (UserPrincipal) service.loadUserByUserId(userId);
//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(userPrincipal,
//                                null, userPrincipal.getAuthorities());
//                // SET THE USER DETAILS IN THE SECURITY CONTEXT HOLDER
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            filterChain.doFilter(request, response);
//        }
//
//}


        try {
            //every request passes through this filter chain
            String jwt = getJwtToken(request);//gets the jwt string
            System.out.println(jwt);
            if (jwt != null && provider.validateToken(jwt)) {
                // GET THE USER ID FROM THE GENERATED TOKEN
                Long userId = provider.getIdFromJwt(jwt);
                // LOAD THE USER DETAILS ASSOCIATED  WITH THAT TOKEN
                UserPrincipal userPrincipal = (UserPrincipal) service.loadUserByUserId(userId);


                //check if session is available
                Optional<Session> session = sessionService.retrieveExistingSession(userPrincipal.getUsername());
                if(!session.isPresent()) {
                    log.error("Session not found for " + userPrincipal.getUsername());
                    filterChain.doFilter(request, response);
                    return;
                }

                //check if session has expired
                if(sessionService.checkSessionExpiryTime(session.get())) {
                    log.warn("Session has expired!");
                    filterChain.doFilter(request, response);
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userPrincipal,
                                null, userPrincipal.getAuthorities());


                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);


            }

        } catch (Exception ex) {
            //throw ex;
            ex.printStackTrace();
        }

        filterChain.doFilter(request, response);


    }


    private String getJwtToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;

    }}




