package com.example.playergroup.board

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.ActivityExchangeFragmentRxBus
import com.example.playergroup.R
import com.example.playergroup.data.BoardData
import com.example.playergroup.fragments.BaseFragment
import com.example.playergroup.util.click
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_common_board.*

class CommonBoardFragment: BaseFragment() {

    private val mRxBus by lazy { ActivityExchangeFragmentRxBus.getInstance() }

    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_common_board, container, false)

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initBtnView()
        initBoardView()
        initData()
    }

    private fun initBtnView() {
        moveToTop click {
            boardList.scrollToPosition(0)
        }

        boardAdd click {
            val newInstance = BoardInsertPageBottomSheetDialog.newInstance()
            newInstance.show(childFragmentManager, newInstance.tag)
        }
    }

    private fun initData() {
        mRxBus.publisher_loading(true)
        firebaseBoardDB
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result?.toObjects<BoardData>()
                    (boardList.adapter as? BoardListAdapter)?.items = data
                    mRxBus.publisher_loading(false)
                }
                else {
                    Log.d("####", "board api fail")
                }
            }
    }

    private fun initBoardView() {
        boardList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = BoardListAdapter()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val itemIdx =
                        (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
                    showMoveTopBtn(!(itemIdx == 0 || itemIdx == null || adapter?.itemCount == 0))
                }
            })
        }
    }

    private fun showMoveTopBtn(isShow: Boolean) =
        if (isShow) moveToTop.show() else moveToTop.hide()

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }
}