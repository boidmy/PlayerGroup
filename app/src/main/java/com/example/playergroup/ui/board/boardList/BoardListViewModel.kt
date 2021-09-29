package com.example.playergroup.ui.board.boardList

import com.example.playergroup.ui.base.BaseViewModel


class BoardListViewModel constructor(): BaseViewModel() {

    fun getBoardList(key: String) {
        noticeRepository.getBoardList(key) {

        }
    }
}