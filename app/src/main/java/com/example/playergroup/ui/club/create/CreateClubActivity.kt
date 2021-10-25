package com.example.playergroup.ui.club.create

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityCreateClubBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.dialog.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.util.*

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
                binding.btnCreateClub.isEnabled = it
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
                    Toast.makeText(this@CreateClubActivity, "동호회 개설 실패\n관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun initGalleryImgResult() {
        galleryImgResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val selectedImageUri = it.data?.data

            if (selectedImageUri == null) {
                debugToast { "이미지를 가져오지 못했습니다." }
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

            binding.btnCreateClub.isEnabled = false

            btnClubNameOverlapCheck click {
                hideKeyboard(etClubNameEditText)
                val clubName = etClubNameEditText.text.toString()
                if (clubName.isNotEmpty() || clubName.length > 2) {
                    createClubViewModel.isClubEmptyOverlapCheck(etClubNameEditText.text.toString()) {
                        val mas = if (it) "사용 가능한 동호회 이름 입니다."
                        else "이미 있는 동호회 이름 입니다."
                        showDefDialog(mas).show()
                        binding.btnCreateClub.isEnabled = it
                    }
                } else {
                    showDefDialog("정확히 입력해 주세요.").show()
                }
            }

            etLocation click {
                val newInstance = ScrollSelectorBottomSheet.newInstance(type = ViewTypeConst.SCROLLER_ACTIVITY_AREA, selectItem = etLocation.text.toString()) {
                    binding.etLocation.setText(it)
                }
                if (newInstance.isVisible) return@click
                newInstance.show(supportFragmentManager, newInstance.tag)
            }

            btnCreateClub click {
                //todo 활동 지역 확인할 수 있는 다이얼로그 뿌리기
                hideKeyboard(etClubNameEditText)
                loadingProgress.publisherLoading(true)
                createClubViewModel.insertInitCreateClub(etClubNameEditText.text.toString(), clubImgUri, etLocation.text.toString())
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