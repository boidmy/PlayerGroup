package com.example.playergroup.ui.dialog.adjust

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.R
import com.example.playergroup.data.AdjustDataSet
import com.example.playergroup.databinding.DialogAdjustBinding
import com.example.playergroup.util.ConfigModule
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.click
import com.example.playergroup.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.reflect.TypeToken
import java.lang.reflect.Type
import com.google.gson.Gson

class AdjustBottomSheet: BottomSheetDialogFragment() {

    private var isAdjustMode = false
    private lateinit var currentMenuList: MutableList<AdjustDataSet>

    private val binding by viewBinding(DialogAdjustBinding::bind)
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : SimpleCallback(UP or DOWN or START or END, 0) {

            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                if (isAdjustMode) {
                    val adapter = recyclerView.adapter as AdjustListAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                }
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ACTION_STATE_DRAG && isAdjustMode) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                if (isAdjustMode) viewHolder.itemView.alpha = 1.0f
            }
        }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    companion object {
        fun newInstance(): AdjustBottomSheet =
            AdjustBottomSheet().apply {
                //todo arguments..
            }
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setOnShowListener { setupBottomSheet(it) }
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        bottomSheetDialog.setCanceledOnTouchOutside(true)  // outside Touch Dismiss Disable
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheet?.let {
            it.setBackgroundColor(Color.TRANSPARENT)
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.isDraggable = false
            behavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> { }
                        BottomSheetBehavior.STATE_COLLAPSED -> { dismiss() }
                        BottomSheetBehavior.STATE_HIDDEN -> {}
                        BottomSheetBehavior.STATE_DRAGGING -> {}
                        BottomSheetBehavior.STATE_SETTLING -> {}
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        } ?: run {
            //TODO 예외 처리 ..?
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_adjust, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtnView()
        initRecyclerView()
        getData()
    }

    private fun getData() {
        val json = ConfigModule(requireContext()).adjustMainMenuList
        val type: Type = object : TypeToken<MutableList<AdjustDataSet>>() {}.type
        var list: MutableList<AdjustDataSet> = Gson().fromJson(json, type) ?: mutableListOf<AdjustDataSet>()

        if (list.isNullOrEmpty()) {
            // 데이터가 저장되어 있는게 없으면 디폴트 값은 아래와 같다.
            list = mutableListOf(
                AdjustDataSet(viewType = ViewTypeConst.MAIN_CLUB_INFO, title = "클럽정보", subTitle = "내가 가입한 클럽 정보를 볼 수 있는 곳 입니다."),
                AdjustDataSet(viewType = ViewTypeConst.MAIN_CLUB_PICK_INFO, title = "내 클럽 PICK", subTitle = "내가 관심 있는 클럽 정보를 볼 수 있는 곳 입니다."),
                AdjustDataSet(viewType = ViewTypeConst.MAIN_PICK_LOCATION_INFO, title = "내 장소 PICK", subTitle = "내가 관심 있는 장소 정보를 볼 수 있는 곳 입니다."),
                AdjustDataSet(viewType = ViewTypeConst.MAIN_APP_COMMON_BOARD_INFO, title = "게시판", subTitle = "새로운 게시판 글을 한눈에 볼 수 있습니다.")
            )
        }
        currentMenuList = list.map { it.copy() }.toMutableList()
        getAdapter()?.submitList(list)
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            itemTouchHelper.attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = AdjustListAdapter{
                itemTouchHelper.startDrag(it)
            }
        }
    }

    private fun initBtnView() {
        with(binding) {
            setAdjustModeViewChange(isAdjustMode)  // initSetting
            btnClose click { dismiss() }

            adjustMenu click {
                isAdjustMode = true
                setAdjustModeViewChange(isAdjustMode)
                getAdapter()?.setAdjustMode(isAdjustMode)
            }

            cancel click {
                isAdjustMode = false
                setAdjustModeViewChange(isAdjustMode)
                getAdapter()?.setAdjustMode(isAdjustMode)
                binding.recyclerView.post {
                    getAdapter()?.submitList(currentMenuList)
                }
            }

            save click {
                isAdjustMode = false
                setAdjustModeViewChange(isAdjustMode)
                getAdapter()?.setAdjustMode(isAdjustMode)
                binding.recyclerView.post {
                    currentMenuList = getAdapter()?.currentList?.toMutableList() ?: mutableListOf()
                    ConfigModule(requireContext()).adjustMainMenuList = Gson().toJson(currentMenuList)
                }
            }
        }
    }

    private fun getAdapter() = (binding.recyclerView.adapter as? AdjustListAdapter)

    private fun setAdjustModeViewChange(isState: Boolean) {
        with(binding) {
            adjustMenu.visibility = if (isState) View.GONE else View.VISIBLE
            adjustGroup.visibility = if (isState) View.VISIBLE else View.GONE
        }
    }

}