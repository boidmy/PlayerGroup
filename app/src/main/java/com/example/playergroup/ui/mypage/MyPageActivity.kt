package com.example.playergroup.ui.mypage

import android.content.Intent
import android.net.Network
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.playergroup.R
import com.example.playergroup.custom.DialogCustom
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet.Companion.ScrollSelectorType
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet.Companion.ScrollSelectorType.*
import com.example.playergroup.data.INTENT_EXTRA_PARAM
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.data.UserInfo
import com.example.playergroup.databinding.ActivityMyinfoBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.*
import io.reactivex.android.schedulers.AndroidSchedulers

class MyPageActivity: BaseActivity<ActivityMyinfoBinding>() {

    private var isEditMode = false
    private val myPageViewModel by viewModels<MyPageViewModel>()
    private lateinit var galleryImgResult: ActivityResultLauncher<Intent>

    override fun getViewBinding(): ActivityMyinfoBinding = ActivityMyinfoBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        // 처음 진입했을 경우
        val isFirstEntry = intent?.getBooleanExtra(INTENT_EXTRA_PARAM, false) ?: false
        if (isFirstEntry) isEditMode = true

        initGalleryImgResult()
        initView(isFirstEntry)
        pgApplication.userInfo?.let {
            setUserProfileView(it)
        }
        initViewModel()

    }

    private fun initGalleryImgResult() {
        galleryImgResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val selectedImageUri = it.data?.data

            if (selectedImageUri == null) {
                debugToast { "이미지를 가져오지 못했습니다." }
                return@registerForActivityResult
            }

            myPageViewModel.profileImgUri = selectedImageUri
            Glide.with(this).load(selectedImageUri).into(binding.ivProfileImg)
        }
    }

    override fun onBackPressed() {
        if (!binding.ivBack.isVisible) {
            DialogCustom(this)
                .setMessage(R.string.profile_first_entry_back_key_info)
                .setConfirmBtnText(R.string.ok)
                .showCancelBtn(true)
                .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                    override fun onClick(dialogCustom: DialogCustom) { dialogCustom.dismiss() }
                })
                .setCancelBtnText(getString(R.string.app_finish_btn))
                .setCancelClickListener(object: DialogCustom.DialogCustomClickListener {
                    override fun onClick(dialogCustom: DialogCustom) {
                        dialogCustom.dismiss()
                        finish()
                    }
                })
                .show()
            return
        }

        if (isEditMode) {
            // 수정중 인 경우
            DialogCustom(this)
                .setMessage(R.string.profile_edit_mode_back_key_info)
                .setConfirmBtnText(R.string.ok)
                .showCancelBtn(true)
                .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                    override fun onClick(dialogCustom: DialogCustom) {
                        dialogCustom.dismiss()
                        finish()
                    }
                })
                .setCancelBtnText(getString(R.string.cancel))
                .setCancelClickListener(object: DialogCustom.DialogCustomClickListener {
                    override fun onClick(dialogCustom: DialogCustom) { dialogCustom.dismiss() }
                })
                .show()
            return
        }
        finish()
    }

    private fun initViewModel() {
        myPageViewModel.apply {
            isEditModeEvent
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::setEditMode)
            isEditModeEvent.onNext(isEditMode)

            firebaseResult.observe(this@MyPageActivity, Observer { isSuccessful ->
                binding.loadingProgress.publisherLoading(false)
                if (isSuccessful) {
                    LandingRouter.move(this@MyPageActivity, RouterEvent(type = Landing.MAIN))
                } else {
                    Toast.makeText(this@MyPageActivity, "프로필을 저정하지 못했습니다. \n잠시 후에 다시 이용해 주세요.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setUserProfileView(userInfo: UserInfo) {
        with (binding) {
            myPageViewModel.getUserProfileImg(userInfo.email) {
                if (!isDestroyed && !it.isNullOrEmpty()) {
                    Glide.with(this@MyPageActivity)
                        .load(it)
                        .into(ivProfileImg)
                }
            }

            etMyInfoName.setText(userInfo.name ?: "")
            etMyInfoHeight.setText(userInfo.height ?: "")
            etMyInfoWeight.setText(userInfo.weight ?: "")
            etMyInfoPosition.setText(userInfo.position ?: "")
            etMyInfoAge.setText(userInfo.age ?: "")
            etMyInfoSex.setText(userInfo.sex ?: "")
            etMyInfoComment.setText(userInfo.comment ?: "")
        }
    }

    private fun initView(isFirstEntry: Boolean) {
        with(binding)  {

            root.setOnTouchListener { v, event ->
                hideKeyboard(etMyInfoComment)
                false
            }

            ivBack.apply {
                click { onBackPressed() }
                visibility = if (isFirstEntry) View.GONE else View.VISIBLE
            }
            ivEdit.apply {
                setEditModeState(isEditMode)
                click {
                    if (isEditMode) {
                        DialogCustom(this@MyPageActivity)
                            .setMessage(R.string.profile_edit_save_confirm_info)
                            .setConfirmBtnText(R.string.ok)
                            .showCancelBtn(true)
                            .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                                override fun onClick(dialogCustom: DialogCustom) {
                                    isEditMode = !isEditMode
                                    saveMyProfile()
                                    myPageViewModel.isEditModeEvent.onNext(isEditMode)
                                    dialogCustom.dismiss()
                                }
                            })
                            .setCancelBtnText(getString(R.string.cancel))
                            .setCancelClickListener(object: DialogCustom.DialogCustomClickListener {
                                override fun onClick(dialogCustom: DialogCustom) { dialogCustom.dismiss() }
                            })
                            .show()
                    } else {
                        isEditMode = !isEditMode
                        myPageViewModel.isEditModeEvent.onNext(isEditMode)
                    }
                    setEditModeState(isEditMode)
                }
            }

            llProfileImg click {
                LandingRouter.move(this@MyPageActivity, RouterEvent(type = Landing.GALLERY, activityResult = galleryImgResult))
            }

            llMyInfoSex.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(SEX) }
            etMyInfoSex click { setScrollerPicker(SEX, etMyInfoSex.text.toString()) }

            llMyInfoAge.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(YEAROFBIRTH) }
            etMyInfoAge click { setScrollerPicker(YEAROFBIRTH, etMyInfoAge.text.toString()) }

            llMyInfoHeight.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(HEIGHT)}
            etMyInfoHeight click { setScrollerPicker(HEIGHT, etMyInfoHeight.text.toString()) }

            llMyInfoWeight.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(WEIGHT)}
            etMyInfoWeight click { setScrollerPicker(WEIGHT, etMyInfoWeight.text.toString()) }

            llMyInfoPosition.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(POSITION)}
            etMyInfoPosition click { setScrollerPicker(POSITION, etMyInfoPosition.text.toString()) }

        }
    }

    private fun setScrollerPicker(type: ScrollSelectorType, selectItem: String = "") {
        val newInstance = ScrollSelectorBottomSheet.newInstance(type, selectItem) {
            when(type) {
                HEIGHT -> binding.etMyInfoHeight.setText(it)
                WEIGHT -> binding.etMyInfoWeight.setText(it)
                YEAROFBIRTH -> binding.etMyInfoAge.setText(it)
                SEX -> binding.etMyInfoSex.setText(it)
                else -> binding.etMyInfoPosition.setText(it)
            }
        }
        if (newInstance.isVisible) return
        newInstance.show(supportFragmentManager, newInstance.tag)
    }

    private fun ImageView.setEditModeState(isState: Boolean) {
        this.setImageDrawable(ContextCompat.getDrawable(this@MyPageActivity,
            if (isState) R.drawable.icon_check else R.drawable.icon_edit
        ))
    }

    private fun saveMyProfile() {
        hideKeyboard(binding.etMyInfoComment)
        if (isEditTextEmpty()) {
            showDefDialog(getString(R.string.profile_edit_is_empty)).show()
            return
        }

        myPageViewModel.saveProfile(getMakeUserInfoClass())
        binding.loadingProgress.publisherLoading(true)
    }

    private fun isEditTextEmpty(): Boolean = (
            binding.etMyInfoName.text.isNullOrEmpty() ||
                    binding.etMyInfoSex.text.isNullOrEmpty() ||
                    binding.etMyInfoAge.text.isNullOrEmpty() ||
                    binding.etMyInfoHeight.text.isNullOrEmpty() ||
                    binding.etMyInfoWeight.text.isNullOrEmpty() ||
                    binding.etMyInfoPosition.text.isNullOrEmpty()
            )

    private fun getMakeUserInfoClass() = UserInfo(
        email = myPageViewModel.getCurrentUser()?.email,
        name = binding.etMyInfoName.text.toString(),
        height = binding.etMyInfoHeight.text.toString(),
        weight = binding.etMyInfoWeight.text.toString(),
        position = binding.etMyInfoPosition.text.toString(),
        age = binding.etMyInfoAge.text.toString(),
        sex = binding.etMyInfoSex.text.toString(),
        img = myPageViewModel.getCurrentUser()?.email,
        comment = binding.etMyInfoComment.text.toString()
    )

    private fun setEditMode(isEditMode: Boolean) {
        with (binding) {
            etMyInfoName.isEnabled = isEditMode
            llProfileImg.isEnabled = isEditMode
            etMyInfoSex.isEnabled = isEditMode
            etMyInfoAge.isEnabled = isEditMode
            etMyInfoHeight.isEnabled = isEditMode
            etMyInfoWeight.isEnabled = isEditMode
            etMyInfoPosition.isEnabled = isEditMode
            etMyInfoComment.isEnabled = isEditMode
        }
    }
}