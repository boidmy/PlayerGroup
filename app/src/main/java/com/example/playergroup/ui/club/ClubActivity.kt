package com.example.playergroup.ui.club

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.INTENT_EXTRA_PRIMARY_KEY
import com.example.playergroup.databinding.ActivityClubBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.showToast

class ClubActivity: BaseActivity<ActivityClubBinding>() {

    private val clubViewModel by viewModels<ClubViewModel>()

    override fun getViewBinding(): ActivityClubBinding = ActivityClubBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        val primaryKey = intent?.getStringExtra(INTENT_EXTRA_PRIMARY_KEY)
        initViewModel()
        if (primaryKey.isNullOrEmpty()) {
            showToast("해당 동호회는 없습니다..")
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
        Glide.with(this)
            .load(clubInfo.clubImgFullUrl)
            .into(binding.ivClubImg)
        binding.tvClubName.text = clubInfo.clubName ?: ""
    }

}