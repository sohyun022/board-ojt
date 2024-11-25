package org.ojt.board_ojt.api.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.board.dto.req.*;
import org.ojt.board_ojt.api.board.dto.req.CreatePostReq;
import org.ojt.board_ojt.api.board.dto.req.PostListReq;
import org.ojt.board_ojt.api.board.dto.req.UpdatePostReq;
import org.ojt.board_ojt.api.board.dto.res.PostDetailRes;
import org.ojt.board_ojt.api.board.dto.res.PostListRes;
import org.ojt.board_ojt.api.board.service.BoardService;
import org.ojt.board_ojt.security.CustomUserDetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/board/")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/")
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록 조회")
    public ResponseEntity<?> getPostList(@RequestBody PostListReq req) {
        List<PostListRes> postListRes=boardService.getPostList(req);
        return ResponseEntity.ok(postListRes);
    }

    @PostMapping("/post")
    @Operation(summary = "게시글 생성", description = "게시글 생성")
    public ResponseEntity<?> createPost(@RequestBody CreatePostReq createPostReq, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = boardService.createPost(createPostReq, userDetails);
        return ResponseEntity.ok().body("게시글 ID: "+post.getPostId());
    }

    // 게시글 상세 정보를 가져오는 엔드포인트
    @GetMapping("/post/{postId}")
    @Operation(summary = "게시글 상세 정보 조회", description = "게시글 상세 정보 조회")
    public ResponseEntity<?> getPostDetail(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostDetailRes postDetail = boardService.getPostDetail(postId, userDetails);
        return ResponseEntity.ok(postDetail);
    }

    @PatchMapping("/post/{postId}/update")
    @Operation(summary = "게시글 수정", description = "게시글 수정")
    public ResponseEntity<?> updatePost(@RequestBody UpdatePostReq updatePostReq, @PathVariable Long postId,  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = boardService.updatePost(updatePostReq,postId,userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @DeleteMapping("/post/{postId}/delete")
    @Operation(summary = "게시글 삭제", description = "게시글 삭제")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isDeleted = boardService.deletePost(postId, userDetails);
        if (isDeleted) {
            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("게시글 삭제 권한이 없습니다.");
        }
    }

    @PostMapping("/post/{postId}/like")
    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요")
    public ResponseEntity<?> likePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardService.likePost(postId, userDetails);
        return ResponseEntity.ok("좋아요 성공");
    }

    @PostMapping("/post/{postId}/comment")
    @Operation(summary = "게시글 댓글 생성", description = "게시글 댓글 생성")
    public ResponseEntity<?> commentPost(@RequestBody CreateCommentReq createCommentReq, @PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardService.createComment(createCommentReq,postId, userDetails);
        return ResponseEntity.ok("댓글 생성 완료");
    }

    @PostMapping("/comment/{commentId}/reply")
    @Operation(summary = "게시글 댓글 수정", description = "게시글 댓글 수정")
    public Long updatePost(@RequestBody UpdateCommentReq updateCommentReq, @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return boardService.updateComment(updateCommentReq,commentId,userDetails);
    }

}