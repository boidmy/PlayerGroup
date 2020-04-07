package com.example.playergroup.join_login

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.playergroup.join_login.fragments.JoinPageFragment
import com.example.playergroup.join_login.fragments.LoginPageFragment
import com.example.playergroup.join_login.fragments.SearchMemberInfoPageFragment

class JoinLoginAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 3
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> JoinPageFragment.newInstance()
            1 -> LoginPageFragment.newInstance()
            else -> SearchMemberInfoPageFragment.newInstance()
        }
    }
}