package com.example.playergroup.ui.club.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentClubMemberBinding
import com.example.playergroup.ui.club.ClubViewModel
import com.example.playergroup.ui.club.adapter.ClubMemberListAdapter
import com.example.playergroup.util.hideKeyboard
import com.example.playergroup.util.setItemAnimatorDuration
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
        initEditText()
        initRecyclerView()
        initObserver()
        viewModel.getMemberTabData()
    }

    private fun initEditText() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    viewModel.onNextObservable(it)
                }
            }
        })
    }

    private fun initObserver() {
        viewModel.apply {
            getMemberTabList = { (binding.recyclerView.adapter as? ClubMemberListAdapter)?.items?.map { it.copy() }?.toMutableList() }
            clubMemberLiveData.observe(viewLifecycleOwner, Observer {
                (binding.recyclerView.adapter as? ClubMemberListAdapter)?.submitList(it.first, it.second)
            })
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ClubMemberListAdapter()

            itemAnimator = setItemAnimatorDuration(150L)

            setOnTouchListener { v, event ->
                hideKeyboard(binding.etSearch)
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard(binding.etSearch)
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(binding.etSearch)
    }
}