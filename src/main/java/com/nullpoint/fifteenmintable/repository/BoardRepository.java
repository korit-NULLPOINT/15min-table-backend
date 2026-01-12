package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.entity.Board;
import com.nullpoint.fifteenmintable.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private final BoardMapper boardMapper;

    public int createBoard(Board board) {
        return boardMapper.createBoard(board);
    }

    public Optional<List<Board>> findAll() {
        return boardMapper.findAll();
    }

    public Optional<List<Board>> findByBoardTypeId(Integer boardTypeId) {
        return boardMapper.findByBoardTypeId(boardTypeId);
    }

    public Optional<Board> findByTitle(String title) {
        return boardMapper.findByTitle(title);
    }

    public Optional<Board> findByBoardId(Integer boardId) {
        return boardMapper.findByBoardId(boardId);
    }

    public int updateBoard (Board board) {
        return boardMapper.updateBoard(board);
    }

    public int deleteBoard (Integer boardId) {
        return boardMapper.deleteBoard(boardId);
    }


}
