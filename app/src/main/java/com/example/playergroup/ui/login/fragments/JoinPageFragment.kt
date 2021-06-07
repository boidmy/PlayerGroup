package com.example.playergroup.ui.login.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.playergroup.R
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.FragmentJoinBinding
import com.example.playergroup.ui.login.LoginViewModel
import com.example.playergroup.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import java.util.regex.Pattern

class JoinPageFragment: Fragment() {

    private val PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@\$!%*#?&]).{8,15}.\$")   // 8 ~ 16 ( 특수문자, 문자, 숫자 모두 포함 )
    private val loginViewModel by activityViewModels<LoginViewModel>()
    private val binding by viewBinding(FragmentJoinBinding::bind)

    //Google Login Activity Result
    private lateinit var getGoogleSignResult: ActivityResultLauncher<Intent>

    companion object {
        fun newInstance() = JoinPageFragment().apply {
            arguments = Bundle()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_join, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtnView()
        initActivityResult()
        initViewModel()
    }

    private fun initActivityResult() {
        getGoogleSignResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                    val token = account?.idToken
                    if (token.isNullOrEmpty()) {
                        requireContext() debugToast { "Error > google 토큰값을 가져오지 못함" }
                    } else {
                        loginViewModel.firebaseAuthWithGoogle(token)
                    }
                } catch (e: ApiException) {
                    requireContext() debugToast { "Google sign in failed > ${e.message}" }
                    Log.e("GOOGLE", "Error > $e")
                }
            }

    }

    private fun initViewModel() {
        loginViewModel.apply {
            // Google Login 성공 하게 되면 여기로 들어온다
            firebaseResult.observe(viewLifecycleOwner, Observer { isSuccessful ->
                loginViewModel.loadingProgress?.invoke(false)
                if (isSuccessful) {
                    LandingRouter.move(requireContext(), RouterEvent(Landing.MAIN))
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
        }
    }

    private fun initBtnView() {
        with (binding) {
            binding.btnJoin click {
                loginViewModel.loadingProgress?.invoke(true)
                LandingRouter.move(requireContext(), RouterEvent(
                    type = Landing.GOOGLE_LOGIN,
                    activityResult = getGoogleSignResult
                ))
            }
        }
    }

    // 화면을 벗어나면 editText 내용 삭제
    override fun onPause() {
        super.onPause()
        with(binding) {
            llJoinId.error = null
            etJoinId.text = null
            etJoinPw.text = null
        }
    }

    private fun isEditTextEmpty(id: AppCompatEditText, pw: AppCompatEditText) =
        (id.text?.trim().isNullOrEmpty() || pw.text?.trim().isNullOrEmpty())

    private fun isEmailPattern(et: AppCompatEditText) =
        (Patterns.EMAIL_ADDRESS.matcher(et.text?.trim().toString()).matches())

    private fun isPWDPattern(et: AppCompatEditText) =
        (PASSWORD_PATTERN.matcher(et.text?.toString()).matches())

}