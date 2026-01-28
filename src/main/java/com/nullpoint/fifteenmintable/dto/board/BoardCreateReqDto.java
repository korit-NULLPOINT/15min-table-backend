package com.nullpoint.fifteenmintable.dto.board;

import com.nullpoint.fifteenmintable.entity.Board;
import com.nullpoint.fifteenmintable.entity.BoardType;
import lombok.Data;

@Data
public class BoardCreateReqDto {

    private String title;
    private Integer boardTypeId;

    public Board toEntity() {
        Board board = new Board();
        board.setTitle(this.title);

        BoardType boardType = new BoardType();
        boardType.setBoardTypeId(this.boardTypeId);

        board.setBoardType(boardType);
        return board;
    }
}


