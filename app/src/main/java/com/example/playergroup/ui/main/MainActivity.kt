package com.example.playergroup.ui.main

import android.net.Network
import android.os.Bundle
import com.example.playergroup.databinding.ActivityMainBinding
import com.example.playergroup.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onNetworkStateLost(network: Network?) {}
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        
    }
}
