package hello.jdbc.service;

import hello.jdbc.connection.repository.MemberRepositoryV2;
import hello.jdbc.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false); // 트랜잭션 시작
            // 비즈니스 로직 수행
            bizLogic(con, fromId, toId, money);
            con.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }

    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void release(Connection con) {
        if(con != null) {
            try {
                con.setAutoCommit(true); // 커넥션 풀을 고려해서 AutoCommit true 처리 (false로 커넥션 풀에 넣으면 다음에 꺼낼때 autocommit이 false로 설정되어서 나오기 때문에)
                con.close();
            } catch (Exception e) {
                log.error("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
