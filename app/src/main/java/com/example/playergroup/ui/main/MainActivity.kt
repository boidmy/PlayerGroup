package com.example.playergroup.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityMainBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.mypage.MyPageViewModel
import com.example.playergroup.util.LandingRouter

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initRecyclerView()
        initViewModel()
    }

    private fun initViewModel() {
        with(viewModel) {
            mainDataSet.observe(this@MainActivity, Observer { data ->
                (binding.recyclerView.adapter as? MainListAdapter)?.let { adapter->
                    adapter.items = data
                    adapter.notifyDataSetChanged()
                }
            })
            getMainData()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = MainListAdapter()
        }
    }

    override fun onLoginStateChange(isLogin: Boolean) {
        val userInfo = pgApplication.userInfo
        if (isLogin) {
            if (userInfo?.isEmptyData() == true) {
                LandingRouter.move(this, RouterEvent(type = Landing.MY_PAGE, paramBoolean = true))
            } else {
                //todo 메인 화면 업데이트
            }
        } else {
            LandingRouter.move(this@MainActivity, RouterEvent(Landing.START_LOGIN_SCREEN))
        }
    }
}
