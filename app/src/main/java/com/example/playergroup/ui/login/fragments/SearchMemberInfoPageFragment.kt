package com.example.playergroup.ui.login.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.example.playergroup.R

class SearchMemberInfoPageFragment: Fragment() {

    companion object {
        fun newInstance() = SearchMemberInfoPageFragment().apply {
            arguments = Bundle()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_searchmemberinfo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun AppCompatEditText.isEditTextEmpty() = this.text?.trim().isNullOrEmpty()

    private fun AppCompatEditText.isEmailPattern() =
        (Patterns.EMAIL_ADDRESS.matcher(this.text?.trim().toString()).matches())
}