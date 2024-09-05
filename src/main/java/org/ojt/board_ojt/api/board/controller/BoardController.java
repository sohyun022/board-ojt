package org.ojt.board_ojt.api.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.board.dto.req.CreatePostReq;
import org.ojt.board_ojt.api.board.dto.req.PostListReq;
import org.ojt.board_ojt.api.board.dto.req.UpdatePostReq;
import org.ojt.board_ojt.api.board.dto.res.PostListRes;
import org.ojt.board_ojt.api.board.service.BoardService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{member_id}")
    @Operation(summary = "게시글 생성", description = "게시글 생성")
    public ResponseEntity<?> createPost(@RequestBody CreatePostReq createPostReq, @PathVariable Long member_id) {
       Post post = boardService.createPost(createPostReq,member_id);

       String message = post.getAuthor().getName() + "님 게시글 작성 완료!\n"
               + "posted data: " + post;

       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(message);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 수정")
    public ResponseEntity<?> updatePost(@RequestBody UpdatePostReq updatePostReq, @PathVariable Long postId) {

        // 본인인지 확인하는 로직 필요? 아니면 userDetail 에서 자동 확인?
        Post post = boardService.updatePost(updatePostReq,postId);

        String message = post.getAuthor().getName() + "님 게시글 수정 완료!\n"
                + "posted data: " + post;

        return ResponseEntity.ok(message);
    }


}