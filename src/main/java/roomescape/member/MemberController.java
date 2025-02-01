package roomescape.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import roomescape.Auth.JwtTokenProvider;
import roomescape.Auth.LoginMember;

@RestController
public class MemberController {
    private MemberService memberService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ResponseEntity createMember(@RequestBody MemberRequest memberRequest) {
        MemberResponse member = memberService.createMember(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(member);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody MemberRequest memberRequest, HttpServletResponse response) {
        String email = memberRequest.getEmail();
        String password = memberRequest.getPassword();

        MemberResponse memberResponse = memberService.checkInvalidLogin(email, password);

        String accessToken = jwtTokenProvider.createToken(email);

        Cookie cookie = new Cookie("token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        return ResponseEntity.ok().body(memberResponse);
    }

    @GetMapping("/login/check")
    public ResponseEntity checkLogin(HttpServletRequest request) {


        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return ResponseEntity.badRequest().build();
        }

        String acessToken = Arrays.stream(cookies)
            .filter(cookie -> "token".equals(cookie.getName())) // "token" 쿠키 찾기
            .findFirst()
            .orElse(null)
            .getValue();

        LoginMember loginMember = memberService.getMemberByToken(acessToken);
        MemberResponse memberResponse = new MemberResponse(loginMember.getId(),
            loginMember.getName(), loginMember.getEmail());
        return ResponseEntity.ok().body(memberResponse);
    }

}
