package org.ojt.board_ojt.api.member.repository;

import org.ojt.board_ojt.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //Member 의 변수명과 findBy 뒤에 오는 내용이 같아야 함
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);


}
