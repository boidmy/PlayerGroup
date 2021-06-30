package com.example.playergroup.ui.club

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.R
import com.example.playergroup.custom.AppBarStateChangeListener
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

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.d("####", "ClubMainFragment Scroll Y > $dy")
                    val itemIdx = (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
                    clubViewModel.setShowTopBtn(
                        (!(itemIdx == 0 || itemIdx == null || adapter?.itemCount == 0))
                    )
                }
            })
        }
    }

    private fun initViewModel() {
        clubViewModel.apply {
            firebaseClubDataResult.observe(viewLifecycleOwner, Observer {
                (binding.recyclerView.adapter as? ClubMainListAdapter)?.items = it?.clubMainData
            })

            scrollTopEvent.observe(viewLifecycleOwner, Observer {
                if (it.peekContent()) {
                    binding.recyclerView.scrollToPosition(0)
                }
            })
            setViewTopRoundModeEvent.observe(viewLifecycleOwner, Observer {
                /**
                 * EXPANDED -> true  //Rounded 처리
                 * COLLAPSED -> false //Rounded 제거
                 */
                when(it) {
                    AppBarStateChangeListener.AppBarState.EXPANDED -> {
                        binding.root.setBackgroundColor(0)
                        binding.root.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_top_rounded_corner_14dp)
                    }
                    AppBarStateChangeListener.AppBarState.COLLAPSED -> {
                        binding.root.background = null
                        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.viewOverlayBgColor))
                    }
                }
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