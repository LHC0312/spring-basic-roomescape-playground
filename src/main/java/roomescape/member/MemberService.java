package roomescape.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import roomescape.utility.JwtTokenProvider;

@Service
public class MemberService {
    private MemberDao memberDao;


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberDao.save(new Member(memberRequest.getName(), memberRequest.getEmail(), memberRequest.getPassword(), "USER"));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public MemberResponse checkInvalidLogin(String email, String password) {
        Member member = memberDao.findByEmailAndPassword(email, password);
        if (member == null) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public MemberResponse getMemberByToken(String acessToken) {
        String email = jwtTokenProvider.getPayload(acessToken);
        Member member = memberDao.findByEmail(email);

        if (member == null) {
            throw new RuntimeException("유효하지 않은 토큰 입니다.");
        }

        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

}
