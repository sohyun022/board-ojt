package org.ojt.board_ojt.api.board.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.board.domain.SortType;
import org.ojt.board_ojt.api.board.domain.View;
import org.ojt.board_ojt.api.board.dto.req.CreatePostReq;
import org.ojt.board_ojt.api.board.dto.req.PostListReq;
import org.ojt.board_ojt.api.board.dto.req.UpdatePostReq;
import org.ojt.board_ojt.api.board.dto.res.PostDetailRes;
import org.ojt.board_ojt.api.board.dto.res.PostListRes;
import org.ojt.board_ojt.api.board.repository.PostRepository;
import org.ojt.board_ojt.api.board.repository.ViewRepository;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.member.repository.MemberRepository;
import org.ojt.board_ojt.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ViewRepository viewRepository;

    @Override
    @Transactional
    public Post createPost(CreatePostReq createPostReq, CustomUserDetails userDetails){

        if(userDetails==null){
            throw new AccessDeniedException("게시글을 작성하기 위해서는 로그인이 필요합니다.");
        }

        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: "));

        Post post = Post.builder()
                .title(createPostReq.getTitle())
                .content(createPostReq.getContent())
                .author(member)
                .delYn("N")
                .likes(0L)
                .views(0L)
                .comments(0L)
                .postImage(createPostReq.getPostImage())
                .boardType(createPostReq.getBoardType())
                .build();

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(UpdatePostReq updatePostReq, Long postId){

        // 기존 게시글을 조회
        Post originPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 정보를 찾을 수 없습니다."));

        // 수정 사항이 있는 경우에만 업데이트
        if (updatePostReq.getTitle() != null) {
            originPost.toBuilder().title(updatePostReq.getTitle()).build();
        }
        if (updatePostReq.getContent() != null) {
            originPost.toBuilder().content(updatePostReq.getContent()).build();
        }
        if (updatePostReq.getPostImage() != null) {
            originPost.toBuilder().postImage(updatePostReq.getPostImage()).build();
        }
        if (updatePostReq.getBoardType() != null) {
            originPost.toBuilder().boardType(updatePostReq.getBoardType()).build();
        }

        // 변경된 게시글 저장
        return postRepository.save(originPost);
    }


    @Override
    public List<PostListRes> getPostList(PostListReq postListReq){

        SortType sortType = postListReq.getSortType() != null ? postListReq.getSortType() : SortType.createdAt;

        Sort sort;

        switch (sortType){
            case views ->
                    sort = Sort.by("views").descending();
            case comments ->
                    sort = Sort.by("comments").descending();
            default ->
                    sort = Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(postListReq.getPage(), 10, sort); //pageable 공부하기

        // 리포지토리를 통해 필터링된 게시글 목록을 조회
        Page<Post> posts = postRepository.findByFilters(
                postListReq.getAuthor(),
                postListReq.getTitle(),
                postListReq.getStartDt(),
                postListReq.getEndDt(),
                pageable);

        // PostListRes로 변환하여 반환
        return posts.stream()
                .map(post -> PostListRes.builder()
                        .author(post.getAuthor().getMemberId())
                        .createdAt(post.getCreatedAt())
                        .title(post.getTitle())
                        .views(post.getViews())
                        .likes(post.getLikes())
                        .comments(post.getComments())
                        .boardType(post.getBoardType().name())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDetailRes getPostDetail(Long postId, CustomUserDetails userDetails){

        // 로그인된 사용자인지 확인
        if (userDetails==null) {
            throw new AccessDeniedException("해당 게시글에 접근하기 위해서는 로그인이 필요합니다.");
        }

        // 기존 게시글을 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 정보를 찾을 수 없습니다."));

        Member member = userDetails.getMember();

        // 조회 기록이 있는지 확인
        if (!viewRepository.existsByPostAndMember(post, member)) {
            // 조회 기록이 없을 경우 새로운 조회 기록 생성
            View view = new View(post,member, LocalDateTime.now());
            viewRepository.save(view);

            // 조회수 증가
            postRepository.incrementViewCount(postId);
        }

        return PostDetailRes.builder()
                .author(post.getAuthor().getMemberId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .title(post.getTitle())
                .views(post.getViews())
                .likes(post.getLikes())
                .comments(post.getComments())
                .boardType(post.getBoardType())
                .image(post.getPostImage())
                .commentsList(post.getCommentList())
                .content(post.getContent())
                .build();
    }

    @Override
    @Transactional
    public boolean deletePost(Long postId, CustomUserDetails userDetails){
        // 해당 게시글을 찾음
        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            // 게시글 작성자와 로그인된 유저의 ID를 비교
            if (post.getAuthor().equals(userDetails.getMember())) {
                // 게시글이 이미 삭제되지 않았을 때만 삭제 처리
                if (!post.isDelYn()) {
                    post.delete();
                    postRepository.save(post); // 삭제 상태 업데이트
                    return true;
                } else {
                    throw new IllegalArgumentException("이미 삭제된 게시글입니다.");
                }
            } else {
                throw new IllegalArgumentException("해당 게시글을 삭제할 권한이 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }
}
