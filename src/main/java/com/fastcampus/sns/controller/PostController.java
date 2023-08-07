package com.fastcampus.sns.controller;

import com.fastcampus.sns.controller.request.PostCommentRequest;
import com.fastcampus.sns.controller.request.PostCreateRequest;
import com.fastcampus.sns.controller.request.PostModifyRequest;
import com.fastcampus.sns.controller.response.CommentResponse;
import com.fastcampus.sns.controller.response.PostResponse;
import com.fastcampus.sns.controller.response.Response;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{post-id}")
    public Response<PostResponse> modify(@PathVariable("post-id") Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(request.getTitle(), request.getBody(), authentication.getName(), postId);
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{post-id}")
    public Response<Void> delete(@PathVariable("post-id") Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> myList(Pageable pageable, Authentication authentication) {
        return Response.success(postService.my(authentication.getName(), pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{post-id}/likes")
    public Response<Void> like(@PathVariable("post-id") Integer postId, Authentication authentication) {
        postService.like(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/{post-id}/likes")
    public Response<Integer> likeCount(@PathVariable("post-id") Integer postId) {
        return Response.success(postService.likeCount(postId));
    }

    @PostMapping("/{post-id}/comments")
    public Response<Void> comment(@PathVariable("post-id") Integer postId, Authentication authentication, @RequestBody PostCommentRequest request) {
        postService.comment(postId, authentication.getName(), request.getComment());
        return Response.success();
    }

    @GetMapping("/{post-id}/comments")
    public Response<Page<CommentResponse>> comment(@PathVariable("post-id") Integer postId, Pageable pageable, Authentication authentication) {
        return Response.success(postService.getComments(postId, pageable).map(CommentResponse::fromComment));
    }
}
