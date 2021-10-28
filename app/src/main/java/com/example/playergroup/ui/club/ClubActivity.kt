package com.example.playergroup.ui.club

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playergroup.data.*
import com.example.playergroup.databinding.ActivityClubBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.click
import com.example.playergroup.util.debugToast
import com.example.playergroup.util.showToast

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
        }
        initTabList()
    }

    private fun initTabList() {
        binding.tab.apply {
            layoutManager = LinearLayoutManager(this@ClubActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = ClubTabListAdapter {

            }

            val testTabList = mutableListOf<ClubTabInfo>(
                ClubTabInfo(name = "클럽정보", primaryKey = "", isSelected = true),
                ClubTabInfo(name = "모임일정", primaryKey = ""),
                ClubTabInfo(name = "사진첩", primaryKey = "", isNewFeed = true),
                ClubTabInfo(name = "멤버", primaryKey = "")
            )

            (adapter as? ClubTabListAdapter)?.submitList(testTabList)
        }
    }

}