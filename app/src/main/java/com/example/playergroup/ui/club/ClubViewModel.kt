package com.example.playergroup.ui.club

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.ClubMemberDataSet
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.diffUtilResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

typealias GetMemberTabList = () -> MutableList<ClubMemberDataSet>?
typealias JoinProgressMemberClickCallback = (String, Boolean) -> Unit
typealias DropClubMemberUserClickCallback = (String) -> Unit
typealias IsCurrentUserClubAdmin = () -> Boolean
class ClubViewModel: BaseViewModel() {

    lateinit var mClubInfo: ClubInfo
    lateinit var initMemberDataSet: MutableList<ClubMemberDataSet>  // 멤버 검색에서 x버튼 사용 시 필요한 데이터

    var getMemberTabList: GetMemberTabList? = null
    lateinit var isCurrentUserClubAdmin: IsCurrentUserClubAdmin

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

    fun setUpdateClubDescription(description: String) {
        clubRepository.updateClubDescription(mClubInfo.clubPrimaryKey, description)
    }

    fun getClubAdminUserName(callback: (String?) -> Unit) {
        authRepository.getUserProfileData(mClubInfo.clubAdmin) {
            callback.invoke(it?.name)
        }
    }

    fun setJoin(email: String) {
        val clubPrimaryKey = mClubInfo.clubPrimaryKey
        if (clubPrimaryKey.isNullOrEmpty()) return
        clubRepository.addClubJoinProgress(clubPrimaryKey, email)
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

    fun setJoinCancel(email: String) {
        val clubPrimaryKey = mClubInfo.clubPrimaryKey
        if (clubPrimaryKey.isNullOrEmpty()) return
        clubRepository.removeClubJoinProgress(clubPrimaryKey, email)
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
                initMemberDataSet = it.first?.map { it.copy() }?.toMutableList() ?: mutableListOf()
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
                    isJoiningUser = true,
                    isCurrentUserAdmin = isCurrentUserClubAdmin.invoke(),
                    joinProgressMemberClickCallback = ::setJoinProgressMemberClickCallback
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
                    isCurrentUserAdmin = isCurrentUserClubAdmin.invoke(),
                    playPosition = it.position ?: "",
                    dropClubMemberClickCallback = ::setClubDropUserClickCallback
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

    private fun setClubDropUserClickCallback(email: String) {
        val clubPrimaryKey = mClubInfo.clubPrimaryKey
        if (clubPrimaryKey.isNullOrEmpty()) return
        clubRepository.removeUserClubMember(clubPrimaryKey, email)
            .subscribeOn(Schedulers.io())
            .subscribe({
                clubRepository.getClubData(clubPrimaryKey) {
                    if (it != null) {
                        mClubInfo = it
                        getMemberTabData()
                    }
                }
            }, {
                Log.d("####", "Club Member Drop Error ${it.message}")
            }).addTo(compositeDisposable)

    }

    private fun setJoinProgressMemberClickCallback(email: String, isState: Boolean) {
        val clubPrimaryKey = mClubInfo.clubPrimaryKey
        if (clubPrimaryKey.isNullOrEmpty()) return

        clubRepository.getMemberDataJoinResult(clubPrimaryKey, email, isState)
            .subscribeOn(Schedulers.io())
            .subscribe({
                clubRepository.getClubData(clubPrimaryKey) {
                    if (it != null) {
                        mClubInfo = it
                        getMemberTabData()
                    }
                }
            }, {
                Log.d("####", "Club Join Approve Error ${it.message}")
            }).addTo(compositeDisposable)
    }

    fun onNextObservable(editTextString: CharSequence) {
        compositeDisposable.clear() // 비동기 로직 Stop
        Observable.create<CharSequence> { emitter -> emitter.onNext(editTextString) }
            .debounce(200L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .map(::createSearchViewResult)
            .map(::calculateDiffUtilResult)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _clubMemberLiveData.value = it
            }, {
                Log.d("####", "Member Search Error > ${it.message}")
            })
            .addTo(compositeDisposable)
    }

    private fun createSearchViewResult(editText: CharSequence): MutableList<ClubMemberDataSet> {
        if (editText.isEmpty()) return initMemberDataSet

        val items = initMemberDataSet.map { it.copy() }.toMutableList()
            .filter { !it.isJoiningUser }   // 가입된 멤머등 중에
            .filter {   // 검색하고자 하는 대상이 있는지
                it.name.toLowerCase(Locale.getDefault()).contains(editText.toString().toLowerCase(
                    Locale.getDefault()))
            }
            .toMutableList()

        return items
    }
}
