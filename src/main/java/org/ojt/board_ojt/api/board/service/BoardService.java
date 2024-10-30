package org.ojt.board_ojt.api.board.service;

import org.ojt.board_ojt.api.board.dto.req.CreatePostReq;
import org.ojt.board_ojt.api.board.domain.Post;
import org.ojt.board_ojt.api.board.dto.req.PostListReq;
import org.ojt.board_ojt.api.board.dto.req.UpdatePostReq;
import org.ojt.board_ojt.api.board.dto.res.PostDetailRes;
import org.ojt.board_ojt.api.board.dto.res.PostListRes;
import org.ojt.board_ojt.security.CustomUserDetails;

import java.util.List;


public interface BoardService {

    //게시글 생성,수정,삭제
    Post createPost(CreatePostReq createPostReq, CustomUserDetails userDetails); //게시글 생성
    Post updatePost(UpdatePostReq updatePostReq, Long postId, CustomUserDetails userDetails); //게시글 수정
    boolean deletePost(Long PostId, CustomUserDetails userDetails);

    //게시글 조회
    List<PostListRes> getPostList(PostListReq postListReq); //게시판 목록 조회
    PostDetailRes getPostDetail(Long PostId, CustomUserDetails userDetails); //게시판 상세 조회

    //게시글 좋아요
    void likePost(Long PostId, CustomUserDetails userDetails);
    void unlikePost(Long PostId, CustomUserDetails userDetails);




}
