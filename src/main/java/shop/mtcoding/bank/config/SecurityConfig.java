package shop.mtcoding.bank.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.mtcoding.bank.config.jwt.JwtAuthenticationFilter;
import shop.mtcoding.bank.config.jwt.JwtAuthorizationFilter;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.util.CustomResponseUtil;


@Configuration
public class SecurityConfig {

   private final Logger log =  LoggerFactory.getLogger(this.getClass());

    @Bean   // Ioc 컨테이너에 BCryptPasswordEncoder() 객체가 등록됨.
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그:BCryptPasswordEncoder 빈 등록됨");
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록이 필요함.
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity>{
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
            super.configure(builder);
        }

        public HttpSecurity build() {
            return getBuilder();
        }
    }

    // JWT 서버를 만들 예정!! Session 사용안함.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그: filterChain 빈 등록됨");
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)); // iframe 허용안함.
        http.csrf(AbstractHttpConfigurer::disable);    // enable이면 포스트맨 작동안함.
        http.cors(cors -> cors.configurationSource(configurationSource()));

        // 세션을 아예 생성하지 않음.
        // jSessionId를 서버쪽에서 관리안하겠다는 뜻!!
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 기본적으로 /login 페이지를 제공하는데, HTML 폼을 사용한 로그인 방식
        // 사용자가 로그인 정보를 입력하고 서버에 POST 요청을 보내면, 세션을 생성해서 로그인 상태를 유지한다.
        // react, 앱으로 요청할 예정
        // Spring Security의 기본 로그인 폼 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증: 브라우저나 API 클라이언트가 요청 헤더(Authorization)에 사용자명/비밀번호를 담아서 보내는 방식
        // 서버가 인증을 확인하면 세선없이 매번 요청에서 인증 정보를 확인
        // 기본적으로 브라우저에서 요청하면 팝업 창이 뜨면서 로그인 정보를 입력하라고 요청합니다.
        // HTTP Basic 인증 비활성화
        // 브라우저 팝업을 통한 사용자 인증을 막음
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 필터 적용
        http.with(new CustomSecurityFilterManager(), CustomSecurityFilterManager::build);

        // Exception 가로채기
        // authenticationEntryPoint(): 인증되지 않는 사용자가 보호된 리소스에 접근하려 할 때 실행되는 핸들러를 설정한다.
        // 인증이 필요하지만 제공된 인증 정보가 없거나 유효하지 않는 경우 인증 시작점(Entry Point)으로 호출된다.
        // 보통 이 핸들러는 인증되지 않는 사용자가 인증을 해야 하는 리소스에 접근하려 할 때 401 Unauthorized 응답을 반환하거나
        // 로그인 페이지로 리디렉션하는 방식으로 사용된다.
        http.exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) -> {
            CustomResponseUtil.fail(response, "로그인을 진행해 주세요", HttpStatus.UNAUTHORIZED);
        }));

        // 권한 실패
        http.exceptionHandling(e -> e.accessDeniedHandler((request, response, accessDeniedException) -> {
            CustomResponseUtil.fail(response, "권한이 없습니다.", HttpStatus.FORBIDDEN);
        }));

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/s/**").authenticated()
                .requestMatchers("/api/admin/**").hasRole(""+UserEnum.ADMIN)
                .anyRequest().permitAll()
        );

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        log.debug("디버그: configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");        // GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트 엔드 IP만 허용 권장, app은 cors에 걸리지 않음)
        configuration.setAllowCredentials(true);    // 클라이언트에서 쿠키 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
