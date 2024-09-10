package org.ojt.board_ojt.api.board.repository;

import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.board.domain.View;
import org.ojt.board_ojt.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {
    boolean existsByPostAndMember(Post post, Member member);
}
