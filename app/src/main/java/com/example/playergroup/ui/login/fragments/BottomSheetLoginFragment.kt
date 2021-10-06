package com.example.playergroup.ui.login.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.playergroup.R
import com.example.playergroup.custom.DialogCustom
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.DialogLoginContainerBinding
import com.example.playergroup.ui.login.JoinLoginAdapter
import com.example.playergroup.ui.login.LoginType
import com.example.playergroup.ui.login.LoginViewModel
import com.example.playergroup.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetLoginFragment: BottomSheetDialogFragment() {

    private val binding by viewBinding(DialogLoginContainerBinding::bind)
    private val loginViewModel by activityViewModels<LoginViewModel>()

    //Google Login Activity Result
    private lateinit var getGoogleSignResult: ActivityResultLauncher<Intent>
    private lateinit var onDismissListener: () -> Unit

    companion object {
        fun newInstance(tabPosition: Int = 0, onDismissListener: () -> Unit): BottomSheetLoginFragment =
            BottomSheetLoginFragment().apply {
                arguments = Bundle().apply {
                    putInt(TAB_POSITION, tabPosition)
                }
                this.onDismissListener = onDismissListener
            }
        const val TAB_POSITION = "tab_position"
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setOnShowListener { setupBottomSheet(it) }
        dialog.setOnDismissListener { getLoadingProgress(false) }
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheetDialog.setCanceledOnTouchOutside(false)  // outside Touch Dismiss Disable
        bottomSheet?.let {
            it.setBackgroundColor(Color.TRANSPARENT)
            val behavior = BottomSheetBehavior.from(it)
            behavior.isDraggable = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        } ?: run {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_login_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initActivityResult()
        initViewModel()
    }

    private fun initViewModel() {
        loginViewModel.apply {
            loadingProgress = this@BottomSheetLoginFragment::getLoadingProgress
            pagerMoveCallback = this@BottomSheetLoginFragment::getPagerMove

            loginViewModel.apply {
                googleLogin = this@BottomSheetLoginFragment::googleLogin

                // Google Login 성공 하게 되면 여기로 들어온다
                firebaseResult.observe(viewLifecycleOwner, Observer { loginResultCallback ->
                    loadingProgress?.invoke(false)
                    if (loginResultCallback.isSuccess) {
                        onDismissListener.invoke()
                        dismiss()
                    } else {
                        DialogCustom(requireContext())
                            .setMessage(R.string.dialog_alert_msg_error)
                            .setConfirmBtnText(R.string.ok)
                            .setDialogCancelable(false)
                            .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                                override fun onClick(dialogCustom: DialogCustom) {
                                    dialogCustom.dismiss()
                                }
                            })
                            .show()
                    }
                })

                firebaseJoinResult.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        loadingProgress?.invoke(false)
                        requireContext().showDefDialog(getString(R.string.email_check)).show()
                        pagerMoveCallback?.invoke(LoginType.LOGIN.value)
                    }
                })

                firebaseError.observe(viewLifecycleOwner, Observer {
                    loadingProgress?.invoke(false)
                    when (it) {
                        is Int -> requireContext().showDefDialog(getString(it)).show()
                        is String -> requireContext().showDefDialog(it).show()
                    }
                })

                firebaseUserPasswordResult.observe(viewLifecycleOwner, Observer {
                    loadingProgress?.invoke(false)
                    val message = if (it) {
                        "비밀번호 변경 메일을 전송했습니다."
                    } else {
                        "가입된 이메일이 없습니다. 다시 한번 확인해 주세요."
                    }
                    requireContext().showDefDialog(message).show()
                })
            }
        }
    }

    private fun googleLogin() {
        LandingRouter.move(requireContext(), RouterEvent(type = Landing.GOOGLE_LOGIN, activityResult = getGoogleSignResult))
    }

    private fun initActivityResult() {
        getGoogleSignResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                    val token = account?.idToken
                    if (token.isNullOrEmpty()) {
                        requireContext().debugToast { "Error > google 토큰값을 가져오지 못함" }
                    } else {
                        loginViewModel.firebaseAuthWithGoogle(token)
                    }
                } catch (e: ApiException) {
                    requireContext().debugToast { "Google sign in failed > ${e.message}" }
                    Log.e("GOOGLE", "Error > $e")
                }
            }
    }

    private fun initView() {
        val index = arguments?.getInt(TAB_POSITION) ?: LoginType.LOGIN.value
        binding.pager.adapter = JoinLoginAdapter(childFragmentManager)
        binding.pager.apply {
            adapter = JoinLoginAdapter(childFragmentManager)
            setCurrentItem(index, false)
        }
        binding.close click {
            dismiss()
        }
    }

    private fun getPagerMove(index: Int) {
        binding.pager.currentItem = index
    }

    private fun getLoadingProgress(isShow: Boolean) {
        binding.loadingProgress.publisherLoading(isShow)
    }
}