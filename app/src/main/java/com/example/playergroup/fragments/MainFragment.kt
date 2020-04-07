package com.example.playergroup.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.example.playergroup.R

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        Log.d("####", "[MainFragment] onActivityCreated")
        super.onActivityCreated(savedInstanceState)
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