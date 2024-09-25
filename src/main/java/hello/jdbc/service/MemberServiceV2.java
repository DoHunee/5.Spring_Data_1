package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    /* 
    계좌 이체를 처리하는 메서드로, 트랜잭션을 시작하고 성공적으로 완료되면 커밋하고,
    실패 시 롤백하여 데이터 일관성을 유지함. 송금과 관련된 비즈니스 로직을 실행함.
    */
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); // 트랜잭션 시작
            // 비즈니스 로직
            bizLogic(con, fromId, toId, money);
            con.commit(); // 성공시 커밋
        } catch (Exception e) {
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    /* 
    비즈니스 로직을 수행하는 메서드로, 송금할 회원과 송금을 받을 회원을 조회하고,
    금액을 차감하거나 추가하는 작업을 처리함. 또한 유효성 검사를 통해 예외 상황을 처리함.
    */
    private void bizLogic(Connection con, String fromId, String toId, int money)
            throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);
        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }


    /* 
    송금을 받을 회원의 유효성을 검사하는 메서드로,
    특정 조건(`toMember.getMemberId()`가 "ex"일 때)에서 예외를 발생시켜
    송금 중 예외 상황을 의도적으로 만들 수 있음.
    */
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    /* 
    트랜잭션이 끝난 후 커넥션을 해제하는 메서드로, 
    커넥션을 자동 커밋 모드로 복원하고 커넥션 풀에 반환함.
    트랜잭션 종료 후 리소스를 적절히 정리함.  
    */
    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); // 커넥션 풀 고려
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

}