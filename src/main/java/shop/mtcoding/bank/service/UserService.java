package shop.mtcoding.bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 서비스는 DTO를 요청하고, DTO를 반환한다.
    @Transactional // 트랜재션이 메서드 시작할 때, 시작되고, 종료될때 함께 종료
    public JoinResDto 회원가입(JoinReqDto joinReqDto) {
        // 1. 동일 유저네임 존재 검사
        Optional<User> userOP = userRepository.findByUsername(joinReqDto.getUsername());
        if(userOP.isPresent()) {
            // 유저네임 중복되었다는 뜻
            throw new CustomApiException("동일한 username이 존재합니다.");
        }
        // 2. 패스워드 인코딩 + 회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new JoinResDto(userPS);
    }

    @Getter
    @Setter
    public static class JoinResDto {
        private Long id;
        private String username;
        private String fullname;

        public JoinResDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }

    @Getter
    @Setter
    public static class JoinReqDto {
        // 유효성 검사
        private String username;
        private String password;
        private String email;
        private String fullname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
