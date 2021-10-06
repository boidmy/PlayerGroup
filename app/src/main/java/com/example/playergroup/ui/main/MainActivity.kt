package com.example.playergroup.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playergroup.data.Landing
import com.example.playergroup.data.LoginStateChange
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityMainBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.RxBus
import io.reactivex.rxkotlin.addTo

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initRecyclerView()
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            mainDataSet.observe(this@MainActivity, Observer { data ->
                (binding.recyclerView.adapter as? MainListAdapter)?.let { adapter->
                    adapter.items = data
                    adapter.notifyDataSetChanged()
                }
            })
            getMainData()
        }

        RxBus.listen(LoginStateChange::class.java).subscribe {
            val userInfo = pgApplication.userInfo
            if (it.isLogin) {
                if (userInfo?.isEmptyData() == true) {
                    LandingRouter.move(this, RouterEvent(type = Landing.MY_PAGE, paramBoolean = true))
                } else {
                    //todo 메인 화면 업데이트
                    viewModel.getMainData()
                }
            } else {
                LandingRouter.move(this@MainActivity, RouterEvent(Landing.START_LOGIN_SCREEN))
            }
        }.addTo(compositeDisposable)
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = MainListAdapter()
        }
    }
}
