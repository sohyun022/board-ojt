package org.ojt.board_ojt.api.board.repository;

import org.ojt.board_ojt.api.board.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
    Like findByPostIdAndMemberId(Long postId, Long memberId);

}
