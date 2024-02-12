package hello.jdbc.connection.repository;

import hello.jdbc.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // insert
        Member member = new Member("memberV3", 10000);
        repository.save(member);

        // select
        Member findMember = repository.findById(member.getMemberId());
        assertThat(findMember).isEqualTo(member);

        // update : money (10000 -> 20000)
        int changeMoney = 20000;
        repository.update(member.getMemberId(), changeMoney);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(changeMoney);

        // delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }
}