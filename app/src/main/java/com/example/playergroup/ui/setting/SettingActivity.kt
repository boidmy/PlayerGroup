package com.example.playergroup.ui.setting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.playergroup.R
import com.example.playergroup.api.AuthRepository
import com.example.playergroup.api.ClubRepository
import com.example.playergroup.data.Landing
import com.example.playergroup.data.LoginStateChange
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivitySettingBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.login.LoginType
import com.example.playergroup.util.*
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxkotlin.addTo

/**
 * 앱 설정 화면
 * ScrollView 구조
 * RecyclerView 로 바꿀 필요가 있을지 ?
 */
class SettingActivity: BaseActivity<ActivitySettingBinding>() {

    override fun getViewBinding(): ActivitySettingBinding = ActivitySettingBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {

        binding.ivBack click { onBackPressed() }
        setUserNameIsLogin(pgApplication.isLogin())
        initThemeSelector()
        initPushSetting()
        initPermission()
        initAppVersion()
        initObserver()

        /*
        binding.btnCreateClub click {
            val clubCount = pgApplication.userInfo?.clubAdmin?.size ?: 0
            if (clubCount == 0) {
                LandingRouter.move(this, RouterEvent(Landing.CREATE_CLUB))
            } else {
                showDefDialog("우선 동호회는 하나만.. 개설 하는걸로 합시다 .. ").show()
            }
        }

        binding.btnMyCreatedClub click {
            //todo 클럽이 여러개 일 경우 어떻게 할지 확인 ! 지금은 0번째 그냥 갖고 온다
            val clubName = pgApplication.userInfo?.clubAdmin?.getOrNull(0)
            if (clubName.isNullOrEmpty()) {
                showDefDialog("개설한 동호회가 없습니다.").show()
            } else {
                clubRepository.isClubEmpty(clubName) { isClubEmpty ->
                    if(isClubEmpty) {
                        showDefDialog("개설한 동호회가 없습니다.").show()
                    } else {
                        LandingRouter.move(this, RouterEvent(type = Landing.CLUB_MAIN, primaryKey = clubName))
                    }
                }
            }
        }

        binding.btnMyJoinClub click {

        }

        binding.btnSearch click {
            LandingRouter.move(this, RouterEvent(type = Landing.SEARCH))
        }

        binding.btnVote click {
            startActivity(Intent(this, VoteActivity::class.java))
        }*/
    }

    override fun onResume() {
        super.onResume()
        initPermission()
    }

    private fun initAppVersion() {
        binding.tvAppVer.text = "버전정보 : $appVersion"
    }

    private fun initPermission() {
        with(binding) {

            with (llPermission1) {
                tvTitle.text = "카메라"
                tvSubTitle.text = "내 프로필 사진 변경 및 게시판 사진 업로드할 때 사용"
                tvAction click {
                    LandingRouter.move(this@SettingActivity, RouterEvent(type = Landing.APP_PERMISSION_SETTING))
                }
                tvOnOff setUsedPermission Manifest.permission.CAMERA
            }

            with (llPermission2) {
                tvTitle.text = "사진 및 파일"
                tvSubTitle.text = "저장된 사진 이나 파일에 접근할 때 사용"
                tvOnOff setUsedPermission Manifest.permission.READ_EXTERNAL_STORAGE
                tvAction click {
                    LandingRouter.move(this@SettingActivity, RouterEvent(type = Landing.APP_PERMISSION_SETTING))
                }
            }
        }
    }

    private infix fun TextView.setUsedPermission(permission: String) { text = if (getPermissionCheck(permission)) "ON" else "OFF" }
    private fun getPermissionCheck(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun initPushSetting() {
        //todo FCM 관련 다시 한번 보고 로직 적용 해야 할듯
        with(binding.llPush) {
            tvTitle.text = "이벤트 및 혜택 알림"
            tvSubTitle.text = "앱 푸쉬 수신시 알림을 받습니다. \n ( 푸쉬 설정을 해놓지 않은 경우 클럽 및 개인 푸쉬 또한 받을 수 없습니다. )"
        }
    }

    private fun initThemeSelector() {
        binding.tvCurrentTheme.apply {
            text = "${
                when(configModule.configThemeMode) {
                    ThemeMode.LIGHT_MODE.value -> getString(R.string.theme_light)
                    ThemeMode.DARK_MODE.value -> getString(R.string.theme_dark)
                    else -> getString(R.string.theme_system_default)
                }    
            } 사용중"

            click {
                LandingRouter.move(this@SettingActivity, RouterEvent(type = Landing.THEME_SELECTOR))
            }
        }
    }

    private fun setUserNameIsLogin(isLogin: Boolean) {
        with(binding) {
            tvUserName.text = if (isLogin) "${pgApplication.userInfo?.email} 님" else "로그인해 주세요"
            tvLoginOut.apply {
                text = if (isLogin) "로그아웃" else "로그인"
                click {
                    if (isLogin) {
                        LandingRouter.move(this@SettingActivity, RouterEvent(type = Landing.LOGOUT, paramBoolean = true))
                    } else {
                        LandingRouter.move(this@SettingActivity, RouterEvent(type = Landing.LOGIN, paramInt = LoginType.LOGIN.value))
                    }
                }
            }

            dividerWithDropOutText.dividerLayer.visibility = if (isLogin) View.VISIBLE else View.GONE
            tvDropOut.apply {
                click {
                    LandingRouter.move(this@SettingActivity, RouterEvent(Landing.DROP_OUT))
                }
                visibility = if (isLogin) View.VISIBLE else View.GONE
            }
        }
    }

    private fun initObserver() {
        RxBus.listen(LoginStateChange::class.java).subscribe {
            val userInfo = pgApplication.userInfo
            if (it.isLogin) {
                if (userInfo?.isEmptyData() == true) {
                    LandingRouter.move(this, RouterEvent(type = Landing.MY_PAGE, paramBoolean = true))
                } else {
                    setUserNameIsLogin(pgApplication.isLogin())
                }
            } else {
                LandingRouter.move(this@SettingActivity, RouterEvent(Landing.START_LOGIN_SCREEN))
            }
        }.addTo(compositeDisposable)
    }
}