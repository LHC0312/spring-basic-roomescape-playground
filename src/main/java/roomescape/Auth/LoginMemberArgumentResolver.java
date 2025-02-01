package roomescape.Auth;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.member.MemberService;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
  private MemberService memberService;

  public LoginMemberArgumentResolver(MemberService memberService) {
    this.memberService = memberService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(LoginMember.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) throws Exception {

    HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

    String acessToken = Arrays.stream(servletRequest.getCookies())
        .filter(cookie -> "token".equals(cookie.getName())) // "token" 쿠키 찾기
        .findFirst()
        .orElseThrow(() -> new RuntimeException())
        .getValue();

    return memberService.getMemberByToken(acessToken);
  }
}