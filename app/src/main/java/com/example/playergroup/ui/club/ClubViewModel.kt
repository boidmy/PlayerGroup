package com.example.playergroup.ui.club

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.custom.AppBarStateChangeListener
import com.example.playergroup.custom.Event
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.ui.base.BaseViewModel

class ClubViewModel: BaseViewModel() {

    private val _firebaseClubDataResult: MutableLiveData<ClubInfo?> = MutableLiveData()
    val firebaseClubDataResult: LiveData<ClubInfo?>
        get() = _firebaseClubDataResult

    private val _scrollToTopBtnStateChangeEvent: MutableLiveData<Boolean> = MutableLiveData()
    val scrollToTopBtnStateChangeEvent: LiveData<Boolean>
        get() = _scrollToTopBtnStateChangeEvent

    private val _scrollTopEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val scrollTopEvent: LiveData<Event<Boolean>>
        get() = _scrollTopEvent

    private val _setViewTopRoundModeEvent: MutableLiveData<AppBarStateChangeListener.AppBarState> = MutableLiveData(AppBarStateChangeListener.AppBarState.EXPANDED)
    val setViewTopRoundModeEvent: LiveData<AppBarStateChangeListener.AppBarState>
        get() = _setViewTopRoundModeEvent

    fun getClubData(clubName: String) {
        clubRepository.getClubData(clubName) {
            _firebaseClubDataResult.value = it
        }
    }

    fun setScrollToTop() {
        _scrollTopEvent.value = Event(true)
    }

    fun setShowTopBtn(state: Boolean) {
        _scrollToTopBtnStateChangeEvent.value = state
    }

    fun setViewTopMode(state: AppBarStateChangeListener.AppBarState) {
        _setViewTopRoundModeEvent.value = state
    }
}
