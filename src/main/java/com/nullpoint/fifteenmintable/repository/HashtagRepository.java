package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.entity.Hashtag;
import com.nullpoint.fifteenmintable.entity.RecipeHashtag;
import com.nullpoint.fifteenmintable.mapper.HashtagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HashtagRepository {

    @Autowired
    private HashtagMapper hashtagMapper;

    public int createHashtag(Hashtag hashtag) {
        return hashtagMapper.createHashtag(hashtag);
    }

    public int deleteHashtag(Integer hashtagId) {
        return hashtagMapper.deleteHashtag(hashtagId);
    }

    public Optional<Hashtag> getByHashtagId(Integer hashtagId) {
        return hashtagMapper.getByHashtagId(hashtagId);
    }

    public Optional<Hashtag> getByName(String name) {
        return hashtagMapper.getByName(name);
    }

    public List<Hashtag> getHashtagList() {
        return hashtagMapper.getHashtagList();
    }

    public  List<Hashtag> getByKeyword(String keyword) {
        return hashtagMapper.getByKeyword(keyword);
    }
}
