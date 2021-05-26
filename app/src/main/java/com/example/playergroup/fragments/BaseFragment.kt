package com.example.playergroup.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.playergroup.MainActivity
import com.example.playergroup.R
import com.example.playergroup.util.DialogCustom
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.disposables.CompositeDisposable
import java.util.HashMap

open class BaseFragment: Fragment() {

    protected val compositeDisposable = CompositeDisposable()
    protected val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    protected val firebaseDB by lazy { FirebaseFirestore.getInstance() }
    protected val firebaseBoardDB by lazy { firebaseDB.collection("board") }

    protected val mainActivity by lazy { activity as MainActivity }
    protected val mainContainer by lazy { mainActivity.main_container }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    protected fun showDefDialog(context: Context, msg: String): DialogCustom =
        DialogCustom(context)
            .setMessage(msg)
            .setConfirmBtnText(R.string.ok)
            .setDialogCancelable(false)
            .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                override fun onClick(dialogCustom: DialogCustom) {
                    dialogCustom.dismiss()
                }
            })

    // 프로그스 바
    protected fun showProgress(view: View, isShow: Boolean) {
        view.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    protected fun getUserInfo(): Task<DocumentSnapshot> =
        firebaseDB.collection("users").document(firebaseAuth.currentUser?.email.toString()).get()

    protected fun setUserInfo(userInfo: HashMap<String, String>): Task<Void> =
        firebaseDB.collection("users").document(firebaseAuth.currentUser?.email.toString()).set(userInfo)



    /*val user = hashMapOf("email" to firebaseAuth.currentUser!!.email.toString())
    firebaseDB
    .collection("users")
    .document(firebaseAuth.currentUser!!.email.toString())
    .set(user)
    .addOnSuccessListener { documentReference ->
        mRxBus.publisher_loading(false)
        goToMain(this)
    }
    .addOnFailureListener { e ->
        Log.w("####", "Error adding document", e)
        showDefDialog(this, getString(R.string.dialog_alert_msg_error)).show()
        mRxBus.publisher_loading(false)
    }*/
}