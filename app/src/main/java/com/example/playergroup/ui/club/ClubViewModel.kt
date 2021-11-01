package com.example.playergroup.ui.club

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.ClubMemberDataSet
import com.example.playergroup.data.MainDataSet
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.diffUtilResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

typealias GetMemberTabList = () -> MutableList<ClubMemberDataSet>?
class ClubViewModel: BaseViewModel() {

    lateinit var mClubInfo: ClubInfo

    var getMemberTabList: GetMemberTabList? = null

    private val _firebaseClubDataResult: MutableLiveData<ClubInfo?> = MutableLiveData()
    val firebaseClubDataResult: LiveData<ClubInfo?>
        get() = _firebaseClubDataResult

    private val _clubMemberLiveData: MutableLiveData<Pair<MutableList<ClubMemberDataSet>?, DiffUtil.DiffResult>> = MutableLiveData()
    val clubMemberLiveData: LiveData<Pair<MutableList<ClubMemberDataSet>?, DiffUtil.DiffResult>>
        get() = _clubMemberLiveData

    var joinEvent = PublishSubject.create<Boolean>()
    var joinCancelEvent = PublishSubject.create<Boolean>()

    fun getClubData(primaryKey: String) {
        clubRepository.getClubData(primaryKey) {
            _firebaseClubDataResult.value = it
        }
    }

    fun setJoin(primaryKey: String, email: String) {
        clubRepository.addClubJoinProgress(primaryKey, email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                joinEvent.onNext(true)
            }, {
                Log.d("####", "Club Join Error ${it.message}")
                joinEvent.onNext(false)
            })
            .addTo(compositeDisposable)
    }

    fun setJoinCancel(primaryKey: String, email: String) {
        clubRepository.removeClubJoinProgress(primaryKey, email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                joinCancelEvent.onNext(true)
            }, {
                Log.d("####", "Club Join Cancel Error ${it.message}")
                joinCancelEvent.onNext(false)
            })
            .addTo(compositeDisposable)
    }

    fun getMemberTabData() {
        val clubMemberPublish: PublishSubject<List<UserInfo>> = PublishSubject.create()
        val clubJoinMemberPublish: PublishSubject<List<UserInfo>> = PublishSubject.create()
        Observable.combineLatest(
            clubMemberPublish,
            clubJoinMemberPublish,
            { member, join -> createClubMemberList(member, join) })
            .map(::calculateDiffUtilResult)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _clubMemberLiveData.value = it
            }, {
                _clubMemberLiveData.value = null
            })
            .addTo(compositeDisposable)

        val member = mClubInfo.member ?: mutableListOf()
        member.add(mClubInfo.clubAdmin!!)   // 클럽장 추가
        val join = mClubInfo.joinProgress ?: mutableListOf()

        authRepository.getUsersProfileData(member)
            .subscribeOn(Schedulers.io())
            .subscribe({
                clubMemberPublish.onNext(it)
            }, {
                Log.d("####", "클럽 멤버 리스트 에러 >> ${it.message}")
                clubMemberPublish.onNext(mutableListOf())
            })
            .addTo(compositeDisposable)

        authRepository.getUsersProfileData(join)
            .subscribeOn(Schedulers.io())
            .subscribe({
                clubJoinMemberPublish.onNext(it)
            }, {
                Log.d("####", "클럽 가입자 대기 리스트 에러 >> ${it.message}")
                clubJoinMemberPublish.onNext(mutableListOf())
            })
            .addTo(compositeDisposable)
    }

    private fun createClubMemberList(member: List<UserInfo>?, join: List<UserInfo>?): MutableList<ClubMemberDataSet> {
        val modules = mutableListOf<ClubMemberDataSet>()

        modules.add(
            ClubMemberDataSet(
                viewType = ViewTypeConst.CLUB_TITLE_TEXT,
                titleText = "클럽 가입 신청자 (${join?.size ?: 0})"
            )
        )

        join?.forEach {
            modules.add(
                ClubMemberDataSet(
                    viewType = ViewTypeConst.CLUB_MEMBER_USER,
                    name = it.name ?: "",
                    email = it.email ?: "",
                    img = it.img ?: "",
                    playPosition = it.position ?: "",
                    isJoiningUser = true
                )
            )
        }

        modules.add(
            ClubMemberDataSet(
                viewType = ViewTypeConst.CLUB_TITLE_TEXT,
                titleText = "클럽 회원 (${member?.size ?: 0})"
            )
        )

        member?.forEach {
            modules.add(
                ClubMemberDataSet(
                    viewType = ViewTypeConst.CLUB_MEMBER_USER,
                    name = it.name ?: "",
                    email = it.email ?: "",
                    img = it.img ?: "",
                    playPosition = it.position ?: ""
                )
            )
        }
        return modules
    }

    private fun calculateDiffUtilResult(data: MutableList<ClubMemberDataSet>): Pair<MutableList<ClubMemberDataSet>?, DiffUtil.DiffResult> {
        val getCurrentList = getMemberTabList?.invoke()
        val result = diffUtilResult(
            oldList = getCurrentList,
            newList = data,
            itemCompare = { o, n -> o?.viewType == n?.viewType },
            contentCompare = { o, n -> o == n }
        )
        return Pair(data, result)
    }

}
