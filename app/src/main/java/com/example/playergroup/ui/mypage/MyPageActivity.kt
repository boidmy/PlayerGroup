package com.example.playergroup.ui.mypage

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.playergroup.R
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

class MyPageActivity: BaseActivity<ActivityMyinfoBinding>() {

    private var isEditMode = false
    private val myPageViewModel by viewModels<MyPageViewModel>()

    private lateinit var galleryImgResult: ActivityResultLauncher<Intent>

    override fun getViewBinding(): ActivityMyinfoBinding = ActivityMyinfoBinding.inflate(layoutInflater)
    override fun onNetworkStateLost(network: Network?) {}
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        // 처음 진입했을 경우
        val isFirstEntry = intent?.getBooleanExtra(INTENT_EXTRA_PARAM, false) ?: false
        if (isFirstEntry) isEditMode = true

        initInputAndImageView()
        initBtnView(isFirstEntry)
        initViewModel()
        initGalleryImgResult()
    }

    private fun initGalleryImgResult() {
        galleryImgResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val selectedImageUri = it.data?.data

            if (selectedImageUri == null) {
                debugToast { "이미지를 가져오지 못했습니다." }
                return@registerForActivityResult
            }

            myPageViewModel.profileImgUri = selectedImageUri

            var imgBitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            } else {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(contentResolver, selectedImageUri))
            }

            var exif: ExifInterface
            try {
                val realPath = getRealPathFromURI(selectedImageUri)
                exif = ExifInterface(realPath ?: "")
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            imgBitmap = rotateBitmap(imgBitmap, orientation)
            binding.ivProfileImg.setImageBitmap(imgBitmap)
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
        super.onBackPressed()
    }

    private fun initViewModel() {
        myPageViewModel.firebaseResult.observe(this, Observer { isSuccessful ->
            binding.loadingProgress.publisherLoading(false)
            if (isSuccessful) {
                LandingRouter.move(this, RouterEvent(type = Landing.MAIN))
            } else {
                Toast.makeText(this, "프로필을 저정하지 못했습니다. \n잠시 후에 다시 이용해 주세요.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initBtnView(isFirstEntry: Boolean) {
        with(binding)  {
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
                                    //isEditMode = !isEditMode
                                    saveMyProfile()
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
                    }
                    setEditModeState(isEditMode)
                }
            }

            llProfileImg click {
                LandingRouter.move(this@MyPageActivity, RouterEvent(type = Landing.GALLERY, activityResult = galleryImgResult))
            }

            llMyInfoSex.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(SEX) }
            etMyInfoSex click { setScrollerPicker(SEX) }

            llMyInfoAge.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(YEAROFBIRTH) }
            etMyInfoAge click { setScrollerPicker(YEAROFBIRTH) }

            llMyInfoHeight.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(HEIGHT)}
            etMyInfoHeight click { setScrollerPicker(HEIGHT) }

            llMyInfoWeight.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(WEIGHT)}
            etMyInfoWeight click { setScrollerPicker(WEIGHT) }

            llMyInfoPosition.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) setScrollerPicker(POSITION)}
            etMyInfoPosition click { setScrollerPicker(POSITION) }
        }
    }

    private fun setScrollerPicker(type: ScrollSelectorType) {
        val newInstance = ScrollSelectorBottomSheet.newInstance(type) {
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

    private fun initInputAndImageView() {
        with(binding) {
            val currentUser = myPageViewModel.getCurrentUser()
            Glide.with(this@MyPageActivity)
                .load(currentUser?.photoUrl)
                .placeholder(R.drawable.icon_user)
                .error(R.drawable.icon_user)
                .into(ivProfileImg)
        }
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

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = contentResolver.query(
            contentURI,
            null,
            null,
            null,
            null
        )
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        return try {
            val bmRotated =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
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
}