
package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.entity.Board;
import com.nullpoint.fifteenmintable.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardRepository {

    @Autowired
    private BoardMapper boardMapper;

    /**
     * 게시판 생성
     */
    public int addBoard(Board board) {
        return boardMapper.createBoard(board);
    }

    /**
     * 게시판 전체 조회
     */
    public List<Board> getBoardList() {
        return boardMapper.findAll(); // ⭕ 그대로 반환
    }

    /**
     * 게시판 삭제
     */
    public int removeBoard(Integer boardId) {
        return boardMapper.deleteBoard(boardId);
    }
}
