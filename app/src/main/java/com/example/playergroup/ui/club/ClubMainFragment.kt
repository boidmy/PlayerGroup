package com.example.playergroup.ui.club

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentClubMainBinding
import com.example.playergroup.databinding.ViewClubListItemBinding
import com.example.playergroup.util.click
import com.example.playergroup.util.showToast
import com.example.playergroup.util.viewBinding

class ClubMainFragment: Fragment() {

    private val binding by viewBinding(FragmentClubMainBinding::bind)
    private val clubViewModel by activityViewModels<ClubViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_club_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ClubMainListAdapter()
        }
    }

    private fun initViewModel() {
        clubViewModel.apply {
            firebaseClubDataResult.observe(viewLifecycleOwner, Observer {
                (binding.recyclerView.adapter as? ClubMainListAdapter)?.items = it?.clubMainData
            })
        }
    }

    inner class ClubMainListAdapter: RecyclerView.Adapter<ClubItemViewHolder>() {
        var items: MutableList<String>? = null
            set(value) {
                value?.let {
                    field?.let {
                        it.clear()
                        it.addAll(value)
                    } ?: run {
                        field = value
                    }
                    notifyDataSetChanged()
                } ?: run {
                    return
                }
            }

        override fun getItemCount(): Int = items?.size ?: 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubItemViewHolder =
            ClubItemViewHolder(ViewClubListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: ClubItemViewHolder, position: Int) {
            holder.onBind(items?.getOrNull(position))
        }
    }

    inner class ClubItemViewHolder(private val binding: ViewClubListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(txt: String?) {
            binding.tvTitle.text = txt ?: ""
            itemView click {
                it.context.showToast("txt > $txt")
            }
        }
    }
}