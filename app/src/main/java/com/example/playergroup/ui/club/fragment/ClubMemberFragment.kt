package com.example.playergroup.ui.club.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentClubMemberBinding
import com.example.playergroup.ui.club.ClubViewModel
import com.example.playergroup.ui.club.adapter.ClubMemberListAdapter
import com.example.playergroup.util.viewBinding

class ClubMemberFragment: Fragment() {
    private val binding by viewBinding(FragmentClubMemberBinding::bind)
    private val viewModel by activityViewModels<ClubViewModel>()

    companion object {
        fun newInstance() = ClubMemberFragment().apply {
            arguments = Bundle().apply {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_club_member, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initObserver()


    }

    private fun initObserver() {

    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ClubMemberListAdapter()
        }
    }
}