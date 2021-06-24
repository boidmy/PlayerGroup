package com.example.playergroup.ui.main

import android.net.Network
import android.os.Bundle
import com.example.playergroup.api.AuthRepository
import com.example.playergroup.api.ClubRepository
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityMainBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.dropout.DropOutBottomSheet
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.click
import com.example.playergroup.util.showDefDialog
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val authRepository by lazy { AuthRepository() }
    private val clubRepository by lazy { ClubRepository() }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        binding.btnLogout click {
            FirebaseAuth.getInstance().signOut()
            LandingRouter.move(this, RouterEvent(Landing.LOGIN))
        }

        binding.btnDropOut click {
            val newInstance = DropOutBottomSheet.newInstance {
                FirebaseAuth.getInstance().signOut()
                LandingRouter.move(this, RouterEvent(Landing.LOGIN))
            }
            newInstance.show(supportFragmentManager, newInstance.tag)
        }

        binding.btnMyPage click {
            LandingRouter.move(this, RouterEvent(Landing.MY_PAGE))
        }

        binding.btnCreateClub click {
            LandingRouter.move(this, RouterEvent(Landing.CREATE_CLUB))
        }

        binding.btnMyClub click {
            authRepository.getUserProfileData(authRepository.getCurrentUser()?.email) {
                val clubName = it?.clubAdmin
                if (clubName.isNullOrEmpty()) {
                     showDefDialog("개설한 동호회가 없습니다.").show()
                } else {
                    clubRepository.isClubEmpty(clubName) { isClubEmpty ->
                        if(isClubEmpty) {
                            showDefDialog("개설한 동호회가 없습니다.").show()
                        } else {
                            LandingRouter.move(this, RouterEvent(type = Landing.CLUB_MAIN, paramString = clubName))
                        }
                    }
                }

            }
        }
    }
}
