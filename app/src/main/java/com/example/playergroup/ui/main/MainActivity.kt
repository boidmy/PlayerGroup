package com.example.playergroup.ui.main

import android.content.Intent
import android.net.Network
import android.os.Bundle
import com.example.playergroup.api.AuthRepository
import com.example.playergroup.api.ClubRepository
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityMainBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.dropout.DropOutBottomSheet
import com.example.playergroup.ui.login.fragments.BottomSheetLoginFragment
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.ui.themeselector.ThemeSelectorBottomSheet
import com.example.playergroup.ui.vote.VoteActivity
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

        binding.btnThemeSelector click {
            val newInstance = ThemeSelectorBottomSheet.newInstance()
            newInstance.show(supportFragmentManager, newInstance.tag)
        }
        
        binding.btnVote click {
            startActivity(Intent(this, VoteActivity::class.java))
        }

    }
}
