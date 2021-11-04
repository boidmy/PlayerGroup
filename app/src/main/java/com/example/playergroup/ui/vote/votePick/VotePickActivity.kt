package com.example.playergroup.ui.vote.votePick

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import com.example.playergroup.R
import com.example.playergroup.databinding.ActivityVotePickBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.vote.votePick.list.VotePickAdapter
import com.example.playergroup.ui.vote.votePick.list.VotePickReviewAdapter
import com.example.playergroup.util.click
import dagger.hilt.android.AndroidEntryPoint

class VotePickActivity : BaseActivity<ActivityVotePickBinding>() {

    private val viewModel: VotePickViewModel by viewModels()
    val adapter: VotePickAdapter = VotePickAdapter()
    val reviewAdapter: VotePickReviewAdapter = VotePickReviewAdapter()

    override fun getViewBinding(): ActivityVotePickBinding = ActivityVotePickBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {

        observe()

        binding.voteList.adapter = adapter
        binding.messageList.adapter = reviewAdapter

        val voteKey = intent.getIntExtra("key", 0)
        initView(voteKey)

        binding.voteComplete click {
            adapter.item?.let {
                viewModel.updateVote(it, voteKey)
            }
        }

        binding.messageInsert click {
            viewModel.updateReview(binding.messageEdit.text.toString(), voteKey)
        }

        val drawable = ResourcesCompat
            .getDrawable(
                binding.messageEdit.resources,
                R.drawable.edge_round_send,
                null
            ) as GradientDrawable
        binding.messageEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length ?: 0 > 0) {
                    drawable.setColor(Color.parseColor("#45D4FA"))
                    binding.messageInsert.background = drawable
                    binding.messageInsert.isEnabled = true
                } else {
                    drawable.setColor(Color.parseColor("#C6C6C6"))
                    binding.messageInsert.background = drawable
                    binding.messageInsert.isEnabled = false
                }
            }
        })
    }

    fun observe() {
        viewModel.voteItem.observe(this, {
            binding.voteCreateName.text = it.creater
            adapter.setData(it)
            reviewAdapter.setData(it)
        })
    }

    private fun initView(voteKey: Int) {
        viewModel.selectVote(voteKey)
    }
}