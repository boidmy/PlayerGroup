package com.example.playergroup.ui.club

import android.graphics.drawable.Drawable
import android.net.Network
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.INTENT_EXTRA_STRING_PARAM
import com.example.playergroup.data.INTENT_EXTRA_URI_TO_STRING_PARAM
import com.example.playergroup.databinding.ActivityClubBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.club.create.CreateClubViewModel
import com.example.playergroup.util.showDefDialog

class ClubActivity: BaseActivity<ActivityClubBinding>() {

    private val clubViewModel by viewModels<ClubViewModel>()
    private var isCreateClubLanding = false // 새로 만들어서 하게 되면 true 를 갖는다

    override fun getViewBinding(): ActivityClubBinding = ActivityClubBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        val imgUri = intent?.getStringExtra(INTENT_EXTRA_URI_TO_STRING_PARAM)
        val getClubName = intent?.getStringExtra(INTENT_EXTRA_STRING_PARAM)
        if (!imgUri.isNullOrEmpty()) {
            isCreateClubLanding = true
            supportPostponeEnterTransition()
            initSetClubImgTransition(Uri.parse(imgUri), getClubName)
        }
        initViewModel()
        if (getClubName.isNullOrEmpty()) {
            showDefDialog("해당 동호회는 삭제되었습니다.")
            finish()
        } else {
            clubViewModel.getClubData(getClubName)
        }
    }

    private fun initViewModel() {
        clubViewModel.apply {
            firebaseClubDataResult.observe(this@ClubActivity, Observer {
                if (it == null) {
                    showDefDialog("동호회가 없어 진입할 수 없습니다.").show()
                    finish()
                } else {
                    initView(it)
                }
            })
        }
    }

    private fun initView(clubInfo: ClubInfo) {
        val clubImg = clubInfo.clubImgFullUrl
        if (!isCreateClubLanding && !clubImg.isNullOrEmpty()) initSetClubImgTransition(clubImg, clubInfo.clubName)
    }

    private fun initSetClubImgTransition(imgUri: Any, clubName: String?) {
        val clubImg = if (imgUri is Uri) (imgUri as Uri) else (imgUri as String)
        Glide.with(this)
            .load(clubImg)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    setEnterTransition(clubName)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    setEnterTransition(clubName)
                    return false
                }
            })
            .into(binding.ivClubImg)
    }

    private fun setEnterTransition(clubName: String?) {
        binding.tvClubName.text = clubName ?: ""
        if (isCreateClubLanding) supportStartPostponedEnterTransition()
    }
}