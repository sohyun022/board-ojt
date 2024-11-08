package org.ojt.board_ojt.api.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.board.dto.req.CommentReq;
import org.ojt.board_ojt.api.board.dto.req.CreatePostReq;
import org.ojt.board_ojt.api.board.dto.req.PostListReq;
import org.ojt.board_ojt.api.board.dto.req.UpdatePostReq;
import org.ojt.board_ojt.api.board.dto.res.PostDetailRes;
import org.ojt.board_ojt.api.board.dto.res.PostListRes;
import org.ojt.board_ojt.api.board.service.BoardService;
import org.ojt.board_ojt.security.CustomUserDetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

        try {
            PostDetailRes postDetail = boardService.getPostDetail(postId, userDetails);
            return ResponseEntity.ok(postDetail);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(null); // 접근 권한이 없을 때 403 Forbidden 응답
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null); // 게시글이 없을 때 404 Not Found 응답
        }
    }

    @PatchMapping("/post/{postId}/update")
    @Operation(summary = "게시글 수정", description = "게시글 수정")
    public ResponseEntity<?> updatePost(@RequestBody UpdatePostReq updatePostReq, @PathVariable Long postId,  @AuthenticationPrincipal CustomUserDetails userDetails) {

        try{
            Post post = boardService.updatePost(updatePostReq,postId,userDetails);

            return ResponseEntity.status(HttpStatus.OK).body(post);
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @DeleteMapping("/post/{postId}/delete")
    @Operation(summary = "게시글 삭제", description = "게시글 삭제")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            boolean isDeleted = boardService.deletePost(postId, userDetails);
            if (isDeleted) {
                return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("게시글 삭제 권한이 없습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @PostMapping("/post/{postId}/like")
    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요")
    public ResponseEntity<?> likePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            boardService.likePost(postId, userDetails);

            return ResponseEntity.ok("좋아요 성공");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/post/{postId}/comment")
    @Operation(summary = "게시글 댓글 생성", description = "게시글 댓글 생성")
    public ResponseEntity<?> commentPost(@RequestBody CommentReq commentReq, @PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            boardService.createComment(commentReq,postId, userDetails);

            return ResponseEntity.ok("댓글 생성 완료");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}