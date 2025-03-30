package shop.mtcoding.bank.handler.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.handler.ex.CustomValidationException;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect // 관점
public class CustomValidationAdivce {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping(){}

    // @Before (메서드 실행 전), @After (메서드 실행 후), @Around (joinPoint의 전후제어)
    @Around("postMapping() || putMapping()")
    public Object validationAdivce(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs(); // joinPoint의 매개변수
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult bindingResult = (BindingResult) arg;

                if (bindingResult.hasErrors()) {
                    Map<String, String> errMap = new HashMap<>();

                    for (FieldError fieldError : bindingResult.getFieldErrors()) {
                        errMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    }

                    throw new CustomValidationException("유효성검사 실패", errMap);
                }

            }
        }

        return proceedingJoinPoint.proceed(); // 정상적으로 해당 메서드를 실행해라.
    }
}
/**
 * get, delete, post(body), put(body)
 */
