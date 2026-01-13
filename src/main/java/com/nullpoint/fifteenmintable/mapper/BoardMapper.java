package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.Board;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {
    int createBoard(Board board);

    Optional<List<Board>> findAll();
    Optional<List<Board>> findByBoardTypeId(Integer boardTypeId);
    Optional<Board> findByTitle(String title);
    Optional<Board> findByBoardId(Integer boardId);

    int updateBoard(Board board);

    int deleteBoard(int BoardId);
}
