package shop.mtcoding.bank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.service.UserService;

public class CustomResponseUtil {
    private static final Logger log = LoggerFactory.getLogger(CustomResponseUtil.class);

    public static void unAuthentication(HttpServletResponse response, String msg){

        try {
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto(-1, msg, null);
            String responseBody = om.writeValueAsString(responseDto);

            // JSON 형식으로 응답을 보내고 싶다면
            response.setContentType("application/json; charset=utf-8");
            // 401: 인증이 필요한 API에 접근할 때 ( 이게 더 적합)
            // 404: 인증은 완료되었지만 권한이 부족할 때
            response.setStatus(401);
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("서버 파싱 에러" );
        }
    }
}
