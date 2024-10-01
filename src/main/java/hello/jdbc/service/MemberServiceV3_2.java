package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    // 생성자: 트랜잭션 템플릿과 레포지토리를 주입받음
    public MemberServiceV3_2(PlatformTransactionManager transactionManager,
            MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    // 계좌 이체 처리: 트랜잭션 템플릿을 사용하여 트랜잭션 내에서 계좌 이체를 처리함
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status) -> {
            try {
                // 비즈니스 로직
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    // 비즈니스 로직: fromId 계좌에서 toId 계좌로 금액을 송금하는 로직을 처리함
    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        
        Member fromMember = memberRepository.findById(fromId); // 송금할 회원 조회
        Member toMember = memberRepository.findById(toId); // 송금을 받을 회원 조회
        
        memberRepository.update(fromId, fromMember.getMoney() - money);  // 송금할 회원의 돈을 차감
        validation(toMember); // 유효성 검사를 통해 예외 상황을 확인
        memberRepository.update(toId, toMember.getMoney() + money); // 송금을 받을 회원의 돈을 추가
    }

    // 유효성 검사: 특정 회원 ID가 'ex'일 경우 예외를 발생시킴
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}