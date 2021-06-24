package com.example.playergroup.ui.club

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.ui.base.BaseViewModel

class ClubViewModel: BaseViewModel() {

    private val _firebaseClubDataResult: MutableLiveData<ClubInfo?> = MutableLiveData()
    val firebaseClubDataResult: LiveData<ClubInfo?>
        get() = _firebaseClubDataResult

    fun getClubData(clubName: String) {
        clubRepository.getClubData(clubName) {
            _firebaseClubDataResult.value = it
        }
    }
}
