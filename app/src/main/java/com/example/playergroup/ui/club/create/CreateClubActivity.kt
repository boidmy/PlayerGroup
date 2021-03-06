package com.example.playergroup.ui.club.create

import android.app.ActivityOptions
import android.content.Intent
import android.net.Network
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityCreateClubBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.*
import android.util.Pair as UtilPair

class CreateClubActivity: BaseActivity<ActivityCreateClubBinding>() {

    private lateinit var galleryImgResult: ActivityResultLauncher<Intent>
    private val createClubViewModel by viewModels<CreateClubViewModel>()
    private var clubImgUri: Uri? = null

    override fun getViewBinding(): ActivityCreateClubBinding = ActivityCreateClubBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initGalleryImgResult()
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        createClubViewModel.apply {
            isVisibleBtnCreateClub.observe(this@CreateClubActivity, Observer {
                binding.btnCreateClub.visibility = if (it) View.VISIBLE else View.GONE
            })

            firebaseCreateClubResult.observe(this@CreateClubActivity, Observer {
                binding.loadingProgress.publisherLoading(false)
                if (it.first) {
                    LandingRouter.move(
                        this@CreateClubActivity,
                        RouterEvent(
                            type = Landing.CLUB_MAIN,
                            primaryKey = it.second
                        ))
                    finish()
                } else {
                    Toast.makeText(this@CreateClubActivity, "????????? ?????? ??????\n??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun initGalleryImgResult() {
        galleryImgResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val selectedImageUri = it.data?.data

            if (selectedImageUri == null) {
                debugToast { "???????????? ???????????? ???????????????." }
                return@registerForActivityResult
            }

            clubImgUri = selectedImageUri
            Glide.with(this).load(selectedImageUri).into(binding.ivClubImg)
        }
    }

    private fun initView() {
        with (binding) {
            root.setOnTouchListener { v, event ->
            hideKeyboard(etClubNameEditText)
            false
            }

            ivBack click {
                finish()
            }

            btnClubNameOverlapCheck click {
                hideKeyboard(etClubNameEditText)
                val clubName = etClubNameEditText.text.toString()
                if (clubName.isNotEmpty() || clubName.length > 2) {
                    createClubViewModel.isClubEmptyOverlapCheck(etClubNameEditText.text.toString()) {
                        val mas = if (it) "?????? ????????? ????????? ?????? ?????????."
                        else "?????? ?????? ????????? ?????? ?????????."
                        showDefDialog(mas).show()
                        binding.btnCreateClub.visibility = if (it) View.VISIBLE else View.GONE
                    }
                } else {
                    showDefDialog("????????? ????????? ?????????.").show()
                }
            }

            btnCreateClub click {
                hideKeyboard(etClubNameEditText)
                loadingProgress.publisherLoading(true)
                createClubViewModel.insertInitCreateClub(etClubNameEditText.text.toString(), clubImgUri)
            }

            btnAddPhoto click {
                hideKeyboard(etClubNameEditText)
                LandingRouter.move(this@CreateClubActivity, RouterEvent(type = Landing.GALLERY, activityResult = galleryImgResult))
            }

            etClubNameEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.let { createClubViewModel.onNextObservable(it.toString()) }
                }
            })
        }
    }
}