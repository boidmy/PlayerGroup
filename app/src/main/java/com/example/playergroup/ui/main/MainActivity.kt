package com.example.playergroup.ui.main

import android.net.Network
import android.os.Bundle
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityMainBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.dropout.DropOutBottomSheet
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.click
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onNetworkStateLost(network: Network?) {}
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
    }
}