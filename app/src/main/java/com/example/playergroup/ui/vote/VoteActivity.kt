package com.example.playergroup.ui.vote

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.playergroup.databinding.ActivityVoteBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.vote.list.VoteAdapter
import com.example.playergroup.ui.vote.voteCreate.VoteCreateActivity
import com.example.playergroup.ui.vote.votePick.VotePickActivity
import com.example.playergroup.util.click
import dagger.hilt.android.AndroidEntryPoint

class VoteActivity : BaseActivity<ActivityVoteBinding>(){

    private val viewModel: VoteViewModel by viewModels()
    private val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        selectVote()
    }

    val adapter = VoteAdapter {
        val intent = Intent(this, VotePickActivity::class.java)
        intent.putExtra("key", it)
        startActivity(intent)
    }

    override fun getViewBinding(): ActivityVoteBinding = ActivityVoteBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {

        val createIntent = Intent(this, VoteCreateActivity::class.java)
        binding.voteCreate click {
            result.launch(createIntent)
        }

        binding.voteList.adapter = adapter
        selectVote()

        viewModel.voteList.observe(this, {
            adapter.setData(it)
        })
    }

    private fun selectVote() {
        viewModel.selectVote()
    }
}