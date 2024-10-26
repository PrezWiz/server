package prezwiz.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prezwiz.server.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 쿼리 메서드
    Member findMemberByEmail(String email);
}
