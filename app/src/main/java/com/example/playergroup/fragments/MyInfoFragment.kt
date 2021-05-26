package com.example.playergroup.fragments


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playergroup.ActivityExchangeFragmentRxBus
import com.example.playergroup.MainApplication
import com.example.playergroup.R
import com.example.playergroup.data.UserInfo
import com.example.playergroup.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_selector_profile.view.*
import kotlinx.android.synthetic.main.fragment_myinfo.*
import java.util.*

/**
 * SNS Login 사용자는 FirebaseAuth.currentUser.photoUrl 을 가지고올 수 있음. ( 이름도 )
 */

class MyInfoFragment : BaseFragment() {

    private var profile: Uri? = null
    private val mRxBus by lazy { ActivityExchangeFragmentRxBus.getInstance() }

    private val isImageUpLoad = PublishSubject.create<Boolean>()
    private val isUserInfoUpLoad = PublishSubject.create<Boolean>()

    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_myinfo, container, false)

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 뷰에 데이터 꽂기
        setInitViewDateSet(MainApplication.getInstance().getUserInfo())

        // 백 버튼
        iv_back click {
            mainContainer goTo R.id.action_myInfoFragment_to_mainFragment
        }

        // 프로필 사진
        iv_img click {
            setSelectorBottomSheet(it.context)
        }

        // 생년월일 피커
        ll_my_info_age.setOnFocusChangeListener { view, focus -> if (focus) setDatePicker(view) }
        et_my_info_age.setOnClickListener { setDatePicker(it) }

        // 키, 몸무게, 포지션 피커
        ll_my_info_height.setOnFocusChangeListener { view, focus ->
            if (focus) setHeightWeightPosition(
                view
            )
        }
        et_my_info_height.setOnClickListener { setHeightWeightPosition(it) }
        et_my_info_weight.setOnClickListener { setHeightWeightPosition(it) }
        et_my_info_position.setOnClickListener { setHeightWeightPosition(it) }

        // 등록하기 버튼
        btn_register click { view ->
            //TODO 포커스가 없을 경우가 있다.. 애뮬레이터에서는 재현이 되는데 실제 폰에서는 재현하기 힘들기 때문에 .. 우선 넘어가자
            if (activity?.currentFocus != null) hideKeyboard(activity?.currentFocus!!)

            if (isEditTextEmpty()) {
                showDefDialog(view.context, view.context.getString(R.string.profile_edit_is_empty)).show()
            } else {
                mRxBus.publisher_loading(true)
                compositeDisposable.add(
                    Observable
                        .zip(isImageUpLoad, isUserInfoUpLoad, BiFunction { t1: Boolean, t2: Boolean -> (t1 && t2) })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { mRxBus.publisher_loading(false) }
                        .subscribe({
                            if (it) {
                                MainApplication.getInstance().setUserInfo(getDataToUserInfoClass())

                                DialogCustom(view.context)
                                    .setMessage(R.string.register_success)
                                    .setConfirmBtnText(R.string.go_home)
                                    .setDialogCancelable(false)
                                    .setConfirmClickListener(object :
                                        DialogCustom.DialogCustomClickListener {
                                        override fun onClick(dialogCustom: DialogCustom) {
                                            dialogCustom.dismiss()
                                            mainContainer goTo R.id.action_myInfoFragment_to_mainFragment
                                        }
                                    })
                                    .show()
                            } else {
                                showDefDialog(
                                    view.context,
                                    view.context.getString(R.string.dialog_alert_msg_error)
                                )
                            }

                        }, {
                            showDefDialog(
                                view.context,
                                view.context.getString(R.string.dialog_alert_msg_error)
                            )
                        })
                )

                // 1. 이미지 부터 Storage 에 저장 후
                upLoadFromMemory(profile)

                // 2. 이미지 Url 을 가져와 DB 에 저장한다.
                setUserInfo(getWriteMyInfoHashMap())
                    .addOnCompleteListener {
                        if (it.isSuccessful) isUserInfoUpLoad.onNext(true)
                    }



                /*// 1. 이미지 부터 Storage 에 저장 후
                upLoadFromMemory(profile)

                // 2. 이미지 Url 을 가져와 DB 에 저장한다.
                setUserInfo(getWriteMyInfoHashMap())
                    .addOnCompleteListener {
                        mRxBus.publisher_loading(false)
                        if (it.isSuccessful) {

                            MainApplication.getInstance().setUserInfo(getDataToUserInfoClass())

                            DialogCustom(view.context)
                                .setMessage(R.string.register_success)
                                .setConfirmBtnText(R.string.go_home)
                                .setDialogCancelable(false)
                                .setConfirmClickListener(object :
                                    DialogCustom.DialogCustomClickListener {
                                    override fun onClick(dialogCustom: DialogCustom) {
                                        dialogCustom.dismiss()
                                        mainContainer goTo R.id.action_myInfoFragment_to_mainFragment
                                    }
                                })
                                .show()
                        } else {
                            showDefDialog(
                                view.context,
                                view.context.getString(R.string.dialog_alert_msg_error)
                            )
                        }
                    }*/
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setDatePicker(view: View) {
        val cal = Calendar.getInstance()

        val imm: InputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        val contextThemeWrapper =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) view.context
            else ContextThemeWrapper(view.context, android.R.style.Theme_Black)

        val datePicker =
            DatePickerDialog(
                contextThemeWrapper!!,
                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val msg = String.format("%d년 %d월 %d일", year, month + 1, day)
                    et_my_info_age.setText(msg)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE)
            )
        datePicker.datePicker.maxDate = Date().time
        datePicker.show()
    }

    private fun setHeightWeightPosition(view: View) {
        CustomPickerDialog(view.context)
            .setConfirmClickListener(object : CustomPickerDialog.PickerDialogListener {
                override fun onClick(
                    customPickerDialog: CustomPickerDialog,
                    height: NumberPicker,
                    weight: NumberPicker,
                    position: NumberPicker
                ) {
                    et_my_info_height?.setText(height.value.toString())
                    et_my_info_weight?.setText(weight.value.toString())
                    et_my_info_position?.setText(
                        when (position.value) {
                            1 -> "포인트가드"
                            2 -> "슈팅가드"
                            3 -> "스몰포워드"
                            4 -> "파워포워드"
                            else -> "센터"
                        }
                    )
                    customPickerDialog.dismiss()
                }
            })
            .setCancelClickListener(object : CustomPickerDialog.PickerDialogListener {
                override fun onClick(
                    customPickerDialog: CustomPickerDialog,
                    height: NumberPicker,
                    weight: NumberPicker,
                    position: NumberPicker
                ) {
                    customPickerDialog.dismiss()
                }
            })
            .show()
    }

    private fun getWriteMyInfoHashMap(): HashMap<String, String> =
        hashMapOf(
            "email" to firebaseAuth.currentUser?.email.toString(),
            "name" to et_my_info_name.text.toString(),
            "height" to et_my_info_height.text.toString(),
            "weight" to et_my_info_weight.text.toString(),
            "position" to et_my_info_position.text.toString(),
            "age" to et_my_info_age.text.toString(),
            "addr" to et_my_info_addr.text.toString(),
            "img" to "userImg/${firebaseAuth.currentUser?.email.toString()}",
            "hope" to et_my_info_hope.text.toString()
        )

    private fun getDataToUserInfoClass() = UserInfo(
        firebaseAuth.currentUser?.email.toString(),
        et_my_info_name.text.toString(),
        et_my_info_height.text.toString(),
        et_my_info_weight.text.toString(),
        et_my_info_position.text.toString(),
        et_my_info_age.text.toString(),
        et_my_info_addr.text.toString(),
        firebaseAuth.currentUser?.email.toString(),
        et_my_info_hope.text.toString()
    )

    private fun setInitViewDateSet(userInfo: UserInfo?) {
        // 프로필
        FirebaseStorage.getInstance().reference.child(userInfo?.img.toString())
            .downloadUrl
            .addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .apply(
                        RequestOptions()
                            .skipMemoryCache(true)    //캐시 사용 해제, Firebase사용 시 느리기 때문에 사용 필수
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                    )
                    .placeholder(
                        CircularProgressDrawable(iv_img.context).apply {
                            setColorFilter(ContextCompat.getColor(iv_img.context, R.color.textColor_Medium_Emphasis), PorterDuff.Mode.SRC_IN)
                            //TODO 컬러 바꾸기.
                            strokeWidth = 5f
                            centerRadius = 30f
                            start()
                        }
                    )
                    .error(R.drawable.iv_empty_profile)
                    .thumbnail(0.5f)    //이미지 배율 설정, 낮게해도 상관 없음
                    .transform(CenterCrop(), RoundedCorners(50))
                    .into(iv_img)
            }
            .addOnFailureListener {
                Log.d("####", "이미지 경로 다운로드 실패 ")
            }

        // 하위 EditText 들 채움.
        userInfo?.let {
            et_my_info_name.setText(it.name)
            et_my_info_height.setText(it.height)
            et_my_info_weight.setText(it.weight)
            et_my_info_position.setText(it.position)
            et_my_info_age.setText(it.age)
            et_my_info_addr.setText(it.addr)
            et_my_info_hope.setText(it.hope)
        }
    }

    private fun setProfileImg(img: String?) {
        Glide.with(this)
            .load(img)
            .placeholder(R.drawable.iv_empty_profile)
            .transform(CenterCrop(), RoundedCorners(50))
            .into(iv_img)
    }

    private fun setSelectorBottomSheet(context: Context) {

        BottomSheetDialog(context, R.style.BottomSheetDialogStyle)
            .let { parent ->
                parent.setContentView(LayoutInflater.from(context).inflate(
                    R.layout.dialog_selector_profile,
                    null
                )
                    .apply {
                        btn_gallery click {
                            TedRx2Permission.with(it.context)
                                .setPermissions(Manifest.permission.CAMERA)
                                .request()
                                .doOnNext { parent.dismiss() }
                                .subscribe({
                                    sendTakeGalleryIntent()
                                }, {
                                    showDefDialog(
                                        parent.context,
                                        parent.context.getString(R.string.dialog_alert_msg_error)
                                    )
                                })
                        }
                        btn_img click {
                            it.context toastShort { "준비중 입니다." }
                            parent.dismiss()
                        }
                    })
                parent.show()
            }
    }

    private fun sendTakeGalleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-date", true)
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data?.data == null) return
            profile = data.data!!
            setProfileImg(profile.toString())
        }
    }

    private fun isEditTextEmpty(): Boolean = (
            et_my_info_name.text.isNullOrEmpty() ||
                    et_my_info_addr.text.isNullOrEmpty() ||
                    et_my_info_age.text.isNullOrEmpty() ||
                    et_my_info_height.text.isNullOrEmpty() ||
                    et_my_info_weight.text.isNullOrEmpty() ||
                    et_my_info_position.text.isNullOrEmpty() ||
                    et_my_info_hope.text.isNullOrEmpty()
            )

    private fun upLoadFromMemory(uri: Uri?){
        if (uri == null) return

        //TODO 저장할 때 용량을 줄이던가 하는 방법이 없을까 ?
        FirebaseStorage
            .getInstance()
            .reference
            .child("userImg")
            .child(firebaseAuth.currentUser?.email.toString())
            .putFile(uri)
            .addOnSuccessListener {
                isImageUpLoad.onNext(true)
            }
            .addOnProgressListener {
                val progress: Double = 100.0 * it.bytesTransferred / it.totalByteCount
                Log.d("####", "UpLoading >> $progress")
            }
            .addOnFailureListener {

            }
    }


    private fun upLoadResult() {

    }
}