package org.ojt.board_ojt.api.board.repository;

import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.board.domain.View;
import org.ojt.board_ojt.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {
    boolean existsByPostAndMember(Post post, Member member);

    @Query("SELECT v from View v join  fetch v.post p where v.member.memberId = : memberId")
    List<View> findAllByMember(Long memberId);

}
