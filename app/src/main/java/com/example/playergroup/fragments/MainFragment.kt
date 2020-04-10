package com.example.playergroup.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.example.playergroup.MainActivity
import com.example.playergroup.R
import com.example.playergroup.join_login.JoinLoginActivity
import com.example.playergroup.util.click
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment() {

    override fun onAttach(@NonNull context: Context) {
        Log.d("####", "[MainFragment] onAttach")
        super.onAttach(context)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        Log.d("####", "[MainFragment] onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        Log.d("####", "[MainFragment] onActivityCreated")
        super.onActivityCreated(savedInstanceState)

        if (firebaseAuth.currentUser != null) {
            tv_title.text = firebaseAuth.currentUser?.email?: "이메일 정보를 못가져왔음.."
        }

        btn_logout click {
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.signOut()
                (activity as MainActivity).apply {
                    startActivity(Intent(this, JoinLoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        Log.d("####", "[MainFragment] onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("####", "[MainFragment] onPause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d("####", "[MainFragment] onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        Log.d("####", "[MainFragment] onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        Log.d("####", "[MainFragment] onDetach")
        super.onDetach()
    }


}