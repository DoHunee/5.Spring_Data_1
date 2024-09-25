package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        
        // save
        Member member = new Member("memberV0", 10000);
        repository.save(member);

        // findById
        //  테스트는 2번 실행하면 PK 중복 오류가 발생한다. 
        // 이 경우 delete from member후에 재실행
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);
    }

}