package com.cos.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가있음
// /login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 가 동작함
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    log.info("JwtAuthenticationFilter: 로그인 시동중");

    // 1. username, password 받음
    try {
//      BufferedReader br = request.getReader();
//
//      String input = null;
//      while ((input = br.readLine()) != null){
//        log.info(input);
//      }
      ObjectMapper om = new ObjectMapper();
      User user = om.readValue(request.getInputStream(), User.class);

      log.info(user.toString());
      log.info("============================================================================");

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),
          user.getPassword());

      // PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 authentication이 리턴됨
      // db username/password가 일치
      Authentication authentication = authenticationManager.authenticate(authenticationToken);

      // 로그인 되었다는 뜻
      PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
      log.info(principalDetails.getUser().getUsername());// 로그인 유저 확인
      // authentication 객차게 session영역에 저장을 해야하고 그 방법이 return 해주면됨.
      // 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는거임
      // JWT 생성시 토큰은 필요없지만 권한 처리를 위헤 session에 넣어줌

      // authentication 객체가 session 영역에 저장됨
      return authentication;
    } catch (IOException e) {
      e.printStackTrace();
    }

    // 2. 정상인지 로그인 시도 -> authenticationManager로 로그인시도 -> PrincipalDetailService가 호출 loadUserByUsername() 실행됨
    // 3. PrincipalDetails를 세션에 담고(권한관리 사용시)
    // 4. JWT 토큰을 만들어서 응답해 주면됨
    return null;
  }


  // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨
  // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 하면됨
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {
    log.info("successfulAuthentication 실행됨 == 인증이 완료됨!");

    PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

    String jwtToken = JWT.create()
        .withSubject("cos토큰")
        .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 30)))
        .withClaim("id", principalDetails.getUser().getId())
        .withClaim("username", principalDetails.getUser().getUsername())
        .sign(Algorithm.HMAC512("cos"));

    response.addHeader("Authorization", "Bearer " + jwtToken);
  }
}
