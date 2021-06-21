package com.example.playergroup.ui.login

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.playergroup.ui.login.fragments.JoinPageFragment
import com.example.playergroup.ui.login.fragments.LoginPageFragment
import com.example.playergroup.ui.login.fragments.SearchMemberInfoPageFragment

class JoinLoginAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 3
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> JoinPageFragment.newInstance()
            1 -> LoginPageFragment.newInstance()
            else -> SearchMemberInfoPageFragment.newInstance()
        }
    }
}