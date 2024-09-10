package org.ojt.board_ojt.api.board.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.board.domain.SortType;
import org.ojt.board_ojt.api.board.dto.req.CreatePostReq;
import org.ojt.board_ojt.api.board.dto.req.PostListReq;
import org.ojt.board_ojt.api.board.dto.req.UpdatePostReq;
import org.ojt.board_ojt.api.board.dto.res.PostDetailRes;
import org.ojt.board_ojt.api.board.dto.res.PostListRes;
import org.ojt.board_ojt.api.board.repository.PostRepository;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.member.repository.MemberRepository;
import org.ojt.board_ojt.security.CustomUserDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

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
    public PostDetailRes getPostDetail(Long postId, CustomUserDetails userDetails){

        // 로그인된 사용자인지 확인
        if (userDetails==null) {
            throw new AccessDeniedException("해당 게시글에 접근하기 위해서는 로그인이 필요합니다.");
        }

        // 기존 게시글을 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 정보를 찾을 수 없습니다."));

        // 조회수 증가
        postRepository.incrementViewCount(postId);

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
}
