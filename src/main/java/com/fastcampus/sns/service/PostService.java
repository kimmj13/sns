package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.AlarmType;
import com.fastcampus.sns.model.Comment;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.model.entity.*;
import com.fastcampus.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final CommentEntityRepository commentEntityRepository;

    public void create(String title, String body, String userName) {

        UserEntity user = getUserOrException(userName);
        postEntityRepository.save(PostEntity.of(title, body, user));

    }

    public Post modify(String title, String body, String userName, Integer postId) {
        PostEntity post = getPostOrException(postId);
        UserEntity user = getUserOrException(userName);

        //post permission
        if (post.getUser() != user) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        //post save
        post.setTitle(title);
        post.setBody(body);

        postEntityRepository.saveAndFlush(post);

        return Post.fromEntity(post);
    }

    public void delete(String userName, Integer postId) {

        PostEntity post = getPostOrException(postId);
        UserEntity user = getUserOrException(userName);

        //post permission
        if (post.getUser() != user) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntityRepository.delete(post);

    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity user = getUserOrException(userName);
        return postEntityRepository.findAllByUser(user, pageable).map(Post::fromEntity);
    }

    public void like(Integer postId, String userName) {
        //post exist
        PostEntity post = getPostOrException(postId);
        UserEntity user = getUserOrException(userName);

        //check liked -> throw
        likeEntityRepository.findByUserAndPost(user, post).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %s", userName, postId));
        });

        //like save
        likeEntityRepository.save(LikeEntity.of(user, post));

        //alarm save
        alarmEntityRepository.save(AlarmEntity.of(post.getUser(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(user.getId(), post.getId())));
    }

    public Integer likeCount(Integer postId) {

        //post exist
        PostEntity post = getPostOrException(postId);
//        return likeEntityRepository.findAllByPost(post).size();

        return likeEntityRepository.countByPost(post);
    }

    public void comment(Integer postId, String userName, String comment) {
        PostEntity post = getPostOrException(postId);
        UserEntity user = getUserOrException(userName);

        //comment save
        commentEntityRepository.save(CommentEntity.of(user, post, comment));

        //alarm save
        alarmEntityRepository.save(AlarmEntity.of(post.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(user.getId(), post.getId())));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity post = getPostOrException(postId);
        return commentEntityRepository.findAllByPost(post, pageable).map(Comment::fromEntity);
    }

    //post exist
    private PostEntity getPostOrException(Integer postId) {
        return postEntityRepository.findById(postId).orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    //user exist
    private UserEntity getUserOrException(String userName) {
        return userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}
