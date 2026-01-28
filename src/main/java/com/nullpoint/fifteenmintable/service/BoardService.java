    package com.nullpoint.fifteenmintable.service;

    import com.nullpoint.fifteenmintable.dto.ApiRespDto;
    import com.nullpoint.fifteenmintable.dto.board.BoardCreateReqDto;
    import com.nullpoint.fifteenmintable.entity.Board;
    import com.nullpoint.fifteenmintable.repository.BoardRepository;
    import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    public class BoardService {

        @Autowired
        private BoardRepository boardRepository;

        public ApiRespDto<Void> addBoard(BoardCreateReqDto dto, PrincipalUser principalUser) {
            Board board = dto.toEntity();

            int result = boardRepository.addBoard(board);
            if (result != 1) {
                return new ApiRespDto<>("failed", "게시판 생성 실패", null);
            }

            return new ApiRespDto<>("success", "게시판 생성 완료", null);
        }

        public ApiRespDto<List<Board>> getBoardList() {

            List<Board> boardList = boardRepository.getBoardList();
            return new ApiRespDto<>("success", "게시물 전체 조회 완료", boardList);
        }

        public ApiRespDto<Void> removeBoard(Integer boardId, PrincipalUser principalUser) {
            int result = boardRepository.removeBoard(boardId);
            if (result != 1) {
                return new ApiRespDto<>("failed", "게시물 삭제에 실패했습니다.", null);
            }

            return new ApiRespDto<>("success", "게시물 삭제 완료", null);
        }
    }
