package org.ojt.board_ojt.api.board.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.board.domain.*;
import org.ojt.board_ojt.api.board.dto.req.*;
import org.ojt.board_ojt.api.board.dto.res.PostDetailRes;
import org.ojt.board_ojt.api.board.dto.res.PostListRes;
import org.ojt.board_ojt.api.board.repository.CommentRepository;
import org.ojt.board_ojt.api.board.repository.LikeRepository;
import org.ojt.board_ojt.api.board.repository.PostRepository;
import org.ojt.board_ojt.api.board.repository.ViewRepository;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.member.repository.MemberRepository;
import org.ojt.board_ojt.exception.CustomErrorInfo;
import org.ojt.board_ojt.exception.CustomException;
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
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

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
                .delYn(false)
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
    public Post updatePost(UpdatePostReq updatePostReq, Long postId, CustomUserDetails userDetails){

        // 기존 게시글을 조회
        Post originPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 정보를 찾을 수 없습니다."));


        if (originPost.getAuthor().equals(userDetails.getMember())){

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
        } else {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }
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
        if (!viewRepository.existsByPostIdAndMemberId(postId, member.getMemberId())) {
            // 조회 기록이 없을 경우 새로운 조회 기록 생성
            View view = new View(post.getPostId(),member.getMemberId(), LocalDateTime.now());
            viewRepository.save(view);

            // 조회수 증가
            post.viewIncrement();
            postRepository.save(post);
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

    @Override
    @Transactional
    public void likePost(Long postId, CustomUserDetails userDetails){
        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            Member member = userDetails.getMember();
            if (!post.isDelYn()) {

                boolean isLiked = likeRepository.existsByPostIdAndMemberId(post.getPostId(),member.getMemberId());

                if(!isLiked){

                    Like like = new Like(post.getPostId(), member.getMemberId());
                    likeRepository.save(like);

                    post.likeIncrement();
                    postRepository.save(post);
                } else{
                    throw new IllegalArgumentException("이미 좋아요 한 게시글입니다.");
                }

            } else {
                throw new IllegalArgumentException("이미 삭제 된 게시글입니다.");
            }
        } else{
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, CustomUserDetails userDetails){
        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            Member member = userDetails.getMember();

            if (!post.isDelYn()) {

                boolean isLiked = likeRepository.existsByPostIdAndMemberId(post.getPostId(),member.getMemberId());

                if(isLiked){

                    Like like = likeRepository.findByPostIdAndMemberId(post.getPostId(),member.getMemberId());
                    likeRepository.delete(like);

                    post.likeDecrement();
                    postRepository.save(post);
                } else{
                    throw new IllegalArgumentException("해당 게시글에 대한 좋아요 기록이 없습니다.");
                }

            } else {
                throw new IllegalArgumentException("이미 삭제 된 게시글입니다.");
            }
        } else{
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
    }

    @Override
    @Transactional
    public void createComment(CreateCommentReq commentReq, Long postId, CustomUserDetails userDetails){

        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            Member member = userDetails.getMember();

            if (!post.isDelYn()) {

                if(!commentReq.getContent().trim().isEmpty()){

                    Comment comment = Comment.builder()
                            .writerId(member.getMemberId())
                            .writerProfileImage(member.getProfileImageUrl())
                            .writerJob(member.getJob())
                            .content(commentReq.getContent())
                            .post(post)
                            .build();

                    // 부모 댓글 설정
                    if (commentReq.getParentCommentId() != null) {
                        Comment parentComment = commentRepository.findById(commentReq.getParentCommentId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID"));
                        parentComment.addChildComment(comment);
                        parentComment.incrementChildCount();
                    }

                    post.commentIncrement();
                    commentRepository.save(comment);

                } else {
                    throw new RuntimeException("댓글의 내용이 비어있습니다.");
                }

            } else {
                throw new RuntimeException("게시글이 삭제되어 댓글을 작성할 수 없습니다."); //따로 나누는 게 좋나?
            }

        } else {
            throw new RuntimeException("존재하지 않는 게시글입니다.");
        }

    }

    @Override
    @Transactional
    public Long updateComment(UpdateCommentReq commentReq, Long commentId, CustomUserDetails userDetails){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(CustomErrorInfo.COMMENT_NOT_FOUND));

        commentRepository.updateComment(commentId, commentReq.getContent(), LocalDateTime.now());
        commentRepository.save(comment);

        return commentId;
    }
}
