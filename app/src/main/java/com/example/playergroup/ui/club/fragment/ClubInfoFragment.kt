package com.example.playergroup.ui.club.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentClubInfoBinding
import com.example.playergroup.ui.club.ClubViewModel
import com.example.playergroup.util.click
import com.example.playergroup.util.hideKeyboard
import com.example.playergroup.util.showDefDialog
import com.example.playergroup.util.viewBinding

class ClubInfoFragment: Fragment() {
    private val binding by viewBinding(FragmentClubInfoBinding::bind)
    private val viewModel by activityViewModels<ClubViewModel>()

    companion object {
        fun newInstance() = ClubInfoFragment().apply {
            arguments = Bundle().apply {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_club_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setOnTouchListener { v, event ->
            hideKeyboard(binding.etClubInfo)
            false
        }

        binding.tvMemberCount.text = "클럽 인원 수 : ${(viewModel.mClubInfo.member?.size ?: 0) + 1} 명"

        binding.tvClubAdminName.apply {
            text = "클럽장 : "
            viewModel.getClubAdminUserName {
                text = "클럽장 : $it"
            }
        }

        val isAdmin = viewModel.isCurrentUserClubAdmin.invoke()

        binding.adjust.apply {
            click {
                isEtMode(true)
            }
            visibility = if (isAdmin) View.VISIBLE else View.GONE
        }

        binding.save click {
            if (binding.etClubInfo.text.toString().trim().isEmpty()) {
                requireContext().showDefDialog("빠짐없이 입력 부탁 드립니다..").show()
                return@click
            }
            isEtMode(false)
            viewModel.setUpdateClubDescription(binding.etClubInfo.text.toString())
        }

        binding.cancel click {
            binding.etClubInfo.setText(viewModel.mClubInfo.clubDescription ?: "환영합니다 [ ${viewModel.mClubInfo.clubName ?: ""} ] 클럽 입니다.")
            isEtMode(false)
        }

        binding.etClubInfo.setText(viewModel.mClubInfo.clubDescription ?: "환영합니다 [ ${viewModel.mClubInfo.clubName ?: ""} ] 클럽 입니다.")

        val hint = "예시 )\n" +
                "환영합니다 [ ${viewModel.mClubInfo.clubName ?: ""} ] 클럽 입니다.\n" +
                "저희 클럽은 (지역 이름 or 자주 이용하는 농구장 이름) 에서 활동 하고 있습니다. \n" +
                "일주일에 한번 3시간 6경기 씩 진행 하고 있습니다.\n" +
                "같이 즐농 하면 좋겠습니다.^^"
        binding.etClubInfo.hint = hint
    }

    private fun isEtMode(isState: Boolean) {
        with (binding) {
            if (isState) {
                adjust.visibility = View.GONE
                adjustGroup.visibility = View.VISIBLE
                etLayer.setBackgroundResource(R.drawable.shape_rounded_line)

                etClubInfo.post {
                    etClubInfo.isFocusableInTouchMode = true
                    etClubInfo.requestFocus()
                    val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(etClubInfo, 0)
                }
            } else {
                adjust.visibility = View.VISIBLE
                adjustGroup.visibility = View.GONE
                etLayer.setBackgroundResource(0)
                hideKeyboard(etClubInfo)
            }
            etClubInfo.isEnabled = isState
        }
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard(binding.etClubInfo)
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(binding.etClubInfo)
    }
}