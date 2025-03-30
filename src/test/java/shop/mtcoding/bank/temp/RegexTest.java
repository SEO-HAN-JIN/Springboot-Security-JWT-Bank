package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Pattern;

@SpringBootTest
public class RegexTest {

    @Test
    public void 한글만된다_test() throws Exception {
        String value = "";
        boolean result = Pattern.matches("^[가-힣]*$", value);
        System.out.println("테스트: " + result);
    }

    @Test
    public void 한글은안된다_test() throws Exception {
        String value = "한글";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value);
        System.out.println("테스트: " + result);
    }

    @Test
    public void 영어만된다_test() throws Exception {
        String value = "";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        System.out.println("테스트: " + result);
    }

    @Test
    public void 영어는안된다_test() throws Exception {
        String value = "";
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value);
        System.out.println("테스트: " + result);
    }

    @Test
    public void 영어와숫자만된다_test() throws Exception {
        String value = "";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        System.out.println("테스트: " + result);
    }

    @Test
    public void 영어만되고_길이는최소2최대4이다_test() throws Exception {
        String value = "";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);
        System.out.println("테스트: " + result);
    }

    @Test
    public void user_username_test() throws Exception {
        String username = "ssar";
        boolean result = Pattern.matches("^[a-zA-Z]{2,20}$", username);
        System.out.println("테스트: " + result);
    }

    @Test
    public void user_fullname_test() throws Exception {
        String fullname = "쌀";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", fullname);
        System.out.println("테스트: " + result);
    }

    @Test
    public void user_email_test() throws Exception {
        String emali = "ssar@nate.com";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", emali);
        System.out.println("테스트: " + result);
    }
}
