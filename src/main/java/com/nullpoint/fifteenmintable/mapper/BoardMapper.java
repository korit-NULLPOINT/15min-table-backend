package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.Board;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {

    int createBoard(Board board);

    // 전체 조회
    List<Board> findAll();

    // 타입별 조회
    List<Board> findByBoardTypeId(Integer boardTypeId);

    // 단건 조회
    Board findByTitle(String title);
    Board findByBoardId(Integer boardId);

    int updateBoard(Board board);
    int deleteBoard(Integer boardId);
}