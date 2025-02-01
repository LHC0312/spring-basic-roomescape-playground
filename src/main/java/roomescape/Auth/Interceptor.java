package roomescape.Auth;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.member.MemberService;

@Component
public class Interceptor implements HandlerInterceptor {

  private final MemberService memberService;

  public Interceptor(MemberService memberService) {
    this.memberService = memberService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {

    Cookie[] cookies = request.getCookies();

    if (cookies == null) {
      response.setStatus(401);
      return false;
    }

    String acessToken = Arrays.stream(cookies)
        .filter(cookie -> "token".equals(cookie.getName())) // "token" 쿠키 찾기
        .findFirst()
        .orElse(null)
        .getValue();

    LoginMember loginMember = memberService.getMemberByToken(acessToken);

    if (loginMember == null || !loginMember.getRole().equals("ADMIN")) {
      response.setStatus(401);
      return false;
    }

    return true;
  }

}