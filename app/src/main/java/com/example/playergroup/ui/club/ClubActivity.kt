package com.example.playergroup.ui.club

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.INTENT_EXTRA_PRIMARY_KEY
import com.example.playergroup.R
import com.example.playergroup.data.*
import com.example.playergroup.databinding.ActivityClubBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.club.fragment.ClubCommonFragment
import com.example.playergroup.util.*
import com.google.android.material.appbar.AppBarLayout

class ClubActivity: BaseActivity<ActivityClubBinding>() {

    private val clubViewModel by viewModels<ClubViewModel>()

    override fun getViewBinding(): ActivityClubBinding = ActivityClubBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        val primaryKey = intent?.getStringExtra(INTENT_EXTRA_PRIMARY_KEY)
        binding.btnJoinClub.visibility = if (isVisitor(primaryKey)) View.VISIBLE else View.GONE
        initViewModel()

        if (primaryKey.isNullOrEmpty()) {
            showToast("해당 클럽은 문제가 있어 폐지 되었습니다. 관리자에게 문의 부탁 드립니다.")
            finish()
        } else {
            clubViewModel.getClubData(primaryKey)
        }
    }

    private fun initViewModel() {
        clubViewModel.apply {
            firebaseClubDataResult.observe(this@ClubActivity, Observer {
                if (it == null) {
                    showToast("해당 동호회는 삭제되었습니다.")
                    finish()
                } else {
                    initView(it)
                }
            })
        }
    }

    private fun initView(clubInfo: ClubInfo) {
        with (binding) {
            Glide.with(this@ClubActivity)
                .load(clubInfo.clubImg)
                .into(ivClubImg)

            tvClubName.text = clubInfo.clubName
            ivShare click {
                debugToast { "준비중" }
            }
            btnJoinClub click {
                debugToast { "준비중" }
            }
        }
        initTabList()
        initContent()
    }

    private fun initContent() {
        //초기 진입에는 클럽 정보화면을 노출 함.
        setFragment(ClubCommonFragment.newInstance(ViewTypeConst.CLUB_TAB_TYPE_INFO))
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in)
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    private fun initTabList() {
        binding.tab.apply {
            layoutManager = LinearLayoutManager(this@ClubActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = ClubTabListAdapter {
                moveToTabContent(it)
            }

            itemAnimator = setItemAnimatorDuration(150L)

            val testTabList = mutableListOf<ClubTabInfo>(
                ClubTabInfo(name = "클럽정보", primaryKey = "", isSelected = true, tabType = ViewTypeConst.CLUB_TAB_TYPE_INFO),
                ClubTabInfo(name = "모임일정", primaryKey = "", tabType = ViewTypeConst.CLUB_TAB_TYPE_SCHEDULER),
                ClubTabInfo(name = "사진첩", primaryKey = "", isNewFeed = true, tabType = ViewTypeConst.CLUB_TAB_TYPE_PHOTO),
                ClubTabInfo(name = "멤버", primaryKey = "", tabType = ViewTypeConst.CLUB_TAB_TYPE_MEMBER)
            )

            (adapter as? ClubTabListAdapter)?.submitList(testTabList)
        }
    }

    private fun moveToTabContent(type: ViewTypeConst) {
        val isHeaderExpandable = type == ViewTypeConst.CLUB_TAB_TYPE_INFO
        binding.appBar.setExpanded(isHeaderExpandable, true)
        setFragment(ClubCommonFragment.newInstance(type))

        val params = binding.appBar.layoutParams as CoordinatorLayout.LayoutParams
        if (params.behavior == null)
            params.behavior = AppBarLayout.Behavior()
        val behaviour = params.behavior as AppBarLayout.Behavior
        behaviour.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return isHeaderExpandable
            }
        })
    }

}