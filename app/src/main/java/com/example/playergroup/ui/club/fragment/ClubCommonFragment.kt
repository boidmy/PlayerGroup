package com.example.playergroup.ui.club.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentClubCommonBinding
import com.example.playergroup.ui.dialog.scrollselector.ScrollSelectorBottomSheet
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.viewBinding

class ClubCommonFragment: Fragment() {
    private val binding by viewBinding(FragmentClubCommonBinding::bind)

    companion object {
        const val TAB_TYPE = "tab_type"
        fun newInstance(tabType: ViewTypeConst) = ClubCommonFragment().apply {
            arguments = Bundle().apply {
                putSerializable(TAB_TYPE, tabType)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_club_common, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabType = (arguments?.get(TAB_TYPE) as? ViewTypeConst) ?: ViewTypeConst.SCROLLER_POSITION

        binding.title.text = when(tabType) {
            ViewTypeConst.CLUB_TAB_TYPE_INFO -> "클럽 첫 화면입니다."
            ViewTypeConst.CLUB_TAB_TYPE_MEMBER -> "클럽 멤버들을 보여주는 곳"
            ViewTypeConst.CLUB_TAB_TYPE_PHOTO -> "클럽에서 등록된 사진이나 영상을 보여주는 곳"
            ViewTypeConst.CLUB_TAB_TYPE_SCHEDULER -> "클럽 모임 일정을 보여주는 곳"
            else -> "???????????????????"
        }

    }
}