package com.example.playergroup.ui.vote.voteCreate

import android.os.Bundle
import androidx.activity.viewModels
import com.example.playergroup.data.room.VoteData
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.databinding.ActivityVoteCreateBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.click
import dagger.hilt.android.AndroidEntryPoint

class VoteCreateActivity : BaseActivity<ActivityVoteCreateBinding>() {

    private val viewModel: VoteCreateViewModel by viewModels()

    override fun getViewBinding(): ActivityVoteCreateBinding = ActivityVoteCreateBinding.inflate(layoutInflater)

    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {

        binding.complete click {
            val voteList: ArrayList<VoteData> = ArrayList()
            voteList.add(VoteData(
                voteName = binding.voteSub1.text.toString(),
                count = 0,
                voteUser = null))
            voteList.add(VoteData(
                voteName = binding.voteSub2.text.toString(),
                count = 0,
                voteUser = null))
            val voteModel = VoteModel(
                sequence = null,
                title = binding.voteTitle.text.toString(),
                voteData = voteList,
                clubKey = "",
                creater = "고릴라",
                multipleVote = binding.multipleVoteBtn.isSelected)

            viewModel.insertVote(voteModel)
            finish()
        }

        binding.multipleVoteBtn.apply {
            click {
                isSelected = !isSelected
            }
        }
    }
}