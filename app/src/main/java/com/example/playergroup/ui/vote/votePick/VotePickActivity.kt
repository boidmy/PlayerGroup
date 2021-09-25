package com.example.playergroup.ui.vote.votePick

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.playergroup.databinding.ActivityVotePickBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.vote.votePick.list.VotePickAdapter
import com.example.playergroup.util.click

class VotePickActivity : BaseActivity<ActivityVotePickBinding>() {

    lateinit var viewModel: VotePickViewModel
    val adapter: VotePickAdapter = VotePickAdapter()

    override fun getViewBinding(): ActivityVotePickBinding = ActivityVotePickBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(VotePickViewModel::class.java)

        binding.voteList.adapter = adapter

        val intent = intent
        val voteKey = intent.getIntExtra("key", 0)
        viewModel.selectVote(voteKey)
        observe()

        binding.voteComplete click {
            adapter.item?.let {
                viewModel.updateVote(it, voteKey)
            }
        }
    }

    fun observe() {
        viewModel.voteItem.observe(this, {
            binding.voteCreateName.text = it.creater
            adapter.setData(it)
        })
    }
}