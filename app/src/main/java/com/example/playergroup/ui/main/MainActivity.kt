package com.example.playergroup.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playergroup.data.AdjustDataSet
import com.example.playergroup.data.Landing
import com.example.playergroup.data.LoginStateChange
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityMainBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.vote.VoteActivity
import com.example.playergroup.util.*
import com.google.common.reflect.TypeToken
import io.reactivex.rxkotlin.addTo
import java.lang.reflect.Type
import com.google.gson.Gson

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initRecyclerView()
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            viewModel.getCurrentMainListData = { getMainListAdapter()?.items?.map { it.copy() }?.toMutableList() }
            mainDataSet.observe(this@MainActivity, Observer {
                getMainListAdapter()?.submitList(it.first, it.second)
            })
        }

        RxBus.listen(LoginStateChange::class.java).subscribe {
            val userInfo = pgApplication.userInfo
            if (it.isLogin) {
                if (userInfo?.isEmptyData() == true) {
                    LandingRouter.move(this, RouterEvent(type = Landing.MY_PAGE, paramBoolean = true))
                } else {
                    viewModel.getMainData(getSaveMainList())
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

    override fun onReload() { viewModel.getMainData(getSaveMainList()) }
    private fun getMainListAdapter() = binding.recyclerView.adapter as? MainListAdapter

    override fun onResume() {
        super.onResume()
        onReload()
    }

    private fun getSaveMainList(): MutableList<ViewTypeConst> {
        val json = ConfigModule(this).adjustMainMenuList
        val type: Type = object : TypeToken<MutableList<AdjustDataSet>>() {}.type
        val list: MutableList<AdjustDataSet> = Gson().fromJson(json, type) ?: mutableListOf<AdjustDataSet>()
        var mainList = list.map { it.viewType }.toMutableList()
        if (mainList.isNullOrEmpty()) {
            mainList = mutableListOf(
                ViewTypeConst.MAIN_NEW_CLUB_INFO,
                ViewTypeConst.MAIN_CLUB_PICK_INFO,
                ViewTypeConst.MAIN_PICK_LOCATION_INFO,
                ViewTypeConst.MAIN_APP_COMMON_BOARD_INFO
            )
        }

        /*binding.btnVote click {
            startActivity(Intent(this, VoteActivity::class.java))
        }
*/
        binding.btnNoticeBoard click {
            LandingRouter.move(this, RouterEvent(type = Landing.BOARD))
        }

        return mainList
    }
}
